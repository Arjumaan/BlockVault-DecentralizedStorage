package com.blockvault.service;

import com.blockvault.model.FileMetadata;
import com.blockvault.model.FileVersion;
import com.blockvault.model.User;
import com.blockvault.repository.FileMetadataRepository;
import com.blockvault.repository.FileVersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final FileMetadataRepository fileMetadataRepository;
    private final FileVersionRepository fileVersionRepository;
    private final IPFSService ipfsService;
    private final EncryptionService encryptionService;

    /**
     * Upload file to IPFS with optional encryption
     */
    @Transactional
    public FileMetadata uploadFile(MultipartFile file, User user, boolean encrypt, String encryptionKey)
            throws Exception {
        // Validate file
        if (file.isEmpty()) {
            throw new Exception("File is empty");
        }

        long fileSize = file.getSize();

        // Check storage quota
        if (!user.hasStorageSpace(fileSize)) {
            throw new Exception("Storage quota exceeded. Please upgrade your plan.");
        }

        byte[] fileData = file.getBytes();
        String filename = file.getOriginalFilename();

        // Encrypt if requested
        String actualEncryptionKey = encryptionKey;
        if (encrypt) {
            if (encryptionKey == null || encryptionKey.trim().isEmpty()) {
                // Generate new key if not provided
                actualEncryptionKey = encryptionService.generateKey();
            }

            fileData = encryptionService.encrypt(fileData, actualEncryptionKey);
            log.info("File encrypted: {}", filename);
        }

        // Upload to IPFS
        String cid = ipfsService.uploadFile(fileData, filename);

        // Create file metadata
        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setFilename(filename);
        fileMetadata.setCid(cid);
        fileMetadata.setFileSize(fileSize);
        fileMetadata.setFileType(getFileType(filename));
        fileMetadata.setContentType(file.getContentType());
        fileMetadata.setEncrypted(encrypt);
        fileMetadata.setUser(user);
        fileMetadata.setPinned(true);
        fileMetadata.setReplicationCount(1);
        fileMetadata.setCurrentVersion(1);

        if (encrypt && actualEncryptionKey != null) {
            fileMetadata.setEncryptionKeyHash(encryptionService.hashKey(actualEncryptionKey));
        }

        FileMetadata savedMetadata = fileMetadataRepository.save(fileMetadata);

        // Create initial version
        FileVersion version = new FileVersion();
        version.setFileMetadata(savedMetadata);
        version.setVersionNumber(1);
        version.setCid(cid);
        version.setFileSize(fileSize);
        version.setDescription("Initial upload");
        fileVersionRepository.save(version);

        // Update user storage usage
        user.updateStorageUsage(fileSize);

        log.info("File uploaded successfully: {} (CID: {})", filename, cid);

        // Note: The actual encryption key should be returned to the user in the
        // response
        // so they can save it securely. We don't store the actual key, only its hash.

        return savedMetadata;
    }

    /**
     * Download file from IPFS with optional decryption
     */
    public byte[] downloadFile(String cid, User user, String decryptionKey) throws Exception {
        // Get file metadata
        FileMetadata fileMetadata = fileMetadataRepository.findByCid(cid)
                .orElseThrow(() -> new Exception("File not found"));

        // Check if user owns the file or it's shared
        if (!fileMetadata.getUser().getId().equals(user.getId())) {
            throw new Exception("Access denied");
        }

        // Download from IPFS
        byte[] fileData = ipfsService.downloadFile(cid);

        // Decrypt if encrypted
        if (fileMetadata.getEncrypted()) {
            if (decryptionKey == null || decryptionKey.trim().isEmpty()) {
                throw new Exception("Decryption key required for encrypted file");
            }

            fileData = encryptionService.decrypt(fileData, decryptionKey);
            log.info("File decrypted: {}", fileMetadata.getFilename());
        }

        log.info("File downloaded: {} (CID: {})", fileMetadata.getFilename(), cid);
        return fileData;
    }

    /**
     * Get all files for a user
     */
    public List<FileMetadata> getUserFiles(User user) {
        return fileMetadataRepository.findByUserIdOrderByUploadedAtDesc(user.getId());
    }

    /**
     * Get recent files for dashboard
     */
    public List<FileMetadata> getRecentFiles(User user) {
        return fileMetadataRepository.findTop4ByUserIdOrderByUploadedAtDesc(user.getId());
    }

    /**
     * Get file by ID
     */
    public FileMetadata getFileById(Long id, User user) throws Exception {
        FileMetadata fileMetadata = fileMetadataRepository.findById(id)
                .orElseThrow(() -> new Exception("File not found"));

        if (!fileMetadata.getUser().getId().equals(user.getId())) {
            throw new Exception("Access denied");
        }

        return fileMetadata;
    }

    /**
     * Delete file
     */
    @Transactional
    public void deleteFile(Long id, User user) throws Exception {
        FileMetadata fileMetadata = getFileById(id, user);

        // Unpin from IPFS
        ipfsService.unpinFile(fileMetadata.getCid());

        // Update user storage
        user.updateStorageUsage(-fileMetadata.getFileSize());

        // Delete metadata (versions will be cascaded)
        fileMetadataRepository.delete(fileMetadata);

        log.info("File deleted: {} (CID: {})", fileMetadata.getFilename(), fileMetadata.getCid());
    }

    /**
     * Get file versions
     */
    public List<FileVersion> getFileVersions(Long fileId, User user) throws Exception {
        FileMetadata fileMetadata = getFileById(fileId, user);
        return fileVersionRepository.findByFileMetadataIdOrderByVersionNumberDesc(fileMetadata.getId());
    }

    /**
     * Generate share link (public IPFS gateway)
     */
    public String generateShareLink(Long id, User user) throws Exception {
        FileMetadata fileMetadata = getFileById(id, user);
        String gatewayUrl = "https://ipfs.io/ipfs/";
        return gatewayUrl + fileMetadata.getCid();
    }

    /**
     * Determine file type from filename
     */
    private String getFileType(String filename) {
        if (filename == null)
            return "UNKNOWN";

        String extension = getFileExtension(filename).toLowerCase();

        return switch (extension) {
            case "pdf" -> "PDF";
            case "doc", "docx" -> "DOC";
            case "mp4", "avi", "mov", "mkv" -> "VIDEO";
            case "jpg", "jpeg", "png", "gif", "bmp" -> "IMAGE";
            case "zip", "rar", "7z", "tar", "gz" -> "ARCHIVE";
            case "mp3", "wav", "flac" -> "AUDIO";
            case "txt", "md" -> "TEXT";
            case "xls", "xlsx", "csv" -> "SPREADSHEET";
            case "ppt", "pptx" -> "PRESENTATION";
            default -> "OTHER";
        };
    }

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot + 1) : "";
    }
}

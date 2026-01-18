package com.blockvault.controller;

import com.blockvault.model.FileMetadata;
import com.blockvault.model.FileVersion;
import com.blockvault.model.User;
import com.blockvault.service.AuthService;
import com.blockvault.service.EncryptionService;
import com.blockvault.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FileController {

    private final FileService fileService;
    private final AuthService authService;
    private final EncryptionService encryptionService;

    /**
     * Upload file
     * POST /api/files/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "encrypt", defaultValue = "false") boolean encrypt,
            @RequestParam(value = "encryptionKey", required = false) String encryptionKey,
            Authentication authentication) {

        try {
            User user = authService.getUserByUsername(authentication.getName());

            // Generate new key if encrypting without provided key
            String actualKey = encryptionKey;
            if (encrypt && (encryptionKey == null || encryptionKey.trim().isEmpty())) {
                actualKey = encryptionService.generateKey();
            }

            FileMetadata fileMetadata = fileService.uploadFile(file, user, encrypt, actualKey);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "File uploaded successfully");
            response.put("file", convertToFileResponse(fileMetadata));

            // Return the encryption key if it was generated
            if (encrypt && actualKey != null) {
                response.put("encryptionKey", actualKey);
                response.put("warning", "Save this encryption key securely! It cannot be recovered.");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("File upload failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    /**
     * Get all user files
     * GET /api/files
     */
    @GetMapping
    public ResponseEntity<?> getAllFiles(Authentication authentication) {
        try {
            User user = authService.getUserByUsername(authentication.getName());
            List<FileMetadata> files = fileService.getUserFiles(user);

            List<Map<String, Object>> filesResponse = files.stream()
                    .map(this::convertToFileResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "files", filesResponse));
        } catch (Exception e) {
            log.error("Get files failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    /**
     * Get file by ID
     * GET /api/files/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getFile(@PathVariable Long id, Authentication authentication) {
        try {
            User user = authService.getUserByUsername(authentication.getName());
            FileMetadata fileMetadata = fileService.getFileById(id, user);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "file", convertToFileResponse(fileMetadata)));
        } catch (Exception e) {
            log.error("Get file failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    /**
     * Download file
     * GET /api/files/download/{cid}
     */
    @GetMapping("/download/{cid}")
    public ResponseEntity<?> downloadFile(
            @PathVariable String cid,
            @RequestParam(value = "decryptionKey", required = false) String decryptionKey,
            Authentication authentication) {

        try {
            User user = authService.getUserByUsername(authentication.getName());
            byte[] fileData = fileService.downloadFile(cid, user, decryptionKey);

            // Get file metadata for content type and filename
            FileMetadata metadata = fileService.getUserFiles(user).stream()
                    .filter(f -> f.getCid().equals(cid))
                    .findFirst()
                    .orElse(null);

            String contentType = metadata != null && metadata.getContentType() != null
                    ? metadata.getContentType()
                    : MediaType.APPLICATION_OCTET_STREAM_VALUE;

            String filename = metadata != null ? metadata.getFilename() : "download";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentDispositionFormData("attachment", filename);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileData);

        } catch (Exception e) {
            log.error("File download failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    /**
     * Delete file
     * DELETE /api/files/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable Long id, Authentication authentication) {
        try {
            User user = authService.getUserByUsername(authentication.getName());
            fileService.deleteFile(id, user);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "File deleted successfully"));
        } catch (Exception e) {
            log.error("File deletion failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    /**
     * Generate share link
     * POST /api/files/{id}/share
     */
    @PostMapping("/{id}/share")
    public ResponseEntity<?> shareFile(@PathVariable Long id, Authentication authentication) {
        try {
            User user = authService.getUserByUsername(authentication.getName());
            String shareLink = fileService.generateShareLink(id, user);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "shareLink", shareLink,
                    "message", "Share link generated successfully"));
        } catch (Exception e) {
            log.error("Share link generation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    /**
     * Get file versions
     * GET /api/files/{id}/versions
     */
    @GetMapping("/{id}/versions")
    public ResponseEntity<?> getFileVersions(@PathVariable Long id, Authentication authentication) {
        try {
            User user = authService.getUserByUsername(authentication.getName());
            List<FileVersion> versions = fileService.getFileVersions(id, user);

            List<Map<String, Object>> versionsResponse = versions.stream()
                    .map(this::convertToVersionResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "versions", versionsResponse));
        } catch (Exception e) {
            log.error("Get versions failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    // Helper methods to convert entities to response DTOs

    private Map<String, Object> convertToFileResponse(FileMetadata file) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", file.getId());
        response.put("name", file.getFilename());
        response.put("cid", file.getCid());
        response.put("size", file.getFormattedSize());
        response.put("sizeBytes", file.getFileSize());
        response.put("type", file.getFileType());
        response.put("contentType", file.getContentType());
        response.put("encrypted", file.getEncrypted());
        response.put("icon", file.getFileIcon());
        response.put("date", file.getUploadedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        response.put("uploadedAt", file.getUploadedAt().toString());
        response.put("pinned", file.getPinned());
        response.put("replicationCount", file.getReplicationCount());
        response.put("currentVersion", file.getCurrentVersion());
        response.put("blockchainTxHash", file.getBlockchainTxHash());

        return response;
    }

    private Map<String, Object> convertToVersionResponse(FileVersion version) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", version.getId());
        response.put("versionNumber", version.getVersionNumber());
        response.put("cid", version.getCid());
        response.put("size", formatBytes(version.getFileSize()));
        response.put("createdAt", version.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
        response.put("description", version.getDescription());

        return response;
    }

    private String formatBytes(long bytes) {
        if (bytes >= 1_073_741_824) {
            return String.format("%.2f GB", bytes / 1_073_741_824.0);
        } else if (bytes >= 1_048_576) {
            return String.format("%.2f MB", bytes / 1_048_576.0);
        } else if (bytes >= 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else {
            return bytes + " B";
        }
    }
}

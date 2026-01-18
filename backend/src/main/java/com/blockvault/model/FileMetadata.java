package com.blockvault.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "file_metadata")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false, unique = true)
    private String cid; // IPFS Content Identifier

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private String fileType;

    private String contentType; // MIME type

    @Column(nullable = false)
    private Boolean encrypted = false;

    private String encryptionKeyHash; // Hash of the encryption key (not the key itself)

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String blockchainTxHash; // Optional blockchain transaction hash

    @Column(nullable = false)
    private Boolean pinned = true;

    private Integer replicationCount = 1;

    @OneToMany(mappedBy = "fileMetadata", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileVersion> versions = new ArrayList<>();

    private Integer currentVersion = 1;

    // Helper methods
    public void addVersion(FileVersion version) {
        versions.add(version);
        version.setFileMetadata(this);
    }

    public String getFileIcon() {
        String ext = getFileExtension().toLowerCase();
        return switch (ext) {
            case "pdf" -> "📘";
            case "doc", "docx" -> "📜";
            case "mp4", "avi", "mov", "mkv" -> "🎬";
            case "jpg", "jpeg", "png", "gif", "bmp" -> "🖼️";
            case "zip", "rar", "7z", "tar", "gz" -> "🗄️";
            case "mp3", "wav", "flac" -> "🎵";
            case "txt", "md" -> "📝";
            case "xls", "xlsx", "csv" -> "📊";
            case "ppt", "pptx" -> "📽️";
            default -> "📄";
        };
    }

    public String getFileExtension() {
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot + 1) : "unknown";
    }

    public String getFormattedSize() {
        if (fileSize >= 1_073_741_824) {
            return String.format("%.2f GB", fileSize / 1_073_741_824.0);
        } else if (fileSize >= 1_048_576) {
            return String.format("%.2f MB", fileSize / 1_048_576.0);
        } else if (fileSize >= 1024) {
            return String.format("%.2f KB", fileSize / 1024.0);
        } else {
            return fileSize + " B";
        }
    }
}

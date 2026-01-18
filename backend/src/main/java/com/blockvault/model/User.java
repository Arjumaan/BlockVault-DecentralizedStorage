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
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Long storageQuota = 5368709120L; // 5GB default

    @Column(nullable = false)
    private Long usedStorage = 0L;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileMetadata> files = new ArrayList<>();

    // Helper methods
    public void addFile(FileMetadata file) {
        files.add(file);
        file.setUser(this);
    }

    public void removeFile(FileMetadata file) {
        files.remove(file);
        file.setUser(null);
    }

    public boolean hasStorageSpace(long fileSize) {
        return (usedStorage + fileSize) <= storageQuota;
    }

    public void updateStorageUsage(long delta) {
        this.usedStorage += delta;
        if (this.usedStorage < 0) {
            this.usedStorage = 0L;
        }
    }
}

package com.blockvault.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_versions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_metadata_id", nullable = false)
    private FileMetadata fileMetadata;

    @Column(nullable = false)
    private Integer versionNumber;

    @Column(nullable = false)
    private String cid; // IPFS CID for this version

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private String description;

    @Column(nullable = false)
    private Long fileSize;
}

package com.blockvault.repository;

import com.blockvault.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    
    List<FileMetadata> findByUserId(Long userId);
    
    List<FileMetadata> findByUserIdOrderByUploadedAtDesc(Long userId);
    
    Optional<FileMetadata> findByCid(String cid);
    
    Optional<FileMetadata> findByIdAndUserId(Long id, Long userId);
    
    List<FileMetadata> findTop4ByUserIdOrderByUploadedAtDesc(Long userId);
    
    @Query("SELECT SUM(f.fileSize) FROM FileMetadata f WHERE f.user.id = :userId")
    Long getTotalStorageByUserId(Long userId);
    
    long countByUserId(Long userId);
}

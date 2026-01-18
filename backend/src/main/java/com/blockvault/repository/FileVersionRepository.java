package com.blockvault.repository;

import com.blockvault.model.FileVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileVersionRepository extends JpaRepository<FileVersion, Long> {
    
    List<FileVersion> findByFileMetadataIdOrderByVersionNumberDesc(Long fileMetadataId);
    
    List<FileVersion> findTop4ByFileMetadataIdOrderByVersionNumberDesc(Long fileMetadataId);
}

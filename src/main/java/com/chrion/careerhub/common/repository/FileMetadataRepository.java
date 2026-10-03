package com.chrion.careerhub.common.repository;

import com.chrion.careerhub.common.model.FileMetadata;
import com.chrion.careerhub.common.model.FileType;
import com.chrion.careerhub.common.model.UploadStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface FileMetadataRepository extends MongoRepository<FileMetadata, UUID> {


    Optional<FileMetadata> findByIdAndUserIdAndUploadStatus(UUID id, UUID userId, UploadStatus uploadStatus);

    Optional<FileMetadata> findByS3Key(String s3Key);

    Optional<FileMetadata> findByUserIdAndS3Key(
            UUID userId,
            String s3Key
    );

    Optional<FileMetadata> findByIdAndUploadStatus(UUID id, UploadStatus uploadStatus);

    @Query("""
    {
        'userId' : ?0,
        'originalFilename' : ?1,
        'contentType' : ?2,
        'fileSize' : ?3,
        'fileType' : ?4,
        'uploadStatus' : ?5 ,
        'expireAt' : { $gt : ?6 }
    }
    """)
    Optional<FileMetadata> findExactMatch(
            UUID userId,
            String originalFilename,
            String contentType,
            Long fileSize,
            FileType fileType,
            UploadStatus uploadStatus,
            Instant expireAt
    );
}

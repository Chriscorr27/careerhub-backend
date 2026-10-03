package com.chrion.careerhub.common.model;

import com.chrion.careerhub.job.model.JobStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "file_metadata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileMetadata {

    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Indexed
    private UUID userId;

    private String s3Key;

    private String watermarkS3Key;

    private String originalFilename;

    private String waterMarkFilename;

    private String contentType;

    private Long fileSize;

    private FileType fileType;

    @CreatedDate
    private Instant createdAt;

    private Instant uploadedAt;

    private Instant expireAt;

    @Builder.Default
    private UploadStatus uploadStatus = UploadStatus.PENDING;

    private String preSignedUrl;

    public String getWatermarkS3Key(){
        return watermarkS3Key!=null?watermarkS3Key:s3Key;
    }

}

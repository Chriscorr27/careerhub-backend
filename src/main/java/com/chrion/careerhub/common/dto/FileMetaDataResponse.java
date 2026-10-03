package com.chrion.careerhub.common.dto;

import com.chrion.careerhub.common.model.FileType;
import com.chrion.careerhub.common.model.UploadStatus;

import java.time.Instant;
import java.util.UUID;

public record FileMetaDataResponse(
    UUID fileId,
    UUID userID,
    String s3Key,
    String watermarkS3Key,
    String originalFilename,
    String waterMarkFilename,
    String contentType,
    Long fileSize,
    FileType fileType,
    Instant updatedAt,
    Instant expireAt,
    UploadStatus uploadStatus,
    String preSignedUrl
) { }

package com.chrion.careerhub.common.dto;

import com.chrion.careerhub.common.model.FileType;

public record UploadFileRequest(
    String originalFilename,
    String contentType,
    Long fileSize,
    FileType fileType
) { }

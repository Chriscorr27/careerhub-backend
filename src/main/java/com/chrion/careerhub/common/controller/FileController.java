package com.chrion.careerhub.common.controller;

import com.chrion.careerhub.common.dto.FileMetaDataResponse;
import com.chrion.careerhub.common.dto.PreSignedUrlResponse;
import com.chrion.careerhub.common.dto.UploadFileRequest;
import com.chrion.careerhub.common.dto.UploadedFileRequest;
import com.chrion.careerhub.common.model.FileType;
import com.chrion.careerhub.common.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping(value = "/upload-presign-url")
    @ResponseStatus(HttpStatus.CREATED)
    public FileMetaDataResponse getUploadPresignUrl(
            @RequestBody UploadFileRequest request
    ) throws IOException {

        return fileStorageService.uploadFilePreSignedUrl(request);

    }

    @PatchMapping(value = "/file-uploaded")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasRole('AWS')")
    public FileMetaDataResponse fileUploaded(
            @RequestHeader("X-AWS-RequestId") String requestId,
            @RequestHeader("X-AWS-Timestamp") String timestamp,
            @RequestHeader("X-AWS-Signature") String signature,
            @RequestBody UploadedFileRequest request
    ) throws IOException {

        return fileStorageService.isUploadedFile(
                requestId,
                timestamp,
                signature,
                request.s3Key()
        );

    }

    @GetMapping("/{fileId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('USER', 'HR')")
    public FileMetaDataResponse getFile(@PathVariable("fileId") UUID fileId) throws Exception {
        return fileStorageService.getFileMetaData(fileId);
    }

    @GetMapping("/{fileId}/presigned-url")
    @ResponseStatus(HttpStatus.OK)
    public PreSignedUrlResponse getPresignedUrl(@PathVariable UUID fileId) throws IOException {
        return fileStorageService.getPreSignedUrl(fileId);
    }

    @GetMapping("/{fileId}/watermark-presigned-url")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public PreSignedUrlResponse getWatermarkPresignedUrl(@PathVariable UUID fileId) throws IOException {

        return fileStorageService.getWatermarkPreSignedUrl(fileId);
    }
}

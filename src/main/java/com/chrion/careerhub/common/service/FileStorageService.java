package com.chrion.careerhub.common.service;

import com.chrion.careerhub.auth.service.AuthService;
import com.chrion.careerhub.common.dto.FileMetaDataResponse;
import com.chrion.careerhub.common.dto.PreSignedUrlResponse;
import com.chrion.careerhub.common.dto.UploadFileRequest;
import com.chrion.careerhub.common.exception.CustomException;
import com.chrion.careerhub.common.model.FileMetadata;
import com.chrion.careerhub.common.model.FileType;
import com.chrion.careerhub.common.model.UploadStatus;
import com.chrion.careerhub.common.repository.FileMetadataRepository;
import com.chrion.careerhub.common.util.S3KeyGenerator;
import com.chrion.careerhub.common.validator.FileValidator;
import com.chrion.careerhub.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final S3Service s3Service;
    private final FileValidator fileValidator;
    private final FileMetadataRepository fileMetadataRepository;
    private final AuthService authService;

    public FileMetaDataResponse uploadFilePreSignedUrl(UploadFileRequest request) throws IOException {
        log.info("getting upload presigned url for {} file", request.fileType());
        UUID userId = authService.getCurrentAuthenticatedUserId();
        Instant now = Instant.now();
        FileMetadata fileMetadata = fileMetadataRepository.findExactMatch(
                userId,
                request.originalFilename(),
                request.contentType(),
                request.fileSize(),
                request.fileType(),
                UploadStatus.PENDING,
                now
        ).orElse(null);
        if(fileMetadata != null){
            return toFileMetaDataResponse(fileMetadata);
        }
        String extension = "";

        if (request.originalFilename() != null && request.originalFilename().contains(".")) {
            extension = request.originalFilename().substring(request.originalFilename().lastIndexOf("."));
        }
        String key = S3KeyGenerator.generateKey(userId, extension, request.fileType());
        String waterMarkFilename = request.originalFilename();
        String watermarkS3Key = key;
        assert request.originalFilename() != null;
        if(!request.originalFilename().endsWith(".pdf")){
            waterMarkFilename = request.originalFilename().replace(extension, ".pdf");
            watermarkS3Key = key.replace(extension, ".pdf");
        }
        fileMetadata = FileMetadata.builder()
                .userId(userId)
                .s3Key(key)
                .watermarkS3Key(watermarkS3Key)
                .originalFilename(request.originalFilename())
                .waterMarkFilename(waterMarkFilename)
                .contentType(request.contentType())
                .fileSize(request.fileSize())
                .fileType(request.fileType())
                .build();
        fileValidator.validateFileMetaData(fileMetadata);
        s3Service.generatePutPresignedUrl(fileMetadata);
        FileMetadata savedFileMetaData = fileMetadataRepository.save(fileMetadata);
        return toFileMetaDataResponse(savedFileMetaData);
    }

    public FileMetaDataResponse isUploadedFile(
            String requestId,
            String timestamp,
            String signature,
            String s3Key
    ){
        log.info("changing uploaded status to UPLOADED s3Key: {}", s3Key);

        String messageToVerify = requestId + "." + timestamp + "." + s3Key;

        if(!s3Service.verifyHmacSignature(messageToVerify, signature)){
            throw new CustomException("Invalid signature", HttpStatus.UNAUTHORIZED);
        }

        FileMetadata fileMetadata = fileMetadataRepository.findByS3Key(s3Key)
                .orElseThrow(
                    ()-> new CustomException("File Not Found", HttpStatus.NOT_FOUND)
                );

        fileMetadata.setUploadedAt(Instant.now());
        fileMetadata.setUploadStatus(UploadStatus.UPLOADED);
        fileMetadata.setPreSignedUrl(null);
        fileMetadata.setExpireAt(null);

        FileMetadata savedFileMetaData = fileMetadataRepository.save(fileMetadata);

        return toFileMetaDataResponse(savedFileMetaData);
    }

    public FileMetaDataResponse uploadFile(MultipartFile file, FileType fileType) throws IOException {
        log.info("Uploading {} file", fileType);
        UUID userId = authService.getCurrentAuthenticatedUserId();
        String originalFilename = file.getOriginalFilename();

        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        fileValidator.validate(file, fileType);

        String key = S3KeyGenerator.generateKey(userId, extension, fileType);

        s3Service.uploadFile(
                key,
                file.getBytes(),
                file.getContentType()
        );

        String waterMarkFilename = originalFilename;
        String watermarkS3Key = key;
        assert originalFilename != null;
        if(!originalFilename.endsWith(".pdf")){
            waterMarkFilename = originalFilename.replace(extension, ".pdf");
            watermarkS3Key = key.replace(extension, ".pdf");
        }

        FileMetadata metadata = FileMetadata.builder()
                .userId(userId)
                .s3Key(key)
                .watermarkS3Key(watermarkS3Key)
                .originalFilename(originalFilename)
                .waterMarkFilename(waterMarkFilename)
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .fileType(fileType)
                .build();

        FileMetadata fileMetadata = fileMetadataRepository.save(metadata);

        return toFileMetaDataResponse(fileMetadata);
    }

    public PreSignedUrlResponse getPreSignedUrl(UUID fileId){
        log.info("Generating presigned URL for file {}", fileId);
        UUID userId = authService.getCurrentAuthenticatedUserId();
        FileMetadata metadata = fileMetadataRepository
                .findByIdAndUserIdAndUploadStatus(fileId, userId, UploadStatus.UPLOADED)
                .orElseThrow(
                        () -> new CustomException("File not found", HttpStatus.NOT_FOUND)
                );
        return s3Service.generatePresignedUrl(metadata.getS3Key());
    }

    public PreSignedUrlResponse getWatermarkPreSignedUrl(UUID fileId){
        log.info("Generating Watermark presigned URL for file {}", fileId);
        FileMetadata metadata = fileMetadataRepository
                .findByIdAndUploadStatus(fileId, UploadStatus.UPLOADED)
                .orElseThrow(
                        () -> new CustomException("File not found", HttpStatus.NOT_FOUND)
                );
        return s3Service.generateWatermarkPresignedUrl(metadata.getWatermarkS3Key());
    }

    public FileMetaDataResponse getFileMetaData(UUID fileId) throws Exception {
        log.info("Fetching metadata for file {}", fileId);
        User user = authService.getCurrentAuthenticatedUser();
        FileMetadata metadata = null;
        if(user.getRole() == User.Role.HR){
            metadata = fileMetadataRepository
                .findByIdAndUploadStatus(fileId, UploadStatus.UPLOADED)
                .orElseThrow(
                        () -> new CustomException("File not found", HttpStatus.NOT_FOUND)
                );
        }else{
            metadata = fileMetadataRepository
                    .findByIdAndUserIdAndUploadStatus(fileId, user.getId(), UploadStatus.UPLOADED)
                    .orElseThrow(
                            () -> new CustomException("File not found", HttpStatus.NOT_FOUND)
                    );
        }

        return toFileMetaDataResponse(metadata);
    }

    private FileMetaDataResponse toFileMetaDataResponse(FileMetadata fileMetadata){
        return new FileMetaDataResponse(
            fileMetadata.getId(),
            fileMetadata.getUserId(),
            fileMetadata.getS3Key(),
            fileMetadata.getWatermarkS3Key()!=null?
                fileMetadata.getWatermarkS3Key():
                fileMetadata.getS3Key(),
            fileMetadata.getOriginalFilename(),
            fileMetadata.getWaterMarkFilename()!=null?
                    fileMetadata.getWaterMarkFilename():
                    fileMetadata.getOriginalFilename(),
            fileMetadata.getContentType(),
            fileMetadata.getFileSize(),
            fileMetadata.getFileType(),
            fileMetadata.getUploadedAt(),
            fileMetadata.getExpireAt(),
            fileMetadata.getUploadStatus(),
            fileMetadata.getPreSignedUrl()
        );
    }
}

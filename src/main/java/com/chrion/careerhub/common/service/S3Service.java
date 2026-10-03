package com.chrion.careerhub.common.service;

import com.chrion.careerhub.common.dto.PreSignedUrlResponse;
import com.chrion.careerhub.common.model.FileMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.files-bucket-name}")
    private String filesBucketName;

    @Value("${aws.s3.watermark-files-bucket-name}")
    private String watermarkFilesBucketName;

    @Value("${aws.s3.profiles-bucket-name}")
    private String profilesBucketName;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.get-object-duration.minutes}")
    private long getObjectDurationMinutes;

    @Value("${aws.s3.put-object-duration.minutes}")
    private long putObjectDurationMinutes;

    @Value("${aws.secret}")
    private String secret;

    @Value("${aws.hashing.algorithm}")
    private String AWS_HASHING_ALGORITHM;

    public void uploadFile(String key, byte[] content, String contentType) {
        log.info("Uploading file to S3 with key {}", key);

        PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(filesBucketName)
                        .key(key)
                        .contentType(contentType)
                        .build();

        s3Client.putObject(request, RequestBody.fromBytes(content));

    }

    public void uploadProfile(String key, byte[] content, String contentType) {
        log.info("Uploading profile pic to S3 with key {}", key);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(profilesBucketName)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(content));

    }

    public void deleteFile(String key) {
        log.info("Deleting file from S3 with key {}", key);

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                        .bucket(filesBucketName)
                        .key(key)
                        .build();

        s3Client.deleteObject(request);
    }

    public void deleteProfileFile(String key) {
        log.info("Deleting Profile file from S3 with key {}", key);

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(profilesBucketName)
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }

    public void generatePutPresignedUrl(FileMetadata fileMetadata) {
        log.info("Generating put presigned S3 URL for key {}", fileMetadata.getS3Key());
        Duration expiration = Duration.ofMinutes(putObjectDurationMinutes);

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(filesBucketName)
                .key(fileMetadata.getS3Key())
                .contentType(fileMetadata.getContentType())
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        Instant expireTime = Instant.now().plus(putObjectDurationMinutes, ChronoUnit.MINUTES);
        fileMetadata.setExpireAt(expireTime);
        fileMetadata.setPreSignedUrl(presignedRequest.url().toString());
    }

    public PreSignedUrlResponse generatePresignedUrl(String key, String bucketName) {
        log.info("Generating presigned S3 URL for key {}", key);
        Duration expiration = Duration.ofMinutes(getObjectDurationMinutes);
        GetObjectRequest getObjectRequest =
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                        .signatureDuration(expiration)
                        .getObjectRequest(getObjectRequest)
                        .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

        return new PreSignedUrlResponse(
                presignedRequest.url().toString(),
                expiration
        );
    }

    public PreSignedUrlResponse generatePresignedUrl(String key){
        return generatePresignedUrl(key, filesBucketName);
    }

    public PreSignedUrlResponse generateWatermarkPresignedUrl(String key){
        return generatePresignedUrl(key, watermarkFilesBucketName);
    }

    public boolean verifyHmacSignature(String message, String providedSignature) {
        try {
            Mac mac = Mac.getInstance(AWS_HASHING_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8),
                    AWS_HASHING_ALGORITHM
            );
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));

            // Convert byte array to Hex String (Java 17+)
            String computedSignature = HexFormat.of().formatHex(hash);

            // CRITICAL: Use MessageDigest.isEqual to prevent Timing Attacks
            return MessageDigest.isEqual(
                    computedSignature.getBytes(StandardCharsets.UTF_8),
                    providedSignature.getBytes(StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            return false;
        }
    }
}

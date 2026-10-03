package com.chrion.careerhub.common.util;

import com.chrion.careerhub.common.model.FileType;

import java.util.UUID;

public final class S3KeyGenerator {

    private S3KeyGenerator() {}

    public static String resumeKey(UUID userId, String extension) {
        return "resumes/"
                + userId
                + "/"
                + UUID.randomUUID()
                + extension;
    }

    public static String profileKey(UUID userId, String extension) {
        return "profile/"
                + userId
                + "/"
                + UUID.randomUUID()
                + extension;
    }

    public static String documentKey(UUID userId, String extension) {
        return "documents/"
                + userId
                + "/"
                + UUID.randomUUID()
                + extension;
    }

    public static String generateKey(UUID userId, String extension, FileType fileType) {
        return switch (fileType) {
            case RESUME -> resumeKey(userId, extension);
            case PROFILE -> profileKey(userId, extension);
            case DOCUMENT -> documentKey(userId, extension);
        };
    }
}

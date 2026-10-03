package com.chrion.careerhub.user.service;

import com.chrion.careerhub.common.exception.CustomException;
import com.chrion.careerhub.common.model.FileType;
import com.chrion.careerhub.common.service.S3Service;
import com.chrion.careerhub.common.util.S3KeyGenerator;
import com.chrion.careerhub.common.validator.FileValidator;
import com.chrion.careerhub.user.dto.UpdateUserProfileRequest;
import com.chrion.careerhub.user.dto.UserProfileResponse;
import com.chrion.careerhub.user.model.User;
import com.chrion.careerhub.user.model.UserProfile;
import com.chrion.careerhub.user.model.UserProfileFileMetaData;
import com.chrion.careerhub.user.repository.UserProfileFileMetaDataRepository;
import com.chrion.careerhub.user.repository.UserProfileRepository;
import com.chrion.careerhub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final S3Service s3Service;
    private final FileValidator fileValidator;
    private final UserProfileFileMetaDataRepository userProfileFileMetaDataRepository;

    @Value("${aws.cloud-front.url}")
    private String cloudFrontUrl;

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(UUID userId) {
        log.info("Fetching profile for user {}", userId);
        User user = findUser(userId);
        UserProfile profile = userProfileRepository
                .findByUserId(userId)
                .orElseGet(() -> createEmptyProfile(user));
        return UserProfileResponse.from(profile, cloudFrontUrl);
    }

    @Transactional
    public UserProfileResponse updateProfile(UUID userId, UpdateUserProfileRequest request) {
        log.info("Updating profile for user {}", userId);
        User user = findUser(userId);
        UserProfile profile = userProfileRepository
                .findByUserId(userId)
                .orElseGet(() -> createEmptyProfile(user));

        profile.setName(request.name());
        profile.setPhone(request.phone());
        profile.setLocation(request.location());
        profile.setCurrentRole(request.currentRole());
        profile.setExperienceYears(request.experienceYears());
        profile.setLinkedinUrl(request.linkedinUrl());
        profile.setGithubUrl(request.githubUrl());
        profile.setPortfolioUrl(request.portfolioUrl());

        UserProfile savedProfile = userProfileRepository.save(profile);
        return UserProfileResponse.from(savedProfile, cloudFrontUrl);
    }

    @Transactional
    public UserProfileResponse uploadProfileFile(UUID userId, MultipartFile file) throws IOException {
        User user = findUser(userId);
        UserProfile profile = userProfileRepository
                .findByUserId(userId)
                .orElseGet(() -> createEmptyProfile(user));
        if(profile.getProfileMetaData() != null){
            userProfileFileMetaDataRepository.findById(
                    profile.getProfileMetaData().getId()
            ).ifPresent(userProfileFileMetaData -> {
                s3Service.deleteProfileFile(userProfileFileMetaData.getS3Key());
                userProfileFileMetaDataRepository.delete(userProfileFileMetaData);
            });
        }
        log.info("Uploading profile file");

        String originalFilename = file.getOriginalFilename();

        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        fileValidator.validate(file, FileType.PROFILE);

        String key = S3KeyGenerator.generateKey(userId, extension, FileType.PROFILE);

        s3Service.uploadProfile(
                key,
                file.getBytes(),
                file.getContentType()
        );

        UserProfileFileMetaData fileMetadata = UserProfileFileMetaData.builder()
                .s3Key(key)
                .originalFilename(originalFilename)
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .build();

        fileMetadata = userProfileFileMetaDataRepository.save(fileMetadata);

        profile.setProfileMetaData(fileMetadata);
        UserProfile savedProfile = userProfileRepository.save(profile);
        return UserProfileResponse.from(savedProfile, cloudFrontUrl);
    }

    private UserProfile createEmptyProfile(User user) {
        UserProfile profile = UserProfile.builder()
                .user(user)
                .build();
        return userProfileRepository.save(profile);
    }

    private User findUser(UUID userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
    }
}
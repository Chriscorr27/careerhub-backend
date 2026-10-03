package com.chrion.careerhub.user.dto;

import com.chrion.careerhub.user.model.UserProfile;
import com.chrion.careerhub.user.model.UserProfileFileMetaData;

import java.math.BigDecimal;
import java.util.UUID;

public record UserProfileResponse(
        UUID userId,
        String email,
        String name,
        String phone,
        String location,
        String currentRole,
        BigDecimal experienceYears,
        String linkedinUrl,
        String githubUrl,
        String portfolioUrl,
        String profileUrl,
        UserProfileFileMetaData profileMetaData
) {
    public static UserProfileResponse from(UserProfile profile, String cloudFrontUrl) {
        String profileUrl = profile.getProfileMetaData()!=null?
                cloudFrontUrl+profile.getProfileMetaData().getS3Key()+"?w=200"
                : null;
        return new UserProfileResponse(
                profile.getUser().getId(),
                profile.getUser().getEmail(),
                profile.getName(),
                profile.getPhone(),
                profile.getLocation(),
                profile.getCurrentRole(),
                profile.getExperienceYears(),
                profile.getLinkedinUrl(),
                profile.getGithubUrl(),
                profile.getPortfolioUrl(),
                profileUrl,
                profile.getProfileMetaData()
        );
    }
}

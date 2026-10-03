package com.chrion.careerhub.auth.service;

import com.chrion.careerhub.auth.model.RefreshToken;
import com.chrion.careerhub.auth.repository.RefreshTokenRepository;
import com.chrion.careerhub.common.exception.CustomException;
import com.chrion.careerhub.config.JwtProperties;
import com.chrion.careerhub.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public String createRefreshToken(User user) {
        log.info("Creating refresh token for user {}", user.getId());

        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        String tokenHash = hashToken(rawToken);

        Instant expiresAt = Instant.now()
                .plusSeconds(
                        jwtProperties.refreshTokenExpirationDays()
                                * 24
                                * 60
                                * 60
                );

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiresAt(expiresAt)
                .build();

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    @Transactional
    public RefreshToken validateToken(String rawToken) {
        log.info("Validating refresh token");

        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new CustomException("Invalid refresh token", HttpStatus.UNAUTHORIZED)
                        );

        if (!refreshToken.isValid()) {
            throw new CustomException("Refresh token expired or revoked", HttpStatus.UNAUTHORIZED);
        }

        if (refreshToken.getUser().getStatus() != User.UserStatus.ACTIVE) {

            throw new CustomException("User account is not active", HttpStatus.UNAUTHORIZED);
        }

        return refreshToken;
    }

    @Transactional
    public void revoke(RefreshToken refreshToken) {
        log.info("Revoking refresh token");
        refreshTokenRepository.delete(refreshToken);
    }

    private String hashToken(String token) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            token.getBytes(StandardCharsets.UTF_8)
                    );

            return Base64.getEncoder()
                    .encodeToString(hash);

        } catch (NoSuchAlgorithmException exception) {

            throw new IllegalStateException(
                    "SHA-256 algorithm not available",
                    exception
            );
        }
    }
}

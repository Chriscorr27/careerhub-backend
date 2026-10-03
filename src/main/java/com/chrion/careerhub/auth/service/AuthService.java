package com.chrion.careerhub.auth.service;

import com.chrion.careerhub.auth.dto.LoginRequest;
import com.chrion.careerhub.auth.dto.RegisterRequest;
import com.chrion.careerhub.auth.dto.RegisterResponse;
import com.chrion.careerhub.auth.dto.TokenResponse;
import com.chrion.careerhub.auth.dto.*;
import com.chrion.careerhub.common.exception.CustomException;
import com.chrion.careerhub.config.JwtProperties;
import com.chrion.careerhub.common.exception.EmailAlreadyExistsException;
import com.chrion.careerhub.user.model.User;
import com.chrion.careerhub.user.model.UserProfile;
import com.chrion.careerhub.user.repository.UserProfileRepository;
import com.chrion.careerhub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public RegisterResponse register(RegisterRequest request, User.Role role) {
        log.info("Registering user");

        String email = request.email()
                .trim()
                .toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        User user = User.builder()
                .email(email)
                .passwordHash(
                        passwordEncoder.encode(request.password())
                )
                .role(role)
                .status(User.UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);
        UserProfile userProfile = UserProfile.builder()
                .user(user)
                .build();
        userProfileRepository.save(userProfile);
        return RegisterResponse.from(savedUser);
    }

    @Transactional(readOnly = true)
    private User validateAndLoginUser(LoginRequest request){

        String email = normalizeEmail(request.email());

        User user = userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid email or password")
                );

        if (!passwordEncoder.matches(
                request.password(),
                user.getPasswordHash())) {

            throw new BadCredentialsException(
                    "Invalid email or password"
            );
        }

        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new BadCredentialsException(
                    "User account is not active"
            );
        }
        return user;
    }

    @Transactional
    public TokenResponse awsLogin(LoginRequest request) {
        log.info("Logging in AWS user");
        User user = validateAndLoginUser(request);

        if(user.getRole() != User.Role.AWS){
            throw new CustomException("Invalid AWS Account", HttpStatus.BAD_REQUEST);
        }

        String accessToken = jwtService.generateAwsAccessToken(user);

        long expiresIn = jwtProperties.awsAccessTokenExpirationMinutes() * 60;

        return new TokenResponse(
                accessToken,
                null,
                "Bearer",
                expiresIn
        );
    }


    @Transactional
    public TokenResponse login(LoginRequest request) {
        log.info("Logging in user");
        User user = validateAndLoginUser(request);

        if(user.getRole() == User.Role.AWS){
            throw new CustomException("Invalid Account details", HttpStatus.BAD_REQUEST);
        }

        String accessToken = jwtService.generateAccessToken(user);

        String refreshToken = refreshTokenService.createRefreshToken(user);

        long expiresIn = jwtProperties.accessTokenExpirationMinutes() * 60;

        return new TokenResponse(
                accessToken,
                refreshToken,
                "Bearer",
                expiresIn
        );
    }

    public User getCurrentAuthenticatedUser() throws Exception{
        log.info("Fetching current authenticated user");
        UUID userId = getCurrentAuthenticatedUserId();
        return getUser(userId);
    }

    @Transactional(readOnly = true)
    public User getUser(UUID userId) throws Exception{
        log.info("Fetching user");
        return userRepository.findById(userId)
                .orElseThrow(
                        () -> new CustomException("User Not found.", HttpStatus.NOT_FOUND)
                );
    }

    public UUID getCurrentAuthenticatedUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException("No authenticated user found in context", HttpStatus.UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();

        return ((UUID) principal);
    }

    @Transactional
    public TokenResponse refresh(String rawRefreshToken) {
        log.info("Refreshing authentication token");

        var storedToken = refreshTokenService.validateToken(rawRefreshToken);

        User user = storedToken.getUser();

        // Rotate refresh token
        refreshTokenService.revoke(storedToken);

        String newAccessToken = jwtService.generateAccessToken(user);

        String newRefreshToken = refreshTokenService.createRefreshToken(user);

        long expiresIn = jwtProperties.accessTokenExpirationMinutes() * 60;

        return new TokenResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                expiresIn
        );
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        log.info("Logging out user");
        UUID userId = getCurrentAuthenticatedUserId();
        var storedToken = refreshTokenService.validateToken(rawRefreshToken);
        User user = storedToken.getUser();
        if(!user.getId().equals(userId)){
            throw new CustomException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }
        refreshTokenService.revoke(storedToken);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}

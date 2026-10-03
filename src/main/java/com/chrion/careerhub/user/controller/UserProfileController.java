package com.chrion.careerhub.user.controller;

import com.chrion.careerhub.auth.service.AuthService;
import com.chrion.careerhub.common.model.FileType;
import com.chrion.careerhub.user.dto.UpdateUserProfileRequest;
import com.chrion.careerhub.user.dto.UserProfileResponse;
import com.chrion.careerhub.user.service.UserProfileService;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/profile")
@RequiredArgsConstructor
public class UserProfileController {
    private final AuthService authService;
    private final UserProfileService userProfileService;

    @GetMapping
    public ResponseEntity<UserProfileResponse> getProfile() {
        UUID userId = authService.getCurrentAuthenticatedUserId();
        return ResponseEntity.ok(userProfileService.getProfile(userId));
    }

    @PutMapping
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateUserProfileRequest request) {

        UUID userId = authService.getCurrentAuthenticatedUserId();
        return ResponseEntity.ok(userProfileService.updateProfile(userId, request));
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public UserProfileResponse uploadResume(
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        UUID userId = authService.getCurrentAuthenticatedUserId();
        return userProfileService.uploadProfileFile(userId,file);

    }

}

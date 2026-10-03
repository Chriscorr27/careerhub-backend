package com.chrion.careerhub.auth.dto;

import com.chrion.careerhub.user.model.User;

import java.util.UUID;

public record RegisterResponse(
        UUID userId,
        String email,
        String role,
        String status
) {

    public static RegisterResponse from(User user) {
        return new RegisterResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                user.getStatus().name()
        );
    }
}
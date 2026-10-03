package com.chrion.careerhub.auth.controller;

import com.chrion.careerhub.auth.dto.LogoutRequest;
import com.chrion.careerhub.auth.service.AuthService;
import com.chrion.careerhub.user.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    @GetMapping("/me")
    public User getCurrentUser() throws Exception {
        return authService.getCurrentAuthenticatedUser();
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(
            @Valid @RequestBody LogoutRequest request) {

        authService.logout(request.refreshToken());
    }
}

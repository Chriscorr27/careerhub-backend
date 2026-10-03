package com.chrion.careerhub.auth.controller;

import com.chrion.careerhub.auth.dto.*;
import com.chrion.careerhub.auth.dto.*;
import com.chrion.careerhub.auth.service.AuthService;
import com.chrion.careerhub.user.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        RegisterResponse response = authService.register(request, User.Role.USER);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/register-hr")
    public ResponseEntity<RegisterResponse> registerHr(
            @Valid @RequestBody RegisterRequest request
    ) {

        RegisterResponse response = authService.register(request, User.Role.HR);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/register-aws")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RegisterResponse> registerAws(
            @Valid @RequestBody RegisterRequest request
    ) {

        RegisterResponse response = authService.register(request, User.Role.AWS);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest request) {

        TokenResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login-aws")
    public ResponseEntity<TokenResponse> loginAws(
            @Valid @RequestBody LoginRequest request) {

        TokenResponse response = authService.awsLogin(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {

        return ResponseEntity.ok(
                authService.refresh(
                        request.refreshToken()
                )
        );
    }
}
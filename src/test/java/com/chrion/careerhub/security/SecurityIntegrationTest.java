package com.chrion.careerhub.security;

import com.chrion.careerhub.auth.dto.LoginRequest;
import com.chrion.careerhub.integration.BaseIntegrationTest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
class SecurityIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void protectedEndpoint_withoutToken_shouldReturn401() throws Exception {

        mockMvc.perform(
                        get("/api/v1/dashboard/summary")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_withInvalidToken_shouldReturn401() throws Exception {

        mockMvc.perform(
                    get("/api/v1/dashboard/summary")
                            .header(
                                    HttpHeaders.AUTHORIZATION,
                                    "Bearer invalid-token"
                            )
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unprotectedEndpoint_login_shouldReturn200() throws Exception {
        LoginRequest request = new LoginRequest(
                mockUser.getEmail(),
                password
        );
        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk());
    }

    @Test
    void protectedEndpoint_withValidToken_shouldReturn200() throws Exception {

        mockMvc.perform(
            get("/api/v1/users/me")
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer "+jwtToken
                )
        )
        .andExpect(status().isOk());
    }

    @Test
    void protectedEndpoint_withTamperedToken_shouldReturn401() throws Exception {

        String tamperedToken = jwtToken + "tampered";

        mockMvc.perform(
            get("/api/v1/users/me")
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + tamperedToken
                )
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_withExpiredToken_shouldReturn401() throws Exception {

        SecretKey signingKey = Keys.hmacShaKeyFor(
                jwtProperties.secret()
                        .getBytes(StandardCharsets.UTF_8)
        );

        Instant now = Instant.now();

        String expiredToken = Jwts.builder()
                .subject(mockUser.getId().toString())
                .claim("email", mockUser.getEmail())
                .claim("role", mockUser.getRole().name())
                .issuedAt(Date.from(now.minusSeconds(120)))
                .expiration(Date.from(now.minusSeconds(60)))
                .signWith(signingKey)
                .compact();

        mockMvc.perform(
            get("/api/v1/users/me")
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + expiredToken
                )
        )
        .andExpect(status().isUnauthorized());
    }
}

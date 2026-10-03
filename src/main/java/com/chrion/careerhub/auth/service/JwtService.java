package com.chrion.careerhub.auth.service;

import com.chrion.careerhub.config.JwtProperties;
import com.chrion.careerhub.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;

        this.signingKey = Keys.hmacShaKeyFor(
                jwtProperties.secret().getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateToken(User user, long expirationTime) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(expirationTime * 60);
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(signingKey)
                .compact();
    }

    public String generateAccessToken(User user) {
        return generateToken(user, jwtProperties.accessTokenExpirationMinutes());
    }

    public String generateAwsAccessToken(User user) {
        return generateToken(user, jwtProperties.awsAccessTokenExpirationMinutes());
    }

    public Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUserId(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {

        try {
            return !extractClaims(token)
                    .getExpiration()
                    .before(new Date());
        } catch (Exception exception) {
            return false;
        }
    }
}

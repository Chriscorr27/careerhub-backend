package com.chrion.careerhub.integration;

import com.chrion.careerhub.auth.service.JwtService;
import com.chrion.careerhub.config.JwtProperties;
import com.chrion.careerhub.user.model.User;
import com.chrion.careerhub.user.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class BaseIntegrationTest extends IntegrationTestContainers {
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected PasswordEncoder passwordEncoder;
    @Autowired
    protected JwtService jwtService;
    @Autowired
    protected JwtProperties jwtProperties;
    @Autowired
    protected MockMvc mockMvc;

    // Protected so child classes can use the user details in their assertions
    protected User mockUser;

    protected String password = "password";

    protected String jwtToken;

    @BeforeAll
    void setUpOnce() {
        // Use a random email to prevent Unique Constraint violations
        // when multiple test classes run back-to-back using the same database
        mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setPasswordHash(passwordEncoder.encode(password));
        mockUser = userRepository.save(mockUser);
        jwtToken = jwtService.generateAccessToken(mockUser);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                mockUser.getId(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterAll
    void tearDownOnce() {
        // Clean up after the test class finishes so data doesn't bleed into other tests
        userRepository.delete(mockUser);
    }
}

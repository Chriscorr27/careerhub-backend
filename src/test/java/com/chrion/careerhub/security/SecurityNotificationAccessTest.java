package com.chrion.careerhub.security;

import com.chrion.careerhub.integration.BaseIntegrationTest;
import com.chrion.careerhub.notification.model.Notification;
import com.chrion.careerhub.notification.model.NotificationType;
import com.chrion.careerhub.notification.repository.NotificationRepository;
import com.chrion.careerhub.user.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

@SpringBootTest
public class SecurityNotificationAccessTest extends BaseIntegrationTest {

    @Autowired
    private NotificationRepository notificationRepository;

    private User mockUserB;

    private Notification notification;

    @BeforeEach
    void setup() {

        mockUserB = new User();
        mockUserB.setEmail("notification-owner@example.com");
        mockUserB.setPasswordHash(
                passwordEncoder.encode(password)
        );

        mockUserB = userRepository.save(mockUserB);

        notification = Notification.builder()
                .userId(mockUserB.getId())
                .title("Test Notification")
                .message("Test notification message")
                .type(NotificationType.JOB_STATUS_CHANGED)
                .read(false)
                .createdAt(Instant.now())
                .build();

        notification = notificationRepository.save(notification);
    }

    @AfterEach
    void tearDown() {
        notificationRepository.delete(notification);
        userRepository.delete(mockUserB);
    }

    @Test
    void userCannotModifyAnotherUsersNotification() throws Exception {

        mockMvc.perform(
            patch("/api/v1/notifications/" + notification.getId() + "/read")
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + jwtToken
                )
        )
        .andExpect(status().isNotFound());
    }

    @Test
    void userCanModifyOwnNotification() throws Exception {

        String userBToken = jwtService.generateAccessToken(mockUserB);

        mockMvc.perform(
            patch("/api/v1/notifications/" + notification.getId() + "/read")
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + userBToken
                )
        )
        .andExpect(status().isNoContent());
    }
}

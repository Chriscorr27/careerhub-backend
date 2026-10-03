package com.chrion.careerhub.security;

import com.chrion.careerhub.application.dto.ApplicationResponse;
import com.chrion.careerhub.application.dto.CreateApplicationRequest;
import com.chrion.careerhub.application.model.ApplicationStatus;
import com.chrion.careerhub.application.service.ApplicationDBService;
import com.chrion.careerhub.integration.BaseIntegrationTest;
import com.chrion.careerhub.job.dto.CreateJobRequest;
import com.chrion.careerhub.job.dto.JobResponse;
import com.chrion.careerhub.job.model.EmploymentType;
import com.chrion.careerhub.job.model.JobStatus;
import com.chrion.careerhub.job.service.JobDBService;
import com.chrion.careerhub.user.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

@SpringBootTest
public class SecurityDashboardAccessTest extends BaseIntegrationTest {

    @Autowired
    private JobDBService jobDBService;

    @Autowired
    private ApplicationDBService applicationDBService;

    private User mockUserB;
    private User mockHrUser;

    @BeforeEach
    void setup() {

        mockUserB = new User();
        mockUserB.setEmail("dashboard-owner@example.com");
        mockUserB.setPasswordHash(passwordEncoder.encode(password));

        mockUserB = userRepository.save(mockUserB);

        mockHrUser = new User();
        mockHrUser.setEmail("dashboard-hr-owner@example.com");
        mockHrUser.setPasswordHash(passwordEncoder.encode(password));
        mockHrUser.setRole(User.Role.HR);

        mockHrUser = userRepository.save(mockHrUser);

        CreateJobRequest request = new CreateJobRequest(
                "Dashboard Test Job",
                "Software Engineer",
                "Mumbai",
                "https://example.com/job/dashboard",
                EmploymentType.FULL_TIME,
                "LinkedIn",
                new BigDecimal("250000"),
                new BigDecimal("350000"),
                "INR",
                "Dashboard isolation test job.",
                "Test notes."
        );

        JobResponse userBJob = jobDBService.createJobByUserId(
                mockHrUser.getId(),
                request
        );

        CreateApplicationRequest createApplicationRequest = new CreateApplicationRequest(
                userBJob.id(),
                ApplicationStatus.APPLIED
        );

        ApplicationResponse applicationResponse = applicationDBService.createApplication(
                mockUserB.getId(),
                createApplicationRequest
        );
    }

    @AfterEach
    void tearDown() {
        userRepository.delete(mockUserB);
        userRepository.delete(mockHrUser);
    }

    @Test
    void userDashboardShouldNotContainAnotherUsersData() throws Exception {

        mockMvc.perform(
            get("/api/v1/dashboard/summary")
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + jwtToken
                )
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalJobApplications").value(0))
        .andExpect(jsonPath("$.appliedJobs").value(0));
    }

    @Test
    void userDashboardShouldContainOwnData() throws Exception {

        String userBToken = jwtService.generateAccessToken(mockUserB);

        mockMvc.perform(
            get("/api/v1/dashboard/summary")
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + userBToken
                )
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalJobApplications").value(1))
        .andExpect(jsonPath("$.appliedJobs").value(1));
    }

}

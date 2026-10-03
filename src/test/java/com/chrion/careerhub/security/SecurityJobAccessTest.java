package com.chrion.careerhub.security;

import com.chrion.careerhub.integration.BaseIntegrationTest;
import com.chrion.careerhub.job.dto.CreateJobRequest;
import com.chrion.careerhub.job.dto.JobResponse;
import com.chrion.careerhub.job.model.EmploymentType;
import com.chrion.careerhub.job.model.JobStatus;
import com.chrion.careerhub.job.service.JobDBService;
import com.chrion.careerhub.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

@Slf4j
@SpringBootTest
public class SecurityJobAccessTest extends BaseIntegrationTest {


    @Autowired
    protected JobDBService jobDBService;

    protected JobResponse jobResponse;

    protected User mockUserB;

    @BeforeEach
    void setup() {
        mockUserB = new User();
        mockUserB.setEmail("testB@example.com");
        mockUserB.setPasswordHash(passwordEncoder.encode(password));
        mockUserB = userRepository.save(mockUserB);
        CreateJobRequest createJobRequest = new CreateJobRequest(
                "Test Job",
                "Software Engineer",
                "Mumbai",
                "https://example.com/job/123",
                EmploymentType.FULL_TIME,
                "LinkedIn",
                new BigDecimal(250000),
                new BigDecimal(350000),
                "INR",
                "This is a test job description.",
                "These are test notes."
        );

        jobResponse = jobDBService.createJobByUserId(mockUserB.getId(),createJobRequest);
    }

    @AfterEach
    void tearDown() {
        userRepository.delete(mockUserB);
    }

    @Test
    void userAccessOwnUsersJob() throws Exception {
        String userBJwtToken = jwtService.generateAccessToken(mockUserB);
        mockMvc.perform(
                        get("/api/v1/jobs/" + jobResponse.id())
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer " + userBJwtToken
                                )
                )
                .andExpect(status().isOk());
    }
}

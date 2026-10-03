package com.chrion.careerhub.job;

import com.chrion.careerhub.integration.BaseIntegrationTest;
import com.chrion.careerhub.job.dto.CreateJobRequest;
import com.chrion.careerhub.job.dto.JobResponse;
import com.chrion.careerhub.job.model.EmploymentType;
import com.chrion.careerhub.job.model.Job;
import com.chrion.careerhub.job.model.JobStatus;
import com.chrion.careerhub.job.repository.JobRepository;
import com.chrion.careerhub.job.service.JobService;
import com.chrion.careerhub.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class JobIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobService jobService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldCreateAndPersistJob() {

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

        JobResponse jobResponse = jobService.createJob(createJobRequest);

        assertNotNull(jobResponse.id());

        Job retrievedJob = jobRepository.findById(jobResponse.id())
                .orElseThrow();

        assertEquals(mockUser.getId(), retrievedJob.getUserId());
        assertEquals("Test Job", retrievedJob.getCompany());
        assertEquals("Software Engineer", retrievedJob.getJobTitle());
        assertEquals(JobStatus.OPEN, retrievedJob.getStatus());
    }
}

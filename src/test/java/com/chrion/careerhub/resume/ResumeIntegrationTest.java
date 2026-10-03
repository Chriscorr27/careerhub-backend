package com.chrion.careerhub.resume;

import com.chrion.careerhub.integration.BaseIntegrationTest;
import com.chrion.careerhub.integration.IntegrationTestContainers;
import com.chrion.careerhub.resume.dto.CreateResumeRequest;
import com.chrion.careerhub.resume.dto.ResumeResponse;
import com.chrion.careerhub.resume.model.PersonalInfo;
import com.chrion.careerhub.resume.model.Resume;
import com.chrion.careerhub.resume.repository.ResumeRepository;
import com.chrion.careerhub.resume.service.ResumeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Testcontainers
class ResumeIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private ResumeService resumeService;

    @Test
    void shouldCreateAndPersistResume() {
        PersonalInfo personalInfo = PersonalInfo.builder()
                .fullName("Tester")
                .build();

        CreateResumeRequest createResumeRequest = new CreateResumeRequest(
                "Java Full Stack Developer",
                Resume.ResumeStatus.DRAFT,
                personalInfo,
                "This is a test resume description.",
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );

        ResumeResponse savedResume = resumeService.createResume(createResumeRequest);

        assertNotNull(savedResume.id());

        Resume retrievedResume = resumeRepository.findById(savedResume.id())
                .orElseThrow();

        assertEquals(mockUser.getId(), retrievedResume.getUserId());
        assertEquals("Tester", retrievedResume.getPersonalInfo().getFullName());
        assertEquals("Java Full Stack Developer", retrievedResume.getTitle());
    }
}

package com.chrion.careerhub.resume.service;

import com.chrion.careerhub.auth.service.AuthService;
import com.chrion.careerhub.common.exception.CustomException;
import com.chrion.careerhub.resume.dto.CreateResumeRequest;
import com.chrion.careerhub.resume.dto.ResumeResponse;
import com.chrion.careerhub.resume.dto.UpdateResumeRequest;
import com.chrion.careerhub.resume.model.Resume;
import com.chrion.careerhub.resume.repository.ResumeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final AuthService authService;

    @Transactional
    public ResumeResponse createResume(CreateResumeRequest request) {
        log.info("Creating resume");

        UUID userId = authService.getCurrentAuthenticatedUserId();

        Resume resume = new Resume();

        resume.setUserId(userId);
        resume.setTitle(request.title());
        resume.setVersion(1);
        resume.setStatus(request.status() != null ? request.status() : Resume.ResumeStatus.DRAFT);

        resume.setPersonalInfo(request.personalInfo());
        resume.setSummary(request.summary());

        resume.setSkills(request.skills() != null ? request.skills() : List.of());

        resume.setExperiences(request.experiences() != null ? request.experiences() : List.of());

        resume.setEducation(request.education() != null ? request.education() : List.of());

        resume.setProjects(request.projects() != null ? request.projects() : List.of());

        resume.setCertifications(request.certifications() != null ? request.certifications() : List.of());

        resume.setAchievements(request.achievements() != null ? request.achievements() : List.of());

        Resume savedResume = resumeRepository.save(resume);

        return toResponse(savedResume);
    }

    @Transactional(readOnly = true)
    public List<ResumeResponse> getMyResumes() {
        log.info("Fetching resumes for current user");

        UUID userId = authService.getCurrentAuthenticatedUserId();

        return resumeRepository.findAllByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Cacheable(
            value = "resumes",
            key = "#resumeId"
    )
    public ResumeResponse getResume(String resumeId) {
        log.info("Fetching resume with ID: {} from mongoDB", resumeId);
        UUID userId = authService.getCurrentAuthenticatedUserId();

        Resume resume = resumeRepository
                .findByIdAndUserId(resumeId, userId)
                .orElseThrow(() -> new CustomException("Resume not found", HttpStatus.NOT_FOUND));
        return toResponse(resume);
    }

    @CacheEvict(
            value = "resumes",
            key = "#resumeId"
    )
    @Transactional
    public ResumeResponse updateResume(String resumeId, UpdateResumeRequest request) {
        log.info("Updating resume {}", resumeId);

        UUID userId = authService.getCurrentAuthenticatedUserId();

        Resume resume = resumeRepository
                .findByIdAndUserId(resumeId, userId)
                .orElseThrow(() -> new CustomException("Resume not found", HttpStatus.NOT_FOUND));

        resume.setTitle(request.title());

        if (request.status() != null) {
            resume.setStatus(request.status());
        }

        resume.setPersonalInfo(request.personalInfo());
        resume.setSummary(request.summary());
        resume.setSkills(request.skills());
        resume.setExperiences(request.experiences());
        resume.setEducation(request.education());
        resume.setProjects(request.projects());
        resume.setCertifications(request.certifications());
        resume.setAchievements(request.achievements());

        resume.setVersion(resume.getVersion() + 1);

        Resume updatedResume = resumeRepository.save(resume);

        return toResponse(updatedResume);
    }

    @CacheEvict(
            value = "resumes",
            key = "#resumeId"
    )
    @Transactional
    public void deleteResume(String resumeId) {
        log.info("Deleting resume {}", resumeId);

        UUID userId = authService.getCurrentAuthenticatedUserId();

        Resume resume = resumeRepository
                .findByIdAndUserId(resumeId, userId)
                .orElseThrow(() -> new CustomException("Resume not found", HttpStatus.NOT_FOUND));

        resumeRepository.delete(resume);
    }


    private ResumeResponse toResponse(Resume resume) {

        return new ResumeResponse(
                resume.getId(),
                resume.getUserId(),
                resume.getTitle(),
                resume.getVersion(),
                resume.getStatus(),
                resume.getPersonalInfo(),
                resume.getSummary(),
                resume.getSkills(),
                resume.getExperiences(),
                resume.getEducation(),
                resume.getProjects(),
                resume.getCertifications(),
                resume.getAchievements(),
                resume.getCreatedAt(),
                resume.getUpdatedAt()
        );
    }
}

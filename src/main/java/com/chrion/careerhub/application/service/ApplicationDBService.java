package com.chrion.careerhub.application.service;

import com.chrion.careerhub.application.dto.ApplicationResponse;
import com.chrion.careerhub.application.dto.ApplicationSearchRequest;
import com.chrion.careerhub.application.dto.CreateApplicationRequest;
import com.chrion.careerhub.application.model.Application;
import com.chrion.careerhub.application.model.ApplicationStatus;
import com.chrion.careerhub.application.repository.ApplicationRepository;
import com.chrion.careerhub.application.specification.ApplicationSpecification;
import com.chrion.careerhub.common.dto.PageResponse;
import com.chrion.careerhub.common.exception.CustomException;
import com.chrion.careerhub.constant.EventVersions;
import com.chrion.careerhub.event.model.ApplicationStatusChangedEvent;
import com.chrion.careerhub.event.producer.EventProducer;
import com.chrion.careerhub.job.dto.JobResponse;
import com.chrion.careerhub.job.dto.JobSearchRequest;
import com.chrion.careerhub.job.model.Job;
import com.chrion.careerhub.job.repository.JobRepository;
import com.chrion.careerhub.job.specification.JobSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationDBService {
    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final ApplicationStatusTransitionService applicationStatusTransitionService;

    private final EventProducer eventProducer;

    @CacheEvict(
            value = "dashboard-summary",
            key = "#userId"
    )
    @Transactional
    public ApplicationResponse createApplication(
            UUID userId,
            CreateApplicationRequest request
    ) {
        Job job = jobRepository.findById(request.jobId())
                .orElseThrow(
                        ()-> new CustomException("Job not found", HttpStatus.NOT_FOUND)
                );
        Application application = applicationRepository.findByUserIdAndJobIdAndStatusNot(
                userId, request.jobId(), ApplicationStatus.WITHDRAWN)
                .orElse(null);

        if(application != null){
            return toResponse(application);
        }

        application = getApplication(userId,job,request);
        Instant now = Instant.now();
        Application createdApplication = applicationRepository.save(application);

        if(createdApplication.getStatus()==ApplicationStatus.APPLIED){
            // produce Kafka event
            ApplicationStatusChangedEvent event = new ApplicationStatusChangedEvent(
                    UUID.randomUUID().toString(),
                    EventVersions.APPLICATION_STATUS_CHANGED,
                    application.getJob().getId().toString(),
                    application.getId().toString(),
                    application.getUserId().toString(),
                    ApplicationStatus.SAVED,
                    ApplicationStatus.APPLIED,
                    "New Application created for JobId: "+application.getJob().toString(),
                    now,
                    userId.toString(),
                    application.getHrUserId().toString()
            );
            eventProducer.publishApplicationStatusChangeEvent(event);
        }

        return toResponse(createdApplication);
    }

    private static @NonNull Application getApplication(
            UUID userId,
            Job job,
            CreateApplicationRequest request
    ) {
        Application application = Application.builder()
                .job(job)
                .userId(userId)
                .hrUserId(job.getUserId())
                .build();
        if(request.applicationStatus() == ApplicationStatus.APPLIED){
            application.setStatus(request.applicationStatus());
            application.setAppliedAt(Instant.now());
        }
        return application;
    }

    @Transactional(readOnly = true)
    public PageResponse<ApplicationResponse> searchApplications(UUID userId, ApplicationSearchRequest request, Pageable pageable) {
        log.info("Searching applications");
        Specification<Application> specification = ApplicationSpecification.filter(userId,request);

        Page<Application> applications = applicationRepository.findAll(specification, pageable);

        return toPageResponse(applications);
    }

    @Transactional(readOnly = true)
    public Application getApplication(UUID userId, UUID applicationId) {
        log.info("Getting job {} from DB", applicationId);

        return applicationRepository
                .findByIdAndUserId(applicationId, userId)
                .orElseThrow(() -> new CustomException("Application not found", HttpStatus.NOT_FOUND));
    }

    @Cacheable(
            value = "applications",
            key = "#applicationId"
    )
    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationResponse(UUID userId, UUID applicationId) {
        return toResponse(getApplication(userId, applicationId));
    }


    @CacheEvict(
            value = {"dashboard-summary", "dashboard-status-distribution","dashboard-recent-jobs","dashboard-job-trends"},
            key = "#application.userId"
    )
    @Transactional
    public ApplicationResponse changeApplicationStatusByUserId(
            UUID changeBy,
            Application application,
            ApplicationStatus newStatus
    ) {

        ApplicationStatus currentStatus = application.getStatus();
        applicationStatusTransitionService.validate(application.getStatus(), newStatus);

        application.setStatus(newStatus);
        Instant now = Instant.now();
        if (newStatus == ApplicationStatus.APPLIED && application.getAppliedAt() == null) {
            application.setAppliedAt(now);
        }

        Application updatedApplication = applicationRepository.save(application);
        String notifyTo = application.getUserId().toString().equals(changeBy.toString())?
                application.getHrUserId().toString(): application.getUserId().toString();
        // produce Kafka event
        ApplicationStatusChangedEvent event = new ApplicationStatusChangedEvent(
                UUID.randomUUID().toString(),
                EventVersions.APPLICATION_STATUS_CHANGED,
                application.getJob().getId().toString(),
                application.getId().toString(),
                application.getUserId().toString(),
                currentStatus,
                newStatus,
                "Application status updated from "+currentStatus+" --> "+newStatus,
                now,
                changeBy.toString(),
                notifyTo
        );
        eventProducer.publishApplicationStatusChangeEvent(event);
        return toResponse(updatedApplication);
    }

    public ApplicationResponse toResponse(Application application) {
        return ApplicationResponse.from(application);
    }

    public PageResponse<ApplicationResponse> toPageResponse(Page<Application> applications) {
        return new PageResponse<>(
                applications.getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList(),
                applications.getNumber(),
                applications.getSize(),
                applications.getTotalElements(),
                applications.getTotalPages(),
                applications.isFirst(),
                applications.isLast()
        );
    }

}

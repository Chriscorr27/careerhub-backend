package com.chrion.careerhub.audit.service;

import com.chrion.careerhub.audit.model.*;
import com.chrion.careerhub.audit.repository.ApplicationStatusHistoryRepository;
import com.chrion.careerhub.auth.service.AuthService;
import com.chrion.careerhub.common.dto.PageResponse;
import com.chrion.careerhub.common.exception.CustomException;
import com.chrion.careerhub.event.model.*;
import com.chrion.careerhub.job.dto.AuditEventResponse;
import com.chrion.careerhub.job.dto.JobStatusHistoryResponse;
import com.chrion.careerhub.audit.repository.AuditEventRepository;
import com.chrion.careerhub.job.repository.JobRepository;
import com.chrion.careerhub.audit.repository.JobStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {
    private final JobStatusHistoryRepository jobStatusHistoryRepository;
    private final ApplicationStatusHistoryRepository applicationStatusHistoryRepository;
    private final AuditEventRepository auditEventRepository;
    private final JobRepository jobRepository;
    private final AuthService authService;

    @Transactional
    public void processApplicationStatusChangeEvent(ApplicationStatusChangedEvent event){
        log.info("Processing application status change audit event {}", event.eventId());
        // save Job status history
        ApplicationStatusHistory applicationStatusHistory = ApplicationStatusHistory.builder()
                .eventId(event.eventId())
                .jobId(event.jobId())
                .applicationId(event.applicationId())
                .userId(event.userId())
                .previousStatus(event.previousStatus())
                .newStatus(event.newStatus())
                .changedAt(event.changedAt())
                .changedBy(event.changedBy())
                .note(event.note())
                .build();
        applicationStatusHistoryRepository.save(applicationStatusHistory);
        // save Audit events
        AuditEvent auditEvent = AuditEvent.builder()
                .eventId(event.eventId())
                .userId(event.userId())
                .entityId(event.applicationId())
                .entityType(EntityType.APPLICATION)
                .timestamp(event.changedAt())
                .action(AuditAction.APPLICATION_STATUS_CHANGED)
                .description(event.note())
                .actorId(event.changedBy())
                .build();
        auditEventRepository.save(auditEvent);
    }


    @Transactional
    public void processJobStatusChangeEvent(JobStatusChangedEvent event){
        log.info("Processing job status change audit event {}", event.eventId());
        // save Job status history
        JobStatusHistory jobStatusHistory = JobStatusHistory.builder()
                .eventId(event.eventId())
                .jobId(event.jobId())
                .userId(event.userId())
                .previousStatus(event.previousStatus())
                .newStatus(event.newStatus())
                .changedAt(event.changedAt())
                .changedBy(event.changedBy())
                .note(event.note())
                .build();
        jobStatusHistoryRepository.save(jobStatusHistory);
        // save Audit events
        AuditEvent auditEvent = AuditEvent.builder()
                .eventId(event.eventId())
                .userId(event.userId())
                .entityId(event.jobId())
                .entityType(EntityType.JOB)
                .timestamp(event.changedAt())
                .action(AuditAction.JOB_STATUS_CHANGED)
                .description(event.note())
                .actorId(event.changedBy())
                .build();
        auditEventRepository.save(auditEvent);
    }

    @Transactional
    public void processJobCreatedEvent(JobCreatedEvent event){
        log.info("Processing job created audit event {}", event.eventId());
        // save Audit events
        AuditEvent auditEvent = AuditEvent.builder()
                .eventId(event.eventId())
                .userId(event.userId())
                .entityId(event.jobId())
                .entityType(EntityType.JOB)
                .timestamp(event.createdAt())
                .action(AuditAction.JOB_CREATED)
                .description(event.note())
                .actorId(event.userId())
                .build();
        auditEventRepository.save(auditEvent);
    }

    @Transactional
    public void processJobUpdatedEvent(JobUpdatedEvent event){
        log.info("Processing job updated audit event {}", event.eventId());
        // save Audit events
        AuditEvent auditEvent = AuditEvent.builder()
                .eventId(event.eventId())
                .userId(event.userId())
                .entityId(event.jobId())
                .entityType(EntityType.JOB)
                .timestamp(event.updatedAt())
                .action(AuditAction.JOB_UPDATED)
                .description(event.note())
                .actorId(event.updatedBy())
                .build();
        auditEventRepository.save(auditEvent);
    }

    @Transactional
    public void processJobDeletedEvent(JobDeletedEvent event){
        log.info("Processing job deleted audit event {}", event.eventId());
        // save Audit events
        AuditEvent auditEvent = AuditEvent.builder()
                .eventId(event.eventId())
                .userId(event.userId())
                .entityId(event.jobId())
                .entityType(EntityType.JOB)
                .timestamp(event.deletedAt())
                .action(AuditAction.JOB_DELETED)
                .description(event.note())
                .actorId(event.deletedBy())
                .build();
        auditEventRepository.save(auditEvent);
    }

    @Transactional(readOnly = true)
    public List<JobStatusHistoryResponse> getJobStatusHistory(UUID jobId) {
        log.info("Fetching status history for job {}", jobId);
        UUID userId = authService.getCurrentAuthenticatedUserId();

        // First ensure the job belongs to the user.
        jobRepository
                .findByIdAndUserId(jobId, userId)
                .orElseThrow(() -> new CustomException("Job not found", HttpStatus.NOT_FOUND));

        return jobStatusHistoryRepository
                .findByJobIdOrderByChangedAtAsc(jobId.toString())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<AuditEventResponse> getEntityAuditEvent(String entityId, Pageable pageable) {
        log.info("Fetching audit events for entity {}", entityId);
        Page<AuditEvent> auditEvents = auditEventRepository.findByEntityId(entityId, pageable);
        return toPageAuditEventResponse(auditEvents);
    }



    @Transactional(readOnly = true)
    public PageResponse<JobStatusHistoryResponse> getJobStatusHistory(Pageable pageable) {
        log.info("Fetching current user's job status history");
        UUID userId = authService.getCurrentAuthenticatedUserId();

        Page<JobStatusHistory> jobStatusHistories = jobStatusHistoryRepository
                .findByUserId(userId.toString(), pageable);

        return toPageResponse(jobStatusHistories);
    }

    private JobStatusHistoryResponse toResponse(JobStatusHistory jobStatusHistory){
        return new JobStatusHistoryResponse(
                jobStatusHistory.getId(),
                jobStatusHistory.getJobId(),
                jobStatusHistory.getPreviousStatus(),
                jobStatusHistory.getNewStatus(),
                jobStatusHistory.getNote(),
                jobStatusHistory.getChangedAt(),
                jobStatusHistory.getChangedBy()
        );
    }

    private AuditEventResponse toAuditEventResponse(AuditEvent auditEvent){
        return new AuditEventResponse(
                auditEvent.getId(),
                auditEvent.getUserId(),
                auditEvent.getEntityId(),
                auditEvent.getEntityType(),
                auditEvent.getAction(),
                auditEvent.getDescription(),
                auditEvent.getTimestamp(),
                auditEvent.getActorId()
        );
    }

    private PageResponse<JobStatusHistoryResponse> toPageResponse(Page<JobStatusHistory> jobs) {
        return new PageResponse<>(
                jobs.getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList(),

                jobs.getNumber(),
                jobs.getSize(),
                jobs.getTotalElements(),
                jobs.getTotalPages(),
                jobs.isFirst(),
                jobs.isLast()
        );
    }

    private PageResponse<AuditEventResponse> toPageAuditEventResponse(Page<AuditEvent> auditEvents) {
        return new PageResponse<>(
                auditEvents.getContent()
                        .stream()
                        .map(this::toAuditEventResponse)
                        .toList(),

                auditEvents.getNumber(),
                auditEvents.getSize(),
                auditEvents.getTotalElements(),
                auditEvents.getTotalPages(),
                auditEvents.isFirst(),
                auditEvents.isLast()
        );
    }

}

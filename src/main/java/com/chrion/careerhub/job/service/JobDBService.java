package com.chrion.careerhub.job.service;

import com.chrion.careerhub.common.dto.PageResponse;
import com.chrion.careerhub.common.exception.CustomException;
import com.chrion.careerhub.constant.EventVersions;
import com.chrion.careerhub.event.model.JobCreatedEvent;
import com.chrion.careerhub.event.model.JobDeletedEvent;
import com.chrion.careerhub.event.model.JobStatusChangedEvent;
import com.chrion.careerhub.event.model.JobUpdatedEvent;
import com.chrion.careerhub.event.producer.EventProducer;
import com.chrion.careerhub.job.dto.CreateJobRequest;
import com.chrion.careerhub.job.dto.JobResponse;
import com.chrion.careerhub.job.dto.JobSearchRequest;
import com.chrion.careerhub.job.dto.UpdateJobRequest;
import com.chrion.careerhub.job.model.Job;
import com.chrion.careerhub.job.model.JobStatus;
import com.chrion.careerhub.job.repository.JobRepository;
import com.chrion.careerhub.job.specification.JobSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
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
public class JobDBService {
    private final JobRepository jobRepository;
    private final EventProducer eventProducer;

    @Transactional
    public JobResponse createJobByUserId(UUID userId, CreateJobRequest request){
        Job job = getJob(userId, request);

        Instant now = Instant.now();
        Job savedJob = jobRepository.save(job);

        // publish Job Create Event
        JobCreatedEvent jobCreatedEvent = new JobCreatedEvent(
                UUID.randomUUID().toString(),
                EventVersions.JOB_CREATED,
                job.getId().toString(),
                job.getUserId().toString(),
                "Job Created",
                now
        );
        eventProducer.publishJobCreatedEvent(jobCreatedEvent);

        return toResponse(savedJob);
    }

    private static @NonNull Job getJob(UUID userId, CreateJobRequest request) {
        Job job = new Job();

        job.setUserId(userId);
        job.setCompany(request.company());
        job.setJobTitle(request.jobTitle());
        job.setLocation(request.location());
        job.setJobUrl(request.jobUrl());
        job.setEmploymentType(request.employmentType());
        job.setSource(request.source());
        job.setSalaryMin(request.salaryMin());
        job.setSalaryMax(request.salaryMax());
        job.setCurrency(request.currency());
        job.setDescription(request.description());
        job.setNotes(request.notes());
        return job;
    }

    @Transactional(readOnly = true)
    public PageResponse<JobResponse> searchJobs(JobSearchRequest request, Pageable pageable) {
        log.info("Searching jobs");

        Specification<Job> specification = JobSpecification.filter(request);

        Page<Job> jobs = jobRepository.findAll(specification, pageable);

        return toPageResponse(jobs);
    }

    @Cacheable(
            value = "jobs",
            key = "#jobId"
    )
    @Transactional(readOnly = true)
    public JobResponse getJob(UUID jobId) {
        log.info("Getting job {} from DB", jobId);

        Job job = jobRepository
                .findById(jobId)
                .orElseThrow(() -> new CustomException("Job not found", HttpStatus.NOT_FOUND));

        return toResponse(job);
    }

    @Transactional
    public JobResponse updateJobByUserID(UUID userId, UUID jobId, UpdateJobRequest request) {

        Job job = jobRepository
                .findByIdAndUserId(jobId, userId)
                .orElseThrow(() -> new CustomException("Job not found", HttpStatus.NOT_FOUND));

        job.setCompany(request.company());
        job.setJobTitle(request.jobTitle());
        job.setLocation(request.location());
        job.setJobUrl(request.jobUrl());
        job.setEmploymentType(request.employmentType());
        job.setSource(request.source());
        job.setSalaryMin(request.salaryMin());
        job.setSalaryMax(request.salaryMax());
        job.setCurrency(request.currency());
        job.setDescription(request.description());
        job.setNotes(request.notes());

        Job updatedJob = jobRepository.save(job);

        Instant now = Instant.now();
        // publish Job Updated Event
        JobUpdatedEvent jobUpdatedEvent = new JobUpdatedEvent(
                UUID.randomUUID().toString(),
                EventVersions.JOB_UPDATED,
                job.getId().toString(),
                job.getUserId().toString(),
                "Job Updated",
                now,
                userId.toString()
        );
        eventProducer.publishJobUpdatedEvent(jobUpdatedEvent);

        return toResponse(updatedJob);
    }

    @Transactional
    public void deleteJobByUserId(UUID userId, UUID jobId) {
        Job job = jobRepository
                .findByIdAndUserId(jobId, userId)
                .orElseThrow(() -> new CustomException("Job not found", HttpStatus.NOT_FOUND));

        Instant now = Instant.now();
        jobRepository.delete(job);

        // publish Job Deleted Event
        JobDeletedEvent jobDeletedEvent = new JobDeletedEvent(
                UUID.randomUUID().toString(),
                EventVersions.JOB_DELETED,
                job.getId().toString(),
                job.getUserId().toString(),
                "Job Deleted",
                now,
                userId.toString()
        );
        eventProducer.publishJobDeletedEvent(jobDeletedEvent);
    }

    @Transactional
    public JobResponse changeJobStatusByUserId(UUID userId, UUID jobId, JobStatus newStatus) {
        Job job = jobRepository
                .findByIdAndUserId(jobId, userId)
                .orElseThrow(() -> new CustomException("Job not found", HttpStatus.NOT_FOUND));
        JobStatus oldStatus = job.getStatus();
        job.setStatus(newStatus);
        Instant now = Instant.now();
        if (newStatus == JobStatus.CLOSED && job.getClosedAt() == null) {
            job.setClosedAt(now);
        }

        Job updatedJob = jobRepository.save(job);

        // publish Job Status Change Event
        JobStatusChangedEvent jobStatusChangedEvent = new JobStatusChangedEvent(
                UUID.randomUUID().toString(),
                EventVersions.JOB_DELETED,
                job.getId().toString(),
                job.getUserId().toString(),
                oldStatus,
                newStatus,
                "Job Status changed from "+oldStatus+" --> "+newStatus,
                now,
                userId.toString()
        );
        eventProducer.publishJobStatusChangeEvent(jobStatusChangedEvent);

        return toResponse(updatedJob);
    }


    public JobResponse toResponse(Job job) {
        return JobResponse.from(job);
    }

    public PageResponse<JobResponse> toPageResponse(Page<Job> jobs) {
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
}

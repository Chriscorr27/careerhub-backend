package com.chrion.careerhub.job.service;

import com.chrion.careerhub.auth.service.AuthService;
import com.chrion.careerhub.common.dto.PageResponse;

import com.chrion.careerhub.job.dto.*;
import com.chrion.careerhub.job.model.*;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final AuthService authService;

    private final JobDBService jobDBService;



    public JobResponse createJob(CreateJobRequest request) {
        log.info("Creating job");
        UUID userId = authService.getCurrentAuthenticatedUserId();
        return jobDBService.createJobByUserId(userId, request);
    }

    @Transactional(readOnly = true)
    public PageResponse<JobResponse> searchJobs(JobSearchRequest request, Pageable pageable) {
        log.info("Searching jobs");
        return jobDBService.searchJobs(request, pageable);
    }

    public JobResponse getJob(UUID jobId) {
        return jobDBService.getJob(jobId);
    }



    @CacheEvict(
            value = "jobs",
            key = "#jobId"
    )
    public JobResponse updateJob(UUID jobId, UpdateJobRequest request) {
        log.info("Updating job {}", jobId);
        UUID userId = authService.getCurrentAuthenticatedUserId();
        return jobDBService.updateJobByUserID(userId, jobId, request);
    }

    @CacheEvict(
            value = "jobs",
            key = "#jobId"
    )
    public void deleteJob(UUID jobId) {
        log.info("Deleting job {}", jobId);
        UUID userId = authService.getCurrentAuthenticatedUserId();
        jobDBService.deleteJobByUserId(userId, jobId);
    }

    @CacheEvict(
            value = "jobs",
            key = "#jobId"
    )
    public JobResponse changeStatus(UUID jobId, JobStatus newStatus) {
        log.info("Changing status for job {} to {}", jobId, newStatus);
        UUID userId = authService.getCurrentAuthenticatedUserId();
        return jobDBService.changeJobStatusByUserId(userId, jobId, newStatus);
    }

}
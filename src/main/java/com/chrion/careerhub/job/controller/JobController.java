package com.chrion.careerhub.job.controller;

import com.chrion.careerhub.common.dto.PageResponse;
import com.chrion.careerhub.common.validator.QueryValidator;
import com.chrion.careerhub.job.dto.*;
import com.chrion.careerhub.job.model.EmploymentType;
import com.chrion.careerhub.job.model.JobStatus;
import com.chrion.careerhub.job.service.JobService;

import com.chrion.careerhub.job.validation.JobQueryValidator;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {

    private final JobService jobService;
    private final QueryValidator queryValidator;
    private final JobQueryValidator jobQueryValidator;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('HR')")
    public JobResponse createJob(@Valid @RequestBody CreateJobRequest request) {
        return jobService.createJob(request);
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<JobResponse> searchJobs(
            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            String company,

            @RequestParam(required = false)
            String location,

            @RequestParam(required = false)
            JobStatus status,

            @RequestParam(required = false)
            EmploymentType employmentType,

            @RequestParam(required = false)
            BigDecimal salaryMin,

            @RequestParam(required = false)
            BigDecimal salaryMax,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant toDate,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "createdAt")
            String sortBy,

            @RequestParam(defaultValue = "desc")
            String direction
    ) {
        queryValidator.validatePagination(page, size);
        jobQueryValidator.validateSortField(sortBy);
        queryValidator.validateDirection(direction);
        jobQueryValidator.validateSalaryRange(salaryMin, salaryMax);
        jobQueryValidator.validateDates(fromDate, toDate);

        JobSearchRequest request = new JobSearchRequest(keyword, company, location, status, employmentType, salaryMin, salaryMax, fromDate, toDate);

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)? Sort.Direction.DESC:Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy)
        );

        return jobService.searchJobs(request,pageable);
    }

    @GetMapping("/{jobId}")
    @ResponseStatus(HttpStatus.OK)
    public JobResponse getJob(@PathVariable UUID jobId) {
        return jobService.getJob(jobId);
    }

    @PutMapping("/{jobId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('HR')")
    public JobResponse updateJob(@PathVariable UUID jobId, @Valid @RequestBody UpdateJobRequest request) {
        return jobService.updateJob(jobId, request);
    }

    @DeleteMapping("/{jobId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('HR')")
    public void deleteJob(@PathVariable UUID jobId) {
        jobService.deleteJob(jobId);
    }

    @PatchMapping("/{jobId}/status")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('HR')")
    public JobResponse changeStatus(@PathVariable UUID jobId, @Valid @RequestBody ChangeJobStatusRequest request) {
        return jobService.changeStatus(jobId, request.status());
    }
}

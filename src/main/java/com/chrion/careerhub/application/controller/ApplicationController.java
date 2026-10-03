package com.chrion.careerhub.application.controller;

import com.chrion.careerhub.application.dto.ApplicationChangeStatusRequest;
import com.chrion.careerhub.application.dto.ApplicationResponse;
import com.chrion.careerhub.application.dto.ApplicationSearchRequest;
import com.chrion.careerhub.application.dto.CreateApplicationRequest;
import com.chrion.careerhub.application.model.Application;
import com.chrion.careerhub.application.model.ApplicationStatus;
import com.chrion.careerhub.application.service.ApplicationService;
import com.chrion.careerhub.application.validation.ApplicationQueryValidator;
import com.chrion.careerhub.common.dto.PageResponse;
import com.chrion.careerhub.common.validator.QueryValidator;
import com.chrion.careerhub.job.dto.JobResponse;
import com.chrion.careerhub.job.model.EmploymentType;
import com.chrion.careerhub.job.model.JobStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/applications")
@Service
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;
    private final QueryValidator queryValidator;
    private final ApplicationQueryValidator applicationQueryValidator;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public ApplicationResponse createApplication(@RequestBody CreateApplicationRequest request) {
        return applicationService.createApplication(request);
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<ApplicationResponse> searchApplications(
            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            String company,

            @RequestParam(required = false)
            String location,

            @RequestParam(required = false)
            JobStatus jobStatus,

            @RequestParam(required = false)
            ApplicationStatus applicationStatus,

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
        applicationQueryValidator.validateSortField(sortBy);
        queryValidator.validateDirection(direction);
        applicationQueryValidator.validateSalaryRange(salaryMin, salaryMax);
        applicationQueryValidator.validateDates(fromDate, toDate);

        ApplicationSearchRequest request = new ApplicationSearchRequest(
                keyword,
                company,
                location,
                jobStatus,
                employmentType,
                salaryMin,
                salaryMax,
                fromDate,
                toDate,
                applicationStatus
        );

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)? Sort.Direction.DESC:Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy)
        );

        return applicationService.searchApplications(request,pageable);
    }

    @GetMapping("/{applicationId}")
    @ResponseStatus(HttpStatus.OK)
    public ApplicationResponse getApplication(@PathVariable UUID applicationId) {
        return applicationService.getApplicationResponse(applicationId);
    }

    @PatchMapping("/{applicationId}/status-change-by-user")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasRole('USER')")
    public ApplicationResponse changeApplicationStatusByUser(
            @PathVariable UUID applicationId,
            @RequestBody ApplicationChangeStatusRequest request
    ) {
        return applicationService.changeApplicationStatusByUser(applicationId, request.applicationStatus());
    }

    @PatchMapping("/{applicationId}/status-change-by-hr")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasRole('HR')")
    public ApplicationResponse changeApplicationStatusByHr(
            @PathVariable UUID applicationId,
            @RequestBody ApplicationChangeStatusRequest request
    ) {
        return applicationService.changeApplicationStatusByHR(applicationId, request.applicationStatus());
    }
}

package com.chrion.careerhub.application.dto;

import com.chrion.careerhub.application.model.ApplicationStatus;
import com.chrion.careerhub.job.model.EmploymentType;
import com.chrion.careerhub.job.model.JobStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record ApplicationSearchRequest(

    String keyword,

    String company,

    String location,

    JobStatus jobStatus,

    EmploymentType employmentType,

    BigDecimal salaryMin,

    BigDecimal salaryMax,

    Instant fromDate,

    Instant toDate,

    ApplicationStatus applicationStatus
) { }

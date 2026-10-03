package com.chrion.careerhub.job.dto;

import com.chrion.careerhub.job.model.EmploymentType;
import com.chrion.careerhub.job.model.JobStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record JobSearchRequest(

    String keyword,

    String company,

    String location,

    JobStatus status,

    EmploymentType employmentType,

    BigDecimal salaryMin,

    BigDecimal salaryMax,

    Instant fromDate,

    Instant toDate
) { }

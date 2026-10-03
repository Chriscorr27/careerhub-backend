package com.chrion.careerhub.job.dto;

import com.chrion.careerhub.job.model.EmploymentType;
import com.chrion.careerhub.job.model.JobStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateJobRequest(

    @NotBlank(message = "Company is required")
    @Size(max = 200)
    String company,

    @NotBlank(message = "Job title is required")
    @Size(max = 200)
    String jobTitle,

    @Size(max = 200)
    String location,

    @Size(max = 1000)
    String jobUrl,

    EmploymentType employmentType,

    @Size(max = 100)
    String source,

    @DecimalMin(value = "0.0", message = "Salary cannot be negative")
    BigDecimal salaryMin,

    @DecimalMin(value = "0.0", message = "Salary cannot be negative")
    BigDecimal salaryMax,

    @Size(max = 10)
    String currency,

    String description,

    String notes
) {}

package com.chrion.careerhub.job.dto;

import com.chrion.careerhub.job.model.EmploymentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record UpdateJobRequest(

        @NotBlank
        String company,

        @NotBlank
        String jobTitle,

        String location,

        String jobUrl,

        EmploymentType employmentType,

        String source,

        @DecimalMin("0.0")
        BigDecimal salaryMin,

        @DecimalMin("0.0")
        BigDecimal salaryMax,

        String currency,

        String description,

        String notes
) {}

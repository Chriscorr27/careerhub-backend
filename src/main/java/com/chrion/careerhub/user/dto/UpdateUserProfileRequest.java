package com.chrion.careerhub.user.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateUserProfileRequest(
    @Size(max = 150)
    String name,

    @Size(max = 30)
    String phone,

    @Size(max = 150)
    String location,

    @Size(max = 150)
    String currentRole,

    @DecimalMin(
            value = "0.0",
            message = "Experience cannot be negative"
    )
    @DecimalMax(
            value = "60.0",
            message = "Experience is invalid"
    )
    BigDecimal experienceYears,

    @Size(max = 500)
    String linkedinUrl,

    @Size(max = 500)
    String githubUrl,

    @Size(max = 500)
    String portfolioUrl
) { }

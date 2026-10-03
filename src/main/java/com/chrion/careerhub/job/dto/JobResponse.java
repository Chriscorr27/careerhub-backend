package com.chrion.careerhub.job.dto;

import com.chrion.careerhub.job.model.EmploymentType;
import com.chrion.careerhub.job.model.Job;
import com.chrion.careerhub.job.model.JobStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record JobResponse(

    UUID id,

    UUID userId,

    String company,

    String jobTitle,

    String location,

    String jobUrl,

    EmploymentType employmentType,

    String source,

    BigDecimal salaryMin,

    BigDecimal salaryMax,

    String currency,

    String description,

    String notes,

    JobStatus status,

    Instant closedAt,

    Instant createdAt,

    Instant updatedAt
) {
    public static JobResponse from(Job job) {
        return new JobResponse(
                job.getId(),
                job.getUserId(),
                job.getCompany(),
                job.getJobTitle(),
                job.getLocation(),
                job.getJobUrl(),
                job.getEmploymentType(),
                job.getSource(),
                job.getSalaryMin(),
                job.getSalaryMax(),
                job.getCurrency(),
                job.getDescription(),
                job.getNotes(),
                job.getStatus(),
                job.getCreatedAt(),
                job.getUpdatedAt(),
                job.getClosedAt()
        );
    }
}

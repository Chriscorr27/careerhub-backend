package com.chrion.careerhub.application.dto;

import com.chrion.careerhub.application.model.Application;
import com.chrion.careerhub.application.model.ApplicationStatus;
import com.chrion.careerhub.job.dto.JobResponse;
import com.chrion.careerhub.job.model.Job;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

public record ApplicationResponse(
    UUID applicationId,
    JobResponse job,
    UUID userId,
    ApplicationStatus status,
    Instant appliedAt,
    Instant createdAt,
    Instant updatedAt
) {
    public static ApplicationResponse from(Application application) {
        return new ApplicationResponse(
                application.getId(),
                JobResponse.from(application.getJob()),
                application.getUserId(),
                application.getStatus(),
                application.getAppliedAt(),
                application.getCreatedAt(),
                application.getUpdatedAt()
        );
    }
}

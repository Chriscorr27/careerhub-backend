package com.chrion.careerhub.dashboard.dto;

import com.chrion.careerhub.application.model.ApplicationStatus;
import com.chrion.careerhub.job.model.JobStatus;

import java.time.Instant;
import java.util.UUID;

public record RecentJobApplicationResponse(
        UUID id,
        String company,
        String jobTitle,
        ApplicationStatus status,
        Instant createdAt,
        UUID userId,
        UUID hrUserId
) { }

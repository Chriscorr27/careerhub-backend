package com.chrion.careerhub.application.dto;

import com.chrion.careerhub.application.model.ApplicationStatus;
import com.chrion.careerhub.job.model.JobStatus;

import java.time.Instant;

public record ApplicationStatusHistoryResponse(
    String id,
    String jobId,
    String applicationId,
    ApplicationStatus previousStatus,
    ApplicationStatus newStatus,
    String note,
    Instant changedAt,
    String changedBy
) { }

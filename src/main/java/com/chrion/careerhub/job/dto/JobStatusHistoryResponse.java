package com.chrion.careerhub.job.dto;

import com.chrion.careerhub.job.model.JobStatus;

import java.time.Instant;

public record JobStatusHistoryResponse(
    String id,
    String jobId,
    JobStatus previousStatus,
    JobStatus newStatus,
    String note,
    Instant changedAt,
    String changedBy
) { }

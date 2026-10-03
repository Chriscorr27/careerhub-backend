package com.chrion.careerhub.event.model;

import com.chrion.careerhub.job.model.JobStatus;
import java.time.Instant;

public record JobStatusChangedEvent(
        String eventId,
        int eventVersion,
        String jobId,
        String userId,
        JobStatus previousStatus,
        JobStatus newStatus,
        String note,
        Instant changedAt,
        String changedBy
) {
}
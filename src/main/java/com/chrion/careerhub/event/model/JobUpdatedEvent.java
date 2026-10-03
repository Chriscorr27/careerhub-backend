package com.chrion.careerhub.event.model;

import com.chrion.careerhub.job.model.JobStatus;

import java.time.Instant;

public record JobUpdatedEvent(
        String eventId,
        int eventVersion,
        String jobId,
        String userId,
        String note,
        Instant updatedAt,
        String updatedBy
) {
}

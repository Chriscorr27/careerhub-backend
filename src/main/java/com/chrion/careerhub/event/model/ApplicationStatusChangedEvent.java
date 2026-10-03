package com.chrion.careerhub.event.model;

import com.chrion.careerhub.application.model.ApplicationStatus;
import java.time.Instant;

public record ApplicationStatusChangedEvent(
        String eventId,
        int eventVersion,
        String jobId,
        String applicationId,
        String userId,
        ApplicationStatus previousStatus,
        ApplicationStatus newStatus,
        String note,
        Instant changedAt,
        String changedBy,
        String notifyTo
) {
}
package com.chrion.careerhub.event.model;

import java.time.Instant;

public record JobDeletedEvent(
        String eventId,
        int eventVersion,
        String jobId,
        String userId,
        String note,
        Instant deletedAt,
        String deletedBy
) {
}

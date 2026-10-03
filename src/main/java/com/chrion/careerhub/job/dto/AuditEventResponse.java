package com.chrion.careerhub.job.dto;

import com.chrion.careerhub.audit.model.AuditAction;
import com.chrion.careerhub.audit.model.EntityType;

import java.time.Instant;

public record AuditEventResponse(
    String id,
    String userId,
    String entityId,
    EntityType entityType,
    AuditAction action,
    String description,
    Instant timestamp,
    String actorId
) { }

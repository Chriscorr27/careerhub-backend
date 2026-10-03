package com.chrion.careerhub.audit.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@CompoundIndex(
        name = "entity_user_history_idx",
        def = "{'entityId': 1, 'userId': 1, 'timestamp': -1}"
)
@Document(collection = "audit_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEvent {

    @Id
    private String id;

    @Indexed(unique = true)
    private String eventId;

    @Indexed
    private String userId;

    @Indexed
    private String entityId;

    private EntityType entityType;

    private AuditAction action;

    private String description;

    private Instant timestamp;

    private String actorId;
}

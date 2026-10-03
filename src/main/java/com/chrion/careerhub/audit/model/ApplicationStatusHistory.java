package com.chrion.careerhub.audit.model;

import com.chrion.careerhub.application.model.ApplicationStatus;
import com.chrion.careerhub.job.model.JobStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@CompoundIndex(
        name = "application_user_history_idx",
        def = "{'applicationId': 1, 'userId': 1, 'changedAt': -1}"
)
@Document(collection = "application_status_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationStatusHistory {

    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @Indexed(unique = true)
    private String eventId;

    private String jobId;

    @Indexed
    private String applicationId;

    @Indexed
    private String userId;

    private ApplicationStatus previousStatus;

    private ApplicationStatus newStatus;

    private Instant changedAt;

    private String changedBy;

    private String note;
}

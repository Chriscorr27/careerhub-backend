package com.chrion.careerhub.audit.model;

import com.chrion.careerhub.job.model.JobStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@CompoundIndex(
        name = "job_user_history_idx",
        def = "{'jobId': 1, 'userId': 1, 'changedAt': -1}"
)
@Document(collection = "job_status_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobStatusHistory {

    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @Indexed(unique = true)
    private String eventId;

    @Indexed
    private String jobId;

    @Indexed
    private String userId;

    private JobStatus previousStatus;

    private JobStatus newStatus;

    private Instant changedAt;

    private String changedBy;

    private String note;
}

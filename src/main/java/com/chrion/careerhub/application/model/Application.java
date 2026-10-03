package com.chrion.careerhub.application.model;

import com.chrion.careerhub.job.model.Job;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "applications",
    indexes = {
        @Index(name = "idx_applications_user_id", columnList = "user_id"),
        @Index(name = "idx_applications_hr_user_id", columnList = "hr_user_id"),
        @Index(
            name = "idx_applications_user_status",
            columnList = "user_id,status"
        ),
        @Index(
            name = "idx_applications_user_created_at",
            columnList = "user_id,created_at"
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "job_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_application_job"
            )
    )
    private Job job;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "hr_user_id", nullable = false)
    private UUID hrUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ApplicationStatus status;

    @Column(name = "applied_at", nullable = false)
    private Instant appliedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = ApplicationStatus.SAVED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}

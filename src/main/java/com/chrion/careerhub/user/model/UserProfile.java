package com.chrion.careerhub.user.model;

import com.chrion.careerhub.common.model.FileMetadata;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "user_profiles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_profiles_user_id",
                        columnNames = "user_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(
                    name = "fk_user_profiles_user"
            )
    )
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "profile_file_id",
            unique = true,
            foreignKey = @ForeignKey(
                    name = "fk_user_profiles_file"
            )
    )
    private UserProfileFileMetaData profileMetaData;

    @Column(length = 150)
    private String name;

    @Column(length = 30)
    private String phone;

    @Column(length = 150)
    private String location;

    @Column(name = "current_role", length = 150)
    private String currentRole;

    @Column(
            name = "experience_years",
            precision = 4,
            scale = 1
    )
    private BigDecimal experienceYears;

    @Column(name = "linkedin_url", length = 500)
    private String linkedinUrl;

    @Column(name = "github_url", length = 500)
    private String githubUrl;

    @Column(name = "portfolio_url", length = 500)
    private String portfolioUrl;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}

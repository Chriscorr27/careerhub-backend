package com.chrion.careerhub.resume.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "resumes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume {

    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @Indexed
    private UUID userId;

    private String title;

    @Builder.Default
    private Integer version = 1;

    private ResumeStatus status;

    private PersonalInfo personalInfo;

    private String summary;

    @Builder.Default
    private List<Skill> skills = new ArrayList<>();

    @Builder.Default
    private List<Experience> experiences = new ArrayList<>();

    @Builder.Default
    private List<Education> education = new ArrayList<>();

    @Builder.Default
    private List<Project> projects = new ArrayList<>();

    @Builder.Default
    private List<Certification> certifications = new ArrayList<>();

    @Builder.Default
    private List<Achievement> achievements = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public enum ResumeStatus {
        DRAFT,
        PUBLISHED,
        ARCHIVED
    }
}

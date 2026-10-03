package com.chrion.careerhub.resume.dto;

import com.chrion.careerhub.resume.model.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ResumeResponse(
    String id,
    UUID userId,
    String title,
    Integer version,
    Resume.ResumeStatus status,
    PersonalInfo personalInfo,
    String summary,
    List<Skill> skills,
    List<Experience> experiences,
    List<Education> education,
    List<Project> projects,
    List<Certification> certifications,
    List<Achievement> achievements,
    Instant createdAt,
    Instant updatedAt
) { }

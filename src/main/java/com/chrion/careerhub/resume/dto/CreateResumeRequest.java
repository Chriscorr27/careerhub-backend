package com.chrion.careerhub.resume.dto;

import com.chrion.careerhub.resume.model.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateResumeRequest(
    @NotBlank(message = "Title is required")
    String title,

    Resume.ResumeStatus status,

    @Valid
    PersonalInfo personalInfo,

    String summary,

    List<Skill> skills,

    List<Experience> experiences,

    List<Education> education,

    List<Project> projects,

    List<Certification> certifications,

    List<Achievement> achievements
) { }

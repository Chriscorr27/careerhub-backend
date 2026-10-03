package com.chrion.careerhub.application.dto;

import com.chrion.careerhub.application.model.ApplicationStatus;

import java.util.UUID;

public record CreateApplicationRequest(
        UUID jobId,
        ApplicationStatus applicationStatus
) {
}

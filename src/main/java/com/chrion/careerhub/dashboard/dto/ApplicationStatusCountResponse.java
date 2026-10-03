package com.chrion.careerhub.dashboard.dto;

import com.chrion.careerhub.application.model.ApplicationStatus;

public record ApplicationStatusCountResponse(
        ApplicationStatus status,
        long count
) { }

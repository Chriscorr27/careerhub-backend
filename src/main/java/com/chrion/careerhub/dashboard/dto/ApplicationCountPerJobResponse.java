package com.chrion.careerhub.dashboard.dto;

import com.chrion.careerhub.job.model.JobStatus;

import java.util.UUID;

public record ApplicationCountPerJobResponse(
        UUID id,
        String company,
        String jobTitle,
        JobStatus status,
        long count
) { }

package com.chrion.careerhub.job.dto;

import com.chrion.careerhub.job.model.JobStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeJobStatusRequest(

        @NotNull(message = "Status is required")
        JobStatus status

) {}

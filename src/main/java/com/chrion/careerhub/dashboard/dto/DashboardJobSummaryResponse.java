package com.chrion.careerhub.dashboard.dto;

import com.chrion.careerhub.job.model.JobStatus;

import java.util.UUID;

public record DashboardJobSummaryResponse(
    UUID id,
    String company,
    String jobTitle,
    JobStatus status,
    long totalJobApplications,
    long savedJobs,
    long appliedJobs,
    long screeningJobs,
    long interviewJobs,
    long offerJobs,
    long acceptedJobs,
    long rejectedJobs
) { }

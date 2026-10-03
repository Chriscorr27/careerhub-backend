package com.chrion.careerhub.dashboard.dto;

public record DashboardSummaryResponse(
    long totalJobApplications,
    long savedJobs,
    long appliedJobs,
    long screeningJobs,
    long interviewJobs,
    long offerJobs,
    long acceptedJobs,
    long rejectedJobs,
    long unreadNotifications
) { }

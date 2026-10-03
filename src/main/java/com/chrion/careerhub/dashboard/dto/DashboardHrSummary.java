package com.chrion.careerhub.dashboard.dto;

import java.util.List;

public record DashboardHrSummary (
    List<ApplicationCountPerJobResponse> perJobResponses,
    long unreadNotifications
) {}

package com.chrion.careerhub.dashboard.service;

import com.chrion.careerhub.auth.service.AuthService;
import com.chrion.careerhub.dashboard.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardDBService dashboardDBService;
    private final AuthService authService;

    public DashboardSummaryResponse getSummary() {
        UUID userId = authService.getCurrentAuthenticatedUserId();
        return dashboardDBService.getDashboardSummaryFromDB(userId);
    }

    public DashboardJobSummaryResponse getJobSummary(UUID jobId) {
        UUID userId = authService.getCurrentAuthenticatedUserId();
        return dashboardDBService.getDashboardJobSummaryFromDB(userId, jobId);
    }

    public List<ApplicationStatusCountResponse> getStatusDistribution() {
        UUID userId = authService.getCurrentAuthenticatedUserId();
        return dashboardDBService.getStatusDistribution(userId);
    }

    public DashboardHrSummary getApplicationCountPerJobResponse(){
        UUID userId = authService.getCurrentAuthenticatedUserId();
        return dashboardDBService.getApplicationCountPerJobResponse(userId);
    }

    public List<JobApplicationTrendResponse> getJobTrend(LocalDate startDate, LocalDate endDate) {

        UUID userId = authService.getCurrentAuthenticatedUserId();

        return dashboardDBService.getJobTrend(userId, startDate, endDate);
    }

    public List<RecentJobApplicationResponse> getRecentJobs() {
        UUID userId = authService.getCurrentAuthenticatedUserId();
       return dashboardDBService.getRecentJobs(userId);
    }

}

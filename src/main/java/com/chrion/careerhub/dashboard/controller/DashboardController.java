package com.chrion.careerhub.dashboard.controller;

import com.chrion.careerhub.dashboard.dto.*;
import com.chrion.careerhub.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    public DashboardSummaryResponse getSummary() {
        return dashboardService.getSummary();
    }

    @GetMapping("/hr-summary")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('HR')")
    public DashboardHrSummary getHrSummary() {
        return dashboardService.getApplicationCountPerJobResponse();
    }

    @GetMapping("/hr-summary/{jobId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('HR')")
    public DashboardJobSummaryResponse getHrSummaryByJobId(@PathVariable UUID jobId) {
        return dashboardService.getJobSummary(jobId);
    }

    @GetMapping("/status-distribution")
    @ResponseStatus(HttpStatus.OK)
    public List<ApplicationStatusCountResponse> getStatusDistribution() {

        return dashboardService.getStatusDistribution();

    }

    @GetMapping("/trends")
    @ResponseStatus(HttpStatus.OK)
    public List<JobApplicationTrendResponse> getJobTrend(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {

        return dashboardService.getJobTrend(startDate, endDate);

    }

    @GetMapping("/recent-jobs")
    @ResponseStatus(HttpStatus.OK)
    public List<RecentJobApplicationResponse> getRecentJobs() {

        return dashboardService.getRecentJobs();

    }
}

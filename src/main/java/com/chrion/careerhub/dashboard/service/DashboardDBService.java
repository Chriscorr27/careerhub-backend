package com.chrion.careerhub.dashboard.service;

import com.chrion.careerhub.application.model.ApplicationStatus;
import com.chrion.careerhub.application.repository.ApplicationRepository;
import com.chrion.careerhub.common.exception.CustomException;
import com.chrion.careerhub.dashboard.dto.*;
import com.chrion.careerhub.job.model.Job;
import com.chrion.careerhub.job.repository.JobRepository;
import com.chrion.careerhub.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardDBService {
    private final JobRepository jobRepository;
    private final NotificationRepository notificationRepository;
    private final ApplicationRepository applicationRepository;

    @Cacheable(
            value = "dashboard-summary",
            key = "#userId"
    )
    public DashboardSummaryResponse getDashboardSummaryFromDB(UUID userId) {
        log.info("Fetching Dashboard Summary from database for user {}", userId);
        long totalJobApplications = applicationRepository.countByUserId(userId);

        long savedJobs = applicationRepository.countByUserIdAndStatus(userId, ApplicationStatus.SAVED);

        long appliedJobs = applicationRepository.countByUserIdAndStatus(userId, ApplicationStatus.APPLIED);

        long screeningJobs = applicationRepository.countByUserIdAndStatus(userId, ApplicationStatus.SCREENING);

        long interviewJobs = applicationRepository.countByUserIdAndStatus(userId, ApplicationStatus.INTERVIEW);

        long offerJobs = applicationRepository.countByUserIdAndStatus(userId, ApplicationStatus.OFFER);

        long acceptedJobs = applicationRepository.countByUserIdAndStatus(userId, ApplicationStatus.ACCEPTED);

        long rejectedJobs = applicationRepository.countByUserIdAndStatus(userId, ApplicationStatus.REJECTED);

        long unreadNotifications = notificationRepository.countByUserIdAndReadFalse(userId);

        return new DashboardSummaryResponse(
                totalJobApplications,
                savedJobs,
                appliedJobs,
                screeningJobs,
                interviewJobs,
                offerJobs,
                acceptedJobs,
                rejectedJobs,
                unreadNotifications
        );
    }

    @Cacheable(
            value = "dashboard-status-distribution",
            key = "#userId"
    )
    public List<ApplicationStatusCountResponse> getStatusDistribution(UUID userId) {
        log.info("Fetching Dashboard Status distribution from database for user {}", userId);
        return applicationRepository.countApplicationByStatus(userId);
    }

    public DashboardHrSummary getApplicationCountPerJobResponse(UUID hrUserId){
        log.info("Fetching HR Dashboard summary from database for user {}", hrUserId);
        List<ApplicationCountPerJobResponse> perJobResponses = applicationRepository.countApplicationPerJob(hrUserId);
        long unreadNotifications = notificationRepository.countByUserIdAndReadFalse(hrUserId);
        return new DashboardHrSummary(
                perJobResponses,
                unreadNotifications
        );
    }

    public DashboardJobSummaryResponse getDashboardJobSummaryFromDB(UUID hrUserId, UUID jobId) {
        log.info("Fetching Dashboard Job Summary from database for HR user {}", hrUserId);
        Job job = jobRepository.findByIdAndUserId(jobId, hrUserId)
                .orElseThrow(
                        ()-> new CustomException("Job not found", HttpStatus.NOT_FOUND)
                );
        long totalJobApplications = applicationRepository.countByHrUserIdAndJobId(hrUserId, jobId);

        long savedJobs = applicationRepository.countByHrUserIdAndJobIdAndStatus(hrUserId, jobId, ApplicationStatus.SAVED);

        long appliedJobs = applicationRepository.countByHrUserIdAndJobIdAndStatus(hrUserId, jobId, ApplicationStatus.APPLIED);

        long screeningJobs = applicationRepository.countByHrUserIdAndJobIdAndStatus(hrUserId, jobId, ApplicationStatus.SCREENING);

        long interviewJobs = applicationRepository.countByHrUserIdAndJobIdAndStatus(hrUserId, jobId, ApplicationStatus.INTERVIEW);

        long offerJobs = applicationRepository.countByHrUserIdAndJobIdAndStatus(hrUserId, jobId, ApplicationStatus.OFFER);

        long acceptedJobs = applicationRepository.countByHrUserIdAndJobIdAndStatus(hrUserId, jobId, ApplicationStatus.ACCEPTED);

        long rejectedJobs = applicationRepository.countByHrUserIdAndJobIdAndStatus(hrUserId, jobId, ApplicationStatus.REJECTED);

        return new DashboardJobSummaryResponse(
                jobId,
                job.getCompany(),
                job.getJobTitle(),
                job.getStatus(),
                totalJobApplications,
                savedJobs,
                appliedJobs,
                screeningJobs,
                interviewJobs,
                offerJobs,
                acceptedJobs,
                rejectedJobs
        );
    }


    @Cacheable(
            value = "dashboard-job-trends",
            key = "{#userId, #startDate, #endDate}"
    )
    public List<JobApplicationTrendResponse> getJobTrend(UUID userId,LocalDate startDate, LocalDate endDate) {
        log.info("Fetching Dashboard Job Trends from database for user {}", userId);
        ZoneId zoneId = ZoneId.of("Asia/Kolkata");

        Instant start = startDate
                .atStartOfDay(zoneId)
                .toInstant();

        Instant end = endDate
                .plusDays(1)
                .atStartOfDay(zoneId)
                .toInstant();

        return applicationRepository.findApplicationTrend(userId, start, end)
                .stream()
                .map(row -> new JobApplicationTrendResponse(
                        ((java.sql.Date) row[0]).toLocalDate(),
                        ((Number) row[1]).longValue()
                ))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    }

    @Cacheable(
            value = "dashboard-recent-jobs",
            key = "#userId"
    )
    @Transactional(readOnly = true)
    public List<RecentJobApplicationResponse> getRecentJobs(UUID userId) {
        log.info("Fetching Dashboard Recent Job Applications from database for user {}", userId);
        return applicationRepository
                .findTop10ByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(application -> new RecentJobApplicationResponse(
                        application.getId(),
                        application.getJob().getCompany(),
                        application.getJob().getJobTitle(),
                        application.getStatus(),
                        application.getCreatedAt(),
                        application.getUserId(),
                        application.getHrUserId()
                ))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    }

}

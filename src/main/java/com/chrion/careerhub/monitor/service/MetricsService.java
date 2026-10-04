package com.chrion.careerhub.monitor.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    private final Counter jobsCreated;
    private final Counter applicationsCreated;
    private final Counter applicationStatusChanges;
    private final Counter notificationsSent;

    public MetricsService(MeterRegistry meterRegistry) {

        this.jobsCreated = Counter.builder("careerhub_jobs_total")
                .description("Total number of jobs created")
                .register(meterRegistry);

        this.applicationsCreated = Counter.builder("careerhub_applications_total")
                .description("Total number of applications created")
                .register(meterRegistry);

        this.applicationStatusChanges = Counter.builder("careerhub_application_status_changes_total")
                .description("Total number of application status changes")
                .register(meterRegistry);

        this.notificationsSent = Counter.builder("careerhub_notifications_sent_total")
                .description("Total number of notifications sent")
                .register(meterRegistry);
    }

    public void jobCreated() {
        jobsCreated.increment();
    }

    public void applicationCreated() {
        applicationsCreated.increment();
    }

    public void applicationStatusChanged() {
        applicationStatusChanges.increment();
    }

    public void notificationSent() {
        notificationsSent.increment();
    }
}

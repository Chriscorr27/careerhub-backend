package com.chrion.careerhub.notification.service;

import com.chrion.careerhub.auth.service.AuthService;
import com.chrion.careerhub.common.exception.CustomException;
import com.chrion.careerhub.constant.EventVersions;
import com.chrion.careerhub.event.model.EmailNotificationEvent;
import com.chrion.careerhub.event.producer.EventProducer;
import com.chrion.careerhub.monitor.service.MetricsService;
import com.chrion.careerhub.notification.model.Notification;
import com.chrion.careerhub.notification.model.NotificationPreference;
import com.chrion.careerhub.notification.model.NotificationType;
import com.chrion.careerhub.notification.repository.NotificationRepository;
import com.chrion.careerhub.user.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final AuthService authService;
    private final EventProducer eventProducer;
    private final NotificationPreferenceService notificationPreferenceService;
    private final MetricsService metricsService;


    @Async("careerHubTaskExecutor")
    @Transactional
    public void createJobCreatedNotification(UUID userId, UUID jobId) throws Exception {
        log.info("Creating job created notification on thread");
        NotificationPreference notificationPreference = notificationPreferenceService.getOrCreate(userId);
        Notification notification = Notification.builder()
                .userId(userId)
                .title("New Job Created")
                .message("Your have created new Job application with jobId: " + jobId)
                .type(NotificationType.JOB_CREATED)
                .read(false)
                .build();

        if(notificationPreference.isInAppEnabled()){
            log.info("Saving job created notification for user {} and job {}", userId, jobId);
            notificationRepository.save(notification);
            metricsService.notificationSent();
        }

        if(notificationPreference.isEmailEnabled()){
            log.info("Publish job created email notification for user {}", userId);
            publishEmailNotification(notification);
        }
    }

    @Async("careerHubTaskExecutor")
    @Transactional
    public void createApplicationStatusNotification(
            UUID notifyTo,
            UUID applicationId,
            String oldStatus,
            String newStatus
    ) throws Exception {
        log.info("Creating application status notification on thread");
        NotificationPreference notificationPreference = notificationPreferenceService.getOrCreate(notifyTo);
        Notification notification = Notification.builder()
                .userId(notifyTo)
                .title("Application Status Updated")
                .message(
                        "Your application ("+applicationId+")  status changed from "
                                + oldStatus
                                + " to "
                                + newStatus
                )
                .type(NotificationType.APPLICATION_STATUS_CHANGED)
                .read(false)
                .build();
        if(notificationPreference.isInAppEnabled()){
            log.info("Saving job status notification for user {} and application {}", notifyTo, applicationId);

            notificationRepository.save(notification);
        }

        if(notificationPreference.isEmailEnabled()){
            log.info("Publish application status email notification for user {}", notifyTo);
            publishEmailNotification(notification);
        }
    }


    @Async("careerHubTaskExecutor")
    @Transactional
    public void createJobStatusNotification(
            UUID userId,
            UUID jobId,
            String oldStatus,
            String newStatus
    ) throws Exception {
        log.info("Creating job status notification on thread");
        NotificationPreference notificationPreference = notificationPreferenceService.getOrCreate(userId);
        Notification notification = Notification.builder()
                .userId(userId)
                .title("Job Status Updated")
                .message(
                        "Your job ("+jobId+") application status changed from "
                                + oldStatus
                                + " to "
                                + newStatus
                )
                .type(NotificationType.JOB_STATUS_CHANGED)
                .read(false)
                .build();
        if(notificationPreference.isInAppEnabled()){
            log.info("Saving job status notification for user {} and job {}", userId, jobId);

            notificationRepository.save(notification);
        }

        if(notificationPreference.isEmailEnabled()){
            log.info("Publish job status email notification for user {}", userId);
            publishEmailNotification(notification);
        }
    }

    @Async("careerHubTaskExecutor")
    @Transactional
    public void createJobUpdatedNotification(UUID userId, UUID jobId) throws Exception {
        log.info("Creating job updated notification on thread");
        NotificationPreference notificationPreference = notificationPreferenceService.getOrCreate(userId);
        Notification notification = Notification.builder()
                .userId(userId)
                .title("Job Updated")
                .message("Your job ("+jobId+") has been updated ")
                .type(NotificationType.JOB_UPDATED)
                .read(false)
                .build();

        if(notificationPreference.isInAppEnabled()){
            log.info("Saving job updated notification for user {} and job {}", userId, jobId);

            notificationRepository.save(notification);
        }

        if(notificationPreference.isEmailEnabled()){
            log.info("Publish job updated email notification for user {}", userId);
            publishEmailNotification(notification);
        }
    }

    @Async("careerHubTaskExecutor")
    @Transactional
    public void createJobDeletedNotification(UUID userId, UUID jobId) throws Exception {
        log.info("Creating job deleted notification on thread");
        NotificationPreference notificationPreference = notificationPreferenceService.getOrCreate(userId);
        Notification notification = Notification.builder()
                .userId(userId)
                .title("Job Deleted")
                .message("Your job ("+jobId+") has been deleted ")
                .type(NotificationType.JOB_DELETED)
                .read(false)
                .build();
        if(notificationPreference.isInAppEnabled()){
            log.info("Saving job deleted notification for user {} and job {}", userId, jobId);

            notificationRepository.save(notification);
        }

        if(notificationPreference.isEmailEnabled()){
            log.info("Publish job deleted email notification for user {}", userId);
            publishEmailNotification(notification);
        }
    }

    public List<Notification> getNotifications() {
        UUID userId = authService.getCurrentAuthenticatedUserId();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getUnreadNotifications() {
        UUID userId = authService.getCurrentAuthenticatedUserId();
        return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
    }

    public long getUnreadCount() {
        UUID userId = authService.getCurrentAuthenticatedUserId();
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public void markAsRead(UUID notificationId) {
        UUID userId = authService.getCurrentAuthenticatedUserId();
        Notification notification =
                notificationRepository
                        .findByIdAndUserId(notificationId, userId)
                        .orElseThrow(() ->
                                new CustomException("Notification not found", HttpStatus.NOT_FOUND)
                        );

        notification.setRead(true);
    }

    @Transactional
    public void markAllAsRead() {
        UUID userId = authService.getCurrentAuthenticatedUserId();
        List<Notification> notifications = notificationRepository
                        .findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);

        notifications.forEach(
                notification -> notification.setRead(true)
        );
    }

    private void publishEmailNotification(Notification notification) throws Exception {
        User user = authService.getUser(notification.getUserId());
        // public Email Notification Event
        EmailNotificationEvent event = new EmailNotificationEvent(
                notification.getUserId().toString(),
                user.getEmail(),
                notification.getTitle(),
                notification.getMessage(),
                EventVersions.EMAIL_NOTIFICATION
        );

        eventProducer.publishEmailNotificationEvent(event);
    }
}

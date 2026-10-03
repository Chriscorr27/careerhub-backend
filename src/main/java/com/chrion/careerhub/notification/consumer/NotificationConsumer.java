package com.chrion.careerhub.notification.consumer;

import com.chrion.careerhub.constant.EventVersions;
import com.chrion.careerhub.event.model.*;
import com.chrion.careerhub.constant.KafkaGroup;
import com.chrion.careerhub.constant.KafkaTopic;
import com.chrion.careerhub.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {
    private final NotificationService notificationService;

    @RetryableTopic(
            attempts = "3",
            backOff = @BackOff(
                    delay = 2000,
                    multiplier = 2.0
            ),
            dltTopicSuffix = ".DLT"
    )
    @KafkaListener(
            topics = KafkaTopic.JOB_STATUS_CHANGED,
            groupId = KafkaGroup.CAREERHUB_NOTIFICATION
    )
    public void jobStatusChangeEventConsume(JobStatusChangedEvent event) throws Exception {
        if (event.eventVersion() != EventVersions.JOB_STATUS_CHANGED) {
            throw new IllegalArgumentException(
                    "Unsupported JobStatusChangedEvent version: "
                            + event.eventVersion()
            );
        }

        log.info(
                "Received JobStatusChangedEvent for notification: eventId={}, jobId={}, userId={}, previousStatus={}, newStatus={}",
                event.eventId(),
                event.jobId(),
                event.userId(),
                event.previousStatus(),
                event.newStatus()
        );

        // run Background Async Task
        notificationService.createJobStatusNotification(
                UUID.fromString(event.userId()),
                UUID.fromString(event.jobId()),
                event.previousStatus().toString(),
                event.newStatus().toString()
        );
    }

    @RetryableTopic(
            attempts = "3",
            backOff = @BackOff(
                    delay = 2000,
                    multiplier = 2.0
            ),
            dltTopicSuffix = ".DLT"
    )
    @KafkaListener(
            topics = KafkaTopic.APPLICATION_STATUS_CHANGED,
            groupId = KafkaGroup.CAREERHUB_NOTIFICATION
    )
    public void applicationStatusChangeEventConsume(ApplicationStatusChangedEvent event) throws Exception {
        if (event.eventVersion() != EventVersions.APPLICATION_STATUS_CHANGED) {
            throw new IllegalArgumentException(
                    "Unsupported ApplicationStatusChangedEvent version: "
                            + event.eventVersion()
            );
        }

        log.info(
                "Received ApplicationStatusChangedEvent for notification: eventId={}, applicationId={}, jobId={}, userId={}, previousStatus={}, newStatus={}",
                event.eventId(),
                event.applicationId(),
                event.jobId(),
                event.userId(),
                event.previousStatus(),
                event.newStatus()
        );

        // run Background Async Task
        notificationService.createApplicationStatusNotification(
                UUID.fromString(event.notifyTo()),
                UUID.fromString(event.applicationId()),
                event.previousStatus().toString(),
                event.newStatus().toString()
        );
    }


    @RetryableTopic(
            attempts = "3",
            backOff = @BackOff(
                    delay = 2000,
                    multiplier = 2.0
            ),
            dltTopicSuffix = ".DLT"
    )
    @KafkaListener(
            topics = KafkaTopic.JOB_CREATED,
            groupId = KafkaGroup.CAREERHUB_NOTIFICATION
    )
    public void jobCreatedEventConsume(JobCreatedEvent event) throws Exception {
        if (event.eventVersion() != EventVersions.JOB_CREATED) {
            throw new IllegalArgumentException(
                    "Unsupported JobCreatedEvent version: "
                            + event.eventVersion()
            );
        }

        log.info(
                "Received JobCreatedEvent for notification: eventId={}, jobId={}, userId={}",
                event.eventId(),
                event.jobId(),
                event.userId()
        );

        notificationService.createJobCreatedNotification(
                UUID.fromString(event.userId()),
                UUID.fromString(event.jobId())
        );
    }

    @RetryableTopic(
            attempts = "3",
            backOff = @BackOff(
                    delay = 2000,
                    multiplier = 2.0
            ),
            dltTopicSuffix = ".DLT"
    )
    @KafkaListener(
            topics = KafkaTopic.JOB_UPDATED,
            groupId = KafkaGroup.CAREERHUB_NOTIFICATION
    )
    public void jobUpdatedEventConsume(JobUpdatedEvent event) throws Exception {
        if (event.eventVersion() != EventVersions.JOB_UPDATED) {
            throw new IllegalArgumentException(
                    "Unsupported JobUpdatedEvent version: "
                            + event.eventVersion()
            );
        }

        log.info(
                "Received JobUpdatedEvent for notification: eventId={}, jobId={}, userId={}",
                event.eventId(),
                event.jobId(),
                event.userId()
        );

        notificationService.createJobUpdatedNotification(
                UUID.fromString(event.userId()),
                UUID.fromString(event.jobId())
        );
    }

    @RetryableTopic(
            attempts = "3",
            backOff = @BackOff(
                    delay = 2000,
                    multiplier = 2.0
            ),
            dltTopicSuffix = ".DLT"
    )
    @KafkaListener(
            topics = KafkaTopic.JOB_DELETED,
            groupId = KafkaGroup.CAREERHUB_NOTIFICATION
    )
    public void jobDeletedEventConsume(JobDeletedEvent event) throws Exception {
        if (event.eventVersion() != EventVersions.JOB_DELETED) {
            throw new IllegalArgumentException(
                    "Unsupported JobDeletedEvent version: "
                            + event.eventVersion()
            );
        }

        log.info(
                "Received JobDeletedEvent for notification: eventId={}, jobId={}, userId={}",
                event.eventId(),
                event.jobId(),
                event.userId()
        );

        notificationService.createJobDeletedNotification(
                UUID.fromString(event.userId()),
                UUID.fromString(event.jobId())
        );
    }
}

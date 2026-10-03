package com.chrion.careerhub.audit.consumer;

import com.chrion.careerhub.audit.service.AuditService;
import com.chrion.careerhub.constant.EventVersions;
import com.chrion.careerhub.event.model.*;
import com.chrion.careerhub.constant.KafkaGroup;
import com.chrion.careerhub.constant.KafkaTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditConsumer {

    private final AuditService auditService;

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
            groupId = KafkaGroup.CAREERHUB_AUDIT
    )
    public void jobStatusChangeEventConsume(JobStatusChangedEvent event) {
        if (event.eventVersion() != EventVersions.JOB_STATUS_CHANGED) {
            throw new IllegalArgumentException(
                    "Unsupported JobStatusChangedEvent version: "
                            + event.eventVersion()
            );
        }

        log.info(
                "Received JobStatusChangedEvent: eventId={}, jobId={}, userId={}, previousStatus={}, newStatus={}",
                event.eventId(),
                event.jobId(),
                event.userId(),
                event.previousStatus(),
                event.newStatus()
        );

        auditService.processJobStatusChangeEvent(event);

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
            groupId = KafkaGroup.CAREERHUB_AUDIT
    )
    public void applicationStatusChangeEventConsume(ApplicationStatusChangedEvent event) {
        if (event.eventVersion() != EventVersions.APPLICATION_STATUS_CHANGED) {
            throw new IllegalArgumentException(
                    "Unsupported ApplicationStatusChangedEvent version: "
                            + event.eventVersion()
            );
        }

        log.info(
                "Received ApplicationStatusChangedEvent: eventId={}, applicationId={}, jobId={}, userId={}, previousStatus={}, newStatus={}",
                event.eventId(),
                event.applicationId(),
                event.jobId(),
                event.userId(),
                event.previousStatus(),
                event.newStatus()
        );

        auditService.processApplicationStatusChangeEvent(event);

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
            groupId = KafkaGroup.CAREERHUB_AUDIT
    )
    public void jobCreatedEventConsume(JobCreatedEvent event) {
        if (event.eventVersion() != EventVersions.JOB_CREATED) {
            throw new IllegalArgumentException(
                    "Unsupported JobCreatedEvent version: "
                            + event.eventVersion()
            );
        }

        log.info(
                "Received JobCreatedEvent: eventId={}, jobId={}, userId={}",
                event.eventId(),
                event.jobId(),
                event.userId()
        );
        auditService.processJobCreatedEvent(event);
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
            groupId = KafkaGroup.CAREERHUB_AUDIT
    )
    public void jobUpdatedEventConsume(JobUpdatedEvent event) {
        if (event.eventVersion() != EventVersions.JOB_UPDATED) {
            throw new IllegalArgumentException(
                    "Unsupported JobUpdatedEvent version: "
                            + event.eventVersion()
            );
        }

        log.info(
                "Received JobUpdatedEvent: eventId={}, jobId={}, userId={}",
                event.eventId(),
                event.jobId(),
                event.userId()
        );
        auditService.processJobUpdatedEvent(event);
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
            groupId = KafkaGroup.CAREERHUB_AUDIT
    )
    public void jobDeletedEventConsume(JobDeletedEvent event) {
        if (event.eventVersion() != EventVersions.JOB_DELETED) {
            throw new IllegalArgumentException(
                    "Unsupported JobDeletedEvent version: "
                            + event.eventVersion()
            );
        }

        log.info(
                "Received JobDeletedEvent: eventId={}, jobId={}, userId={}",
                event.eventId(),
                event.jobId(),
                event.userId()
        );
        auditService.processJobDeletedEvent(event);
    }
}
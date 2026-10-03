package com.chrion.careerhub.audit.consumer;

import com.chrion.careerhub.event.model.*;
import com.chrion.careerhub.constant.KafkaGroup;
import com.chrion.careerhub.constant.KafkaTopic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventDltAuditConsumer {

    @KafkaListener(
            topics = KafkaTopic.JOB_STATUS_CHANGED_DLT,
            groupId = KafkaGroup.CAREERHUB_AUDIT_DLT
    )
    public void jobStatusChangeEventConsume(JobStatusChangedEvent event) {
        log.error(
                "JobStatusChangedEvent moved to DLT: eventId={}, jobId={}, previousStatus={}, newStatus={}",
                event.eventId(),
                event.jobId(),
                event.previousStatus(),
                event.newStatus()
        );
    }

    @KafkaListener(
            topics = KafkaTopic.APPLICATION_STATUS_CHANGED_DLT,
            groupId = KafkaGroup.CAREERHUB_AUDIT_DLT
    )
    public void applicationStatusChangeEventConsume(ApplicationStatusChangedEvent event) {
        log.error(
                "ApplicationStatusChangedEvent moved to DLT: eventId={}, applicationId={}, jobId={}, previousStatus={}, newStatus={}",
                event.eventId(),
                event.applicationId(),
                event.jobId(),
                event.previousStatus(),
                event.newStatus()
        );
    }

    @KafkaListener(
            topics = KafkaTopic.JOB_CREATED_DLT,
            groupId = KafkaGroup.CAREERHUB_AUDIT_DLT
    )
    public void jobCreatedEventConsume(JobCreatedEvent event) {
        log.error(
                "JobCreatedEvent moved to DLT: eventId={}, jobId={}, userId={}",
                event.eventId(),
                event.jobId(),
                event.userId()
        );
    }

    @KafkaListener(
            topics = KafkaTopic.JOB_UPDATED_DLT,
            groupId = KafkaGroup.CAREERHUB_AUDIT_DLT
    )
    public void jobUpdatedEventConsume(JobUpdatedEvent event) {
        log.error(
                "JobUpdatedEvent moved to DLT: eventId={}, jobId={}, userId={}",
                event.eventId(),
                event.jobId(),
                event.userId()
        );
    }

    @KafkaListener(
            topics = KafkaTopic.JOB_DELETED_DLT,
            groupId = KafkaGroup.CAREERHUB_AUDIT_DLT
    )
    public void jobDeletedEventConsume(JobDeletedEvent event) {
        log.error(
                "JobDeletedEvent moved to DLT: eventId={}, jobId={}, userId={}",
                event.eventId(),
                event.jobId(),
                event.userId()
        );
    }
}

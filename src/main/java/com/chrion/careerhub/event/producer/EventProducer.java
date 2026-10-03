package com.chrion.careerhub.event.producer;

import com.chrion.careerhub.event.model.*;
import com.chrion.careerhub.constant.KafkaTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishJobStatusChangeEvent(JobStatusChangedEvent event) {
        kafkaTemplate.send(KafkaTopic.JOB_STATUS_CHANGED, event.jobId(), event);
        log.info("JobStatusChange Event Produced");
    }

    public void publishJobCreatedEvent(JobCreatedEvent event) {
        kafkaTemplate.send(KafkaTopic.JOB_CREATED, event.jobId(), event);
        log.info("JobCreated Event Produced");
    }

    public void publishJobUpdatedEvent(JobUpdatedEvent event) {
        kafkaTemplate.send(KafkaTopic.JOB_UPDATED, event.jobId(), event);
        log.info("JobUpdated Event Produced");
    }

    public void publishJobDeletedEvent(JobDeletedEvent event) {
        kafkaTemplate.send(KafkaTopic.JOB_DELETED, event.jobId(), event);
        log.info("JobDeleted Event Produced");
    }

    public void publishEmailNotificationEvent(EmailNotificationEvent event) {
        kafkaTemplate.send(KafkaTopic.EMAIL_NOTIFICATION, event.userId(), event);
        log.info("EmailNotification Event Produced");
    }

    public void publishApplicationStatusChangeEvent(ApplicationStatusChangedEvent event) {
        kafkaTemplate.send(KafkaTopic.APPLICATION_STATUS_CHANGED, event.applicationId(), event);
        log.info("ApplicationStatusChange Event Produced");
    }
}

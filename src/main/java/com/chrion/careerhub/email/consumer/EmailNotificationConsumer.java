package com.chrion.careerhub.email.consumer;

import com.chrion.careerhub.constant.EventVersions;
import com.chrion.careerhub.constant.KafkaGroup;
import com.chrion.careerhub.constant.KafkaTopic;
import com.chrion.careerhub.email.service.EmailService;
import com.chrion.careerhub.event.model.EmailNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailNotificationConsumer {

    private final EmailService emailService;

    public EmailNotificationConsumer(EmailService emailService) {
        this.emailService = emailService;
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
            topics = KafkaTopic.EMAIL_NOTIFICATION,
            groupId = KafkaGroup.CAREERHUB_EMAIL_NOTIFICATION
    )
    public void emailNotificationEventConsume(EmailNotificationEvent event) throws Exception {
        if (event.eventVersion() != EventVersions.JOB_STATUS_CHANGED) {
            throw new IllegalArgumentException(
                    "Unsupported EmailNotificationEvent version: "
                            + event.eventVersion()
            );
        }

        log.info(
                "Received EmailNotificationEvent : userId={}, email={}, subject={}",
                event.userId(),
                event.email(),
                event.subject()
        );

        // run Background Async Task
        emailService.sendEmail(event.email(), event.subject(), event.body());
    }

}

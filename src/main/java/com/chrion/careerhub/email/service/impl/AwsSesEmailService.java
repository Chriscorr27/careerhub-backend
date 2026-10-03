package com.chrion.careerhub.email.service.impl;

import com.chrion.careerhub.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service("AwsSesEmailService")
@Slf4j
@RequiredArgsConstructor
public class AwsSesEmailService implements EmailService {

    private final SesClient sesClient;

    @Value("${aws.ses.from-email}")
    private String fromEmail;

    @Override
    public void sendEmail(String to, String subject, String body) {

        Destination destination = Destination.builder()
                .toAddresses(to)
                .build();

        Content subjectContent = Content.builder()
                .data(subject)
                .build();

        Content bodyContent = Content.builder()
                .data(body)
                .build();

        Body emailBody = Body.builder()
                .text(bodyContent)
                .build();

        Message message = Message.builder()
                .subject(subjectContent)
                .body(emailBody)
                .build();

        SendEmailRequest request = SendEmailRequest.builder()
                .source(fromEmail)
                .destination(destination)
                .message(message)
                .build();

        SendEmailResponse response = sesClient.sendEmail(request);

        log.info("Email sent successfully | messageId={}", response.messageId());
    }
}

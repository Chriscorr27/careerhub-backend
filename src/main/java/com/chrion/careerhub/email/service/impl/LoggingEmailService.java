package com.chrion.careerhub.email.service.impl;

import com.chrion.careerhub.email.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service("default")
@Slf4j
public class LoggingEmailService implements EmailService {

    @Override
    public void sendEmail(String to, String subject, String body) {

        log.info(
                "Email notification | to={} | subject={} | body={}",
                to,
                subject,
                body
        );

    }
}

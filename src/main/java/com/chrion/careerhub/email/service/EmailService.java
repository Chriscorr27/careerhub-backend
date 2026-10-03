package com.chrion.careerhub.email.service;

public interface EmailService {

    void sendEmail(String to, String subject, String body);
}

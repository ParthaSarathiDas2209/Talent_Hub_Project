package com.jobportal.talenthub.service.impl;

import com.jobportal.talenthub.entity.ApplicationStatus;
import com.jobportal.talenthub.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String username;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void SendApplicationsStatusEmail(
            String recipientEmail, String applicantName,
            String jobTitle, ApplicationStatus status
    ) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(username);
        message.setTo(recipientEmail);
        message.setSubject(jobTitle);
        message.setText(status.toString());
        javaMailSender.send(message);

    }
}

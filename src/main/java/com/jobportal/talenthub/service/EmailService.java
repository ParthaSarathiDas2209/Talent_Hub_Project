package com.jobportal.talenthub.service;

import com.jobportal.talenthub.entity.ApplicationStatus;

public interface EmailService {

    void SendApplicationsStatusEmail(
            String recipientEmail,
            String applicantName,
            String jobTitle,
            ApplicationStatus status
    );
}

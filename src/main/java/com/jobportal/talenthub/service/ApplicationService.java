package com.jobportal.talenthub.service;

import com.jobportal.talenthub.dto.ApplicationRequestDto;
import com.jobportal.talenthub.dto.ApplicationResponseDto;
import com.jobportal.talenthub.entity.ApplicationStatus;

import java.util.List;

public interface ApplicationService {

    // =========================================================
    // CANDIDATE / APPLICATION MANAGEMENT
    // =========================================================

    // Apply the logged-in job seeker to a job.
    ApplicationResponseDto applyJob(
            ApplicationRequestDto applicationRequestDto,
            String email
    );

    // Get a specific application owned by the logged-in user.
    ApplicationResponseDto getApplicationById(
            Long id,
            String email
    );

    // Get all applications submitted by the logged-in user.
    List<ApplicationResponseDto> getAllApplications(
            String email
    );

    // Soft-delete/withdraw an application owned by the logged-in user.
    void deleteApplication(
            Long id,
            String email
    );


    // =========================================================
    // RECRUITER APPLICATION MANAGEMENT
    // =========================================================

    // Update an application's status.
    // Only the recruiter who owns the related job can perform this.
    ApplicationResponseDto updateApplicationStatus(
            Long applicationId,
            ApplicationStatus applicationStatus,
            String email
    );

    // Get all applications submitted to jobs owned by the logged-in recruiter.
    List<ApplicationResponseDto> getRecruiterApplications(
            String email
    );
}
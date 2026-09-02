package com.jobportal.talenthub.service.impl;

import com.jobportal.talenthub.dto.RecruiterDashboardDto;
import com.jobportal.talenthub.entity.ApplicationStatus;
import com.jobportal.talenthub.entity.JobStatus;
import com.jobportal.talenthub.entity.User;
import com.jobportal.talenthub.exception.ResourceNotFoundException;
import com.jobportal.talenthub.repository.ApplicationRepository;
import com.jobportal.talenthub.repository.JobRepository;
import com.jobportal.talenthub.repository.UserRepository;
import com.jobportal.talenthub.service.RecruiterDashboardService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class RecruiterDashboardServiceImpl implements RecruiterDashboardService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public RecruiterDashboardServiceImpl(ApplicationRepository applicationRepository, JobRepository jobRepository, UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    @Override
    public RecruiterDashboardDto getRecruiterDashboard() {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Recruiter not found"
                        )
                );

        Long recruiterId = recruiter.getId();

        long totalJobs = jobRepository.countByRecruiterIdAndDeletedFalse(recruiterId);

        long draftJobs = jobRepository.countByRecruiterIdAndStatusAndDeletedFalse(
                recruiterId,
                JobStatus.DRAFT
        );

        long pendingReviewJobs = jobRepository.countByRecruiterIdAndStatusAndDeletedFalse(
                recruiterId, JobStatus.PENDING_REVIEW
        );

        long activeJobs = jobRepository.countByRecruiterIdAndStatusAndDeletedFalse(
                recruiterId, JobStatus.ACTIVE
        );

        long rejectedJobs = jobRepository.countByRecruiterIdAndStatusAndDeletedFalse(
                recruiterId, JobStatus.REJECTED
        );

        long closedJobs = jobRepository.countByRecruiterIdAndStatusAndDeletedFalse(
                recruiterId, JobStatus.CLOSED
        );

        long archivedJobs = jobRepository.countByRecruiterIdAndStatusAndDeletedFalse(
                recruiterId, JobStatus.ARCHIVED
        );

        long totalApplications = applicationRepository.countByJobRecruiterId(
                recruiterId
        );

        long appliedApplications = applicationRepository.countByJobRecruiterIdAndStatus(
                recruiterId,
                ApplicationStatus.APPLIED
        );

        long shortlistedApplications = applicationRepository.countByJobRecruiterIdAndStatus(
                recruiterId,
                ApplicationStatus.SHORTLISTED
        );

        long interviewedApplications = applicationRepository.countByJobRecruiterIdAndStatus(
                recruiterId,
                ApplicationStatus.INTERVIEWED
        );

        long acceptedApplications = applicationRepository.countByJobRecruiterIdAndStatus(
                recruiterId,
                ApplicationStatus.ACCEPTED
        );

        long rejectedApplications = applicationRepository.countByJobRecruiterIdAndStatus(
                recruiterId,
                ApplicationStatus.REJECTED
        );

        long withdrawnApplications = applicationRepository.countByJobRecruiterIdAndStatus(
                recruiterId,
                ApplicationStatus.WITHDRAWN
        );

        return new RecruiterDashboardDto(
                totalJobs,
                draftJobs,
                pendingReviewJobs,
                activeJobs,
                rejectedJobs,
                closedJobs,
                archivedJobs,

                totalApplications,
                appliedApplications,
                shortlistedApplications,
                interviewedApplications,
                acceptedApplications,
                rejectedApplications,
                withdrawnApplications
        );
    }
}

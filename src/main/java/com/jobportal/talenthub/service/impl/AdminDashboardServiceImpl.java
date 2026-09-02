package com.jobportal.talenthub.service.impl;

import com.jobportal.talenthub.dto.AdminDashboardDto;
import com.jobportal.talenthub.entity.ApplicationStatus;
import com.jobportal.talenthub.entity.JobStatus;
import com.jobportal.talenthub.entity.Role;
import com.jobportal.talenthub.entity.UserStatus;
import com.jobportal.talenthub.repository.ApplicationRepository;
import com.jobportal.talenthub.repository.JobRepository;
import com.jobportal.talenthub.repository.UserRepository;
import com.jobportal.talenthub.service.AdminDashboardService;
import org.springframework.stereotype.Service;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public AdminDashboardServiceImpl(ApplicationRepository applicationRepository, JobRepository jobRepository, UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }


    @Override
    public AdminDashboardDto getAdminDashboard() {

        long totalUsers = userRepository.countByDeletedFalse();

        long totalJobSeekers = userRepository.countByRoleAndDeletedFalse(
                Role.JOB_SEEKER
        );

        long totalRecruiters = userRepository.countByRoleAndDeletedFalse(
                Role.RECRUITER
        );

        long activeUsers = userRepository.countByStatusAndDeletedFalse(
                UserStatus.ACTIVE
        );

        long inactiveUsers = userRepository.countByStatusAndDeletedFalse(
                UserStatus.INACTIVE
        );

        long suspendedUsers = userRepository.countByStatusAndDeletedFalse(
                UserStatus.SUSPENDED
        );

        long deactivatedUsers = userRepository.countByStatusAndDeletedFalse(
                UserStatus.DEACTIVATED
        );

        long totalJobs = jobRepository.countByDeletedFalse();

        long draftJobs = jobRepository.countByStatusAndDeletedFalse(
                JobStatus.DRAFT
        );

        long pendingReviewJobs = jobRepository.countByStatusAndDeletedFalse(
                JobStatus.PENDING_REVIEW
        );

        long activeJobs = jobRepository.countByStatusAndDeletedFalse(
                JobStatus.ACTIVE
        );

        long rejectedJobs = jobRepository.countByStatusAndDeletedFalse(
                JobStatus.REJECTED
        );

        long closedJobs = jobRepository.countByStatusAndDeletedFalse(
                JobStatus.CLOSED
        );

        long archivedJobs = jobRepository.countByStatusAndDeletedFalse(
                JobStatus.ARCHIVED
        );

        long deletedJobs = jobRepository.countByDeletedTrue();

        long totalApplications = applicationRepository.count();

        long appliedApplications = applicationRepository.countByStatus(
                ApplicationStatus.APPLIED
        );

        long shortlistedApplications = applicationRepository.countByStatus(
                ApplicationStatus.SHORTLISTED
        );

        long interviewedApplications = applicationRepository.countByStatus(
                ApplicationStatus.INTERVIEWED
        );

        long acceptedApplications = applicationRepository.countByStatus(
                ApplicationStatus.ACCEPTED
        );

        long rejectedApplications = applicationRepository.countByStatus(
                ApplicationStatus.REJECTED
        );

        long withdrawnApplications = applicationRepository.countByStatus(
                ApplicationStatus.WITHDRAWN
        );

        return new AdminDashboardDto(
                totalUsers,
                totalJobSeekers,
                totalRecruiters,
                activeUsers,
                inactiveUsers,
                suspendedUsers,
                deactivatedUsers,
                totalJobs,
                draftJobs,
                pendingReviewJobs,
                activeJobs,
                rejectedJobs,
                closedJobs,
                archivedJobs,
                deletedJobs,
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
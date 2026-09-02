package com.jobportal.talenthub.dto;

public record AdminDashboardDto(

        // =========================
        // USERS
        // =========================

        long totalUsers,
        long totalJobSeekers,
        long totalRecruiters,

        long activeUsers,
        long inactiveUsers,
        long suspendedUsers,
        long deactivatedUsers,

        // =========================
        // JOBS
        // =========================

        long totalJobs,
        long draftJobs,
        long pendingReviewJobs,
        long activeJobs,
        long rejectedJobs,
        long closedJobs,
        long archivedJobs,
        long deletedJobs,

        // =========================
        // APPLICATIONS
        // =========================

        long totalApplications,
        long appliedApplications,
        long shortlistedApplications,
        long interviewedApplications,
        long acceptedApplications,
        long rejectedApplications,
        long withdrawnApplications
) {
}

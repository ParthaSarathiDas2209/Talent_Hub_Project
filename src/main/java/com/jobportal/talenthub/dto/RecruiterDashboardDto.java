package com.jobportal.talenthub.dto;

public record RecruiterDashboardDto(
        long totalJobs,
        long draftJobs,
        long pendingReviewJobs,
        long activeJobs,
        long rejectedJobs,
        long closedJobs,
        long archivedJobs,

        long totalApplications,
        long appliedApplications,
        long shortlistedApplications,
        long interviewedApplications,
        long acceptedApplications,
        long rejectedApplications,
        long withdrawnApplications
) {
}
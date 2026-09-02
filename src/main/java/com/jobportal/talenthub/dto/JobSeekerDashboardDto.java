package com.jobportal.talenthub.dto;

public record JobSeekerDashboardDto(
        long totalApplications,
        long appliedApplications,
        long shortlistedApplications,
        long interviewedApplications,
        long acceptedApplications,
        long rejectedApplications,
        long withdrawnApplications
) {
}
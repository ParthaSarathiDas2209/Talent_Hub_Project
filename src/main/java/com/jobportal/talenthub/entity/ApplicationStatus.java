package com.jobportal.talenthub.entity;
// Enum representing the lifecycle/status of a job application.

public enum ApplicationStatus {

    APPLIED,
    // Candidate has successfully submitted the application.

    SHORTLISTED,
    // Recruiter has shortlisted the candidate.

    INTERVIEWED,
    // Candidate has gone through the interview stage.

    ACCEPTED,
    // Candidate has been selected/accepted.

    REJECTED,
    // Application has been rejected.

    WITHDRAWN
    // Candidate has withdrawn their application.
}
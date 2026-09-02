package com.jobportal.talenthub.entity;

public enum Role {

    JOB_SEEKER,
    // Regular user who can browse jobs and apply for jobs.

    RECRUITER,
    // User who can create/manage their own jobs
    // and manage applications for those jobs.

    ADMIN
    // Administrator who can manage users, jobs,
    // and applications through admin endpoints.
}

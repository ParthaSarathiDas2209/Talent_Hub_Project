package com.jobportal.talenthub.entity;

// Represents the current lifecycle/status of a job posting.
// The status controls where the job is in its publishing process.
public enum JobStatus {

    DRAFT,
    // Job has been created but is not yet publicly active.

    PENDING_REVIEW,
    // Job has been submitted and is waiting for review/approval.

    ACTIVE,
    // Job is currently published and available to candidates.

    REJECTED,
    // Job posting has been rejected during the review process.

    CLOSED,
    // Job is no longer accepting applications.

    ARCHIVED,
    // Job is kept for historical/reference purposes
    // but is no longer an active job posting.

    DELETED
    // Job has been soft-deleted.
    // The database record remains, but the job is treated as deleted.
}
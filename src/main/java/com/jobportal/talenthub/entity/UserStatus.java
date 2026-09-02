package com.jobportal.talenthub.entity;

public enum UserStatus {

    ACTIVE, // User account is active and can normally use the application.

    INACTIVE,
    // User account is currently inactive.
    // Can be used when an account is temporarily disabled
    // without treating it as deleted.

    SUSPENDED,
    // User account has been suspended, for example due to
    // an administrative action or policy violation.

    DEACTIVATED
    // User account has been deactivated.
    // In TalentHub, this is also used when a user is soft-deleted.
}

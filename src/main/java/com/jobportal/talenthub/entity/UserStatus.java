package com.jobportal.talenthub.entity;

// Represents the current account status of a TalentHub user.
// UserStatus can be used for account lifecycle and access control.
public enum UserStatus {

    ACTIVE,
    // User account is active and can normally use the application.

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

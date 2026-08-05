package com.jobportal.talenthub.exception;

// Custom exception used when the current authenticated user
// does not have permission to perform an operation.
//
// Example:
//
// JOB_SEEKER
//    ↓
// Try to update recruiter application status
//    ↓
// AccessDeniedException
//
// Usually handled as HTTP 403 FORBIDDEN
// by GlobalExceptionHandler.
public class AccessDeniedException extends RuntimeException {

    // Constructor receives the authorization error message.
    public AccessDeniedException(String message) {

        // Pass the message to RuntimeException.
        super(message);
    }
}
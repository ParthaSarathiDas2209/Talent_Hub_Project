package com.jobportal.talenthub.exception;

// Custom exception used when a user tries to perform
// an operation that would create a duplicate application.
//
// Example:
//
// User A
//   ↓
// Apply to Job 10
//   ↓
// Apply to Job 10 again
//   ↓
// DuplicateApplicationException
//
// This protects the application from duplicate
// user-job application records.
public class DuplicateApplicationException extends RuntimeException {

    // Constructor receives the duplicate-operation error message.
    public DuplicateApplicationException(String message) {

        // Pass the message to RuntimeException.
        super(message);
    }
}
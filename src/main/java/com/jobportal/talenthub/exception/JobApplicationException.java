package com.jobportal.talenthub.exception;

// Custom runtime exception used for general business-rule violations.
//
// Examples:
// - Invalid application operation
// - Job is not active
// - User account is not active
// - Application cannot be updated further
//
// RuntimeException means the exception does not need to be
// explicitly declared or caught by every calling method.
public class JobApplicationException extends RuntimeException {

    // Constructor receives the error message
    // that will be returned/used when the exception is handled.
    public JobApplicationException(String message) {

        // Pass the custom message to RuntimeException.
        super(message);
    }
}
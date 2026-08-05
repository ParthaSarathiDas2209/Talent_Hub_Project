package com.jobportal.talenthub.exception;

// Custom exception used when a requested resource
// cannot be found in the database.
//
// Examples:
// - User ID does not exist
// - Job ID does not exist
// - Application ID does not exist
//
// Usually handled as HTTP 404 NOT_FOUND
// by GlobalExceptionHandler.
public class ResourceNotFoundException extends RuntimeException {

    // Constructor receives the resource-specific error message.
    public ResourceNotFoundException(String message) {

        // Store the message inside the parent RuntimeException.
        super(message);
    }
}
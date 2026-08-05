package com.jobportal.talenthub.exception;

// Custom exception used when authentication credentials
// provided during login are invalid.
//
// Example:
//
// Email + Password
//       ↓
// Authentication fails
//       ↓
// InvalidCredentialsException
//
// Usually handled as an authentication error
// and mapped to an appropriate HTTP response.
public class InvalidCredentialsException extends RuntimeException {

    // Constructor receives the authentication error message.
    public InvalidCredentialsException(String message) {

        // Pass the message to RuntimeException.
        super(message);
    }
}
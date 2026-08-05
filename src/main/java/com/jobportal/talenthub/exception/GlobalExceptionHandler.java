package com.jobportal.talenthub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


// Applies exception-handling logic globally to all REST controllers.
//
// Instead of writing try-catch blocks inside every controller,
// exceptions can be handled centrally here.
//
// Flow:
//
// Controller
//     ↓
// Service
//     ↓
// Exception thrown
//     ↓
// GlobalExceptionHandler
//     ↓
// HTTP Error Response
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles ResourceNotFoundException.
    //
    // Example:
    //
    // UserService
    //     ↓
    // User not found
    //     ↓
    // throw ResourceNotFoundException
    //     ↓
    // HTTP 404 NOT_FOUND
    //
    // Used when the requested resource does not exist.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(
            ResourceNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }


    // Handles AccessDeniedException.
    //
    // Used when the user is authenticated
    // but does not have permission to perform the operation.
    //
    // Example:
    //
    // JOB_SEEKER
    //     ↓
    // Try to update recruiter-only application status
    //     ↓
    // AccessDeniedException
    //     ↓
    // HTTP 403 FORBIDDEN
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(
            AccessDeniedException ex) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ex.getMessage());
    }


    // Catch-all handler for unexpected exceptions.
    //
    // If an exception is not handled by one of the
    // more specific @ExceptionHandler methods above,
    // this method handles it.
    //
    // HTTP 500 means an unexpected server-side error occurred.
    //
    // Important:
    // In a production application, we normally should NOT
    // expose internal exception details directly to the client.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(
            Exception ex) {

        return new ResponseEntity<>(
                "Something went wrong!: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}
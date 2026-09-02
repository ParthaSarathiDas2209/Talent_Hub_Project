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

    // =========================================================
    // 404 - RESOURCE NOT FOUND
    // =========================================================

    // Handles ResourceNotFoundException.
    //
    // Used when the requested resource does not exist.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(
            ResourceNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }


    // =========================================================
    // 403 - ACCESS DENIED
    // =========================================================

    // Handles AccessDeniedException.
    //
    // Used when the user is authenticated
    // but does not have permission to perform the operation.
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(
            AccessDeniedException ex) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ex.getMessage());
    }


    // =========================================================
    // 409 - DUPLICATE APPLICATION
    // =========================================================

    // Handles DuplicateApplicationException.
    //
    // Used when the user tries to create
    // a duplicate application.
    @ExceptionHandler(DuplicateApplicationException.class)
    public ResponseEntity<String> handleDuplicateApplicationException(
            DuplicateApplicationException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }


    // =========================================================
    // 401 - INVALID CREDENTIALS
    // =========================================================

    // Handles InvalidCredentialsException.
    //
    // Used when authentication credentials are invalid.
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentialsException(
            InvalidCredentialsException ex) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ex.getMessage());
    }


    // =========================================================
    // 400 - BUSINESS RULE VIOLATION
    // =========================================================

    // Handles JobApplicationException.
    //
    // Used when the request violates
    // an application business rule.
    @ExceptionHandler(JobApplicationException.class)
    public ResponseEntity<String> handleJobApplicationException(
            JobApplicationException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }


    // =========================================================
    // 500 - UNEXPECTED SERVER ERROR
    // =========================================================

    // Catch-all handler for unexpected exceptions.
    //
    // If an exception is not handled by one of the
    // specific handlers above, this method handles it.
    //
    // HTTP 500 means an unexpected server-side
    // error occurred.
    //
    // We do not expose the internal exception message
    // to the client.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(
            Exception ex) {

        return new ResponseEntity<>(
                "Something went wrong. Please try again later.",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}
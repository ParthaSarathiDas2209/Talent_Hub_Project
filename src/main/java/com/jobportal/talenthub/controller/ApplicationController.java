package com.jobportal.talenthub.controller;

import com.jobportal.talenthub.dto.ApplicationRequestDto;
import com.jobportal.talenthub.dto.ApplicationResponseDto;
import com.jobportal.talenthub.entity.ApplicationStatus;
import com.jobportal.talenthub.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    // Service layer responsible for application business logic.
    private final ApplicationService applicationService;


    // Constructor Injection:
    // Spring injects the ApplicationService dependency.
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }


    /*
     * ============================================================
     * APPLY FOR A JOB
     * ============================================================
     *
     * POST /api/applications
     *
     * Flow:
     *
     * Client
     *   ↓
     * ApplicationRequestDto
     *   ↓
     * Authentication
     *   ↓
     * Logged-in user's email
     *   ↓
     * ApplicationService
     *   ↓
     * Business validation
     *   ↓
     * Application created
     */

    @PostMapping
    public ResponseEntity<ApplicationResponseDto> applyJob(

            // @Valid triggers Jakarta Bean Validation
            // defined inside ApplicationRequestDto.
            @Valid @RequestBody ApplicationRequestDto applicationRequestDto,

            // Authentication contains information about
            // the currently authenticated user.
            Authentication authentication) {

        /*
         * The authenticated user's email is obtained from:
         *
         * authentication.getName()
         *
         * This avoids accepting userId from the client
         * and prevents users from applying on behalf of another user.
         */
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        applicationService.applyJob(
                                applicationRequestDto,
                                authentication.getName()
                        )
                );
    }


    /*
     * ============================================================
     * GET APPLICATION BY ID
     * ============================================================
     *
     * GET /api/applications/{id}
     *
     * The service performs the ownership check to ensure
     * the logged-in user can only access their own application.
     */

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponseDto> getApplicationById(

            // ID of the application requested by the client.
            @PathVariable Long id,

            // Used to identify the currently logged-in user.
            Authentication authentication) {

        return ResponseEntity.ok(
                applicationService.getApplicationById(
                        id,
                        authentication.getName()
                )
        );
    }


    /*
     * ============================================================
     * GET ALL APPLICATIONS OF LOGGED-IN USER
     * ============================================================
     *
     * GET /api/applications
     *
     * Returns only applications belonging to the
     * currently authenticated user.
     */

    @GetMapping
    public ResponseEntity<List<ApplicationResponseDto>> getAllApplications(

            // Used to identify the logged-in applicant.
            Authentication authentication) {

        return ResponseEntity.ok(
                applicationService.getAllApplications(
                        authentication.getName()
                )
        );
    }


    /*
     * ============================================================
     * WITHDRAW / DELETE APPLICATION
     * ============================================================
     *
     * DELETE /api/applications/{id}
     *
     * The controller passes the logged-in user's identity
     * to the service so the service can perform the ownership check.
     *
     * The service performs a soft delete instead of
     * physically deleting the database record.
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteApplicationById(

            // ID of the application to withdraw.
            @PathVariable Long id,

            // Used for ownership validation.
            Authentication authentication) {

        applicationService.deleteApplication(
                id,
                authentication.getName()
        );

        return ResponseEntity.ok(
                "Application deleted successfully ! "
        );
    }


    /*
     * ============================================================
     * UPDATE APPLICATION STATUS
     * ============================================================
     *
     * PATCH /api/applications/{applicationId}/status
     *
     * Example:
     *
     * PATCH /api/applications/10/status?applicationStatus=SHORTLISTED
     *
     * Only the recruiter who owns the related job
     * should be allowed to update the application status.
     *
     * The service layer performs:
     *
     * Recruiter validation
     *        ↓
     * Account status validation
     *        ↓
     * Job ownership validation
     *        ↓
     * Application validation
     *        ↓
     * Status transition validation
     */

    @PatchMapping("/{applicationId}/status")
    public ResponseEntity<ApplicationResponseDto> updateApplicationStatus(

            // ID of the application whose status is being changed.
            @PathVariable Long applicationId,

            // Requested new application status.
            //
            // Example:
            // ?applicationStatus=SHORTLISTED
            @RequestParam ApplicationStatus applicationStatus,

            // Used to identify the logged-in recruiter.
            Authentication authentication) {

        ApplicationResponseDto updatedApplication =
                applicationService.updateApplicationStatus(
                        applicationId,
                        applicationStatus,
                        authentication.getName()
                );

        // Return the updated application to the client.
        return ResponseEntity.ok(updatedApplication);
    }


    /*
     * ============================================================
     * GET APPLICATIONS FOR LOGGED-IN RECRUITER
     * ============================================================
     *
     * GET /api/applications/recruiter
     *
     * Returns applications submitted to jobs
     * owned by the currently authenticated recruiter.
     *
     * Flow:
     *
     * Logged-in Recruiter
     *        ↓
     * Recruiter's Jobs
     *        ↓
     * Applications for those Jobs
     */

    @GetMapping("/recruiter")
    public ResponseEntity<List<ApplicationResponseDto>> getRecruiterApplications(

            // Used to identify the logged-in recruiter.
            Authentication authentication) {

        return ResponseEntity.ok(
                applicationService.getRecruiterApplications(
                        authentication.getName()
                )
        );
    }
}
package com.jobportal.talenthub.service.impl;

import com.jobportal.talenthub.dto.ApplicationRequestDto;
import com.jobportal.talenthub.dto.ApplicationResponseDto;
import com.jobportal.talenthub.entity.*;
import com.jobportal.talenthub.exception.AccessDeniedException;
import com.jobportal.talenthub.exception.DuplicateApplicationException;
import com.jobportal.talenthub.exception.JobApplicationException;
import com.jobportal.talenthub.exception.ResourceNotFoundException;
import com.jobportal.talenthub.mapper.ApplicationMapper;
import com.jobportal.talenthub.repository.ApplicationRepository;
import com.jobportal.talenthub.repository.JobRepository;
import com.jobportal.talenthub.repository.UserRepository;
import com.jobportal.talenthub.service.ApplicationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    // Repository for performing CRUD operations on Application entity.
    private final ApplicationRepository applicationRepository;

    // Repository for finding users.
    // Mainly used to find the currently logged-in user using email.
    private final UserRepository userRepository;

    // Repository for finding the job to which the user wants to apply.
    private final JobRepository jobRepository;


    // Constructor Injection
    public ApplicationServiceImpl(
            ApplicationRepository applicationRepository,
            UserRepository userRepository,
            JobRepository jobRepository) {

        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }


    /*
     * ============================================================
     * APPLY FOR A JOB
     * ============================================================
     *
     * Flow:
     *
     * JWT
     *   ↓
     * Authentication
     *   ↓
     * Logged-in user's email
     *   ↓
     * Find User
     *   ↓
     * Find Job
     *   ↓
     * Check duplicate application
     *   ↓
     * Create Application
     */

    @Override
    public ApplicationResponseDto applyJob(
            ApplicationRequestDto applicationRequestDto,
            String email) {

        // ==========================================================
        // STEP 1 : Identify the logged-in user from the JWT token.
        // The email comes from Authentication.getName().
        // This user will be the applicant.
        // ==========================================================
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Logged-in user not found"
                        )
                );

        // ==========================================================
        // STEP 2 : USER VALIDATION
        // Ensure the logged-in user is allowed to apply.
        // ==========================================================

        // Only JOB_SEEKER accounts can apply for jobs.
        if (user.getRole() != Role.JOB_SEEKER) {
            throw new AccessDeniedException(
                    "Only Job Seekers can apply for jobs."
            );
        }

        // User account must be ACTIVE.
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AccessDeniedException(
                    "Your account is not active."
            );
        }

        // ==========================================================
        // STEP 3 : Find the job using the provided jobId.
        // ==========================================================
        Job job = jobRepository.findById(
                        applicationRequestDto.jobId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found with id: "
                                        + applicationRequestDto.jobId()
                        )
                );

        // ==========================================================
        // STEP 4 : JOB VALIDATION
        // Verify whether this job can currently accept applications.
        // ==========================================================

        // Recruiters cannot apply to their own jobs.
        if (job.getRecruiter().getId().equals(user.getId())) {
            throw new JobApplicationException(
                    "You cannot apply to your own job."
            );
        }

        // Soft-deleted jobs should not accept applications.
        if (job.isDeleted()) {
            throw new JobApplicationException(
                    "This job has been removed and is no longer accepting applications."
            );
        }

        // Only ACTIVE jobs are open for applications.
        if (job.getStatus() != JobStatus.ACTIVE) {
            throw new JobApplicationException(
                    "You can only apply for an active job."
            );
        }

        // Current timestamp used for application window validation.
        LocalDateTime now = LocalDateTime.now();

        // Application period has not started yet.
        if (job.getApplicationStartTime() != null &&
                now.isBefore(job.getApplicationStartTime())) {
            throw new JobApplicationException(
                    "Applications for this job have not started yet."
            );
        }

        // Application period has already ended.
        if (job.getApplicationEndTime() != null &&
                now.isAfter(job.getApplicationEndTime())) {
            throw new JobApplicationException(
                    "The application period for this job has already ended."
            );
        }

        // ==========================================================
        // STEP 5 : APPLICATION VALIDATION
        // Prevent duplicate applications by the same user
        // for the same job.
        // ==========================================================
        if (applicationRepository.existsByUserAndJob(user, job)) {
            throw new DuplicateApplicationException(
                    "You have already applied for this job."
            );
        }

        // ==========================================================
        // STEP 6 : Create a new Application entity.
        // ==========================================================
        Application application = new Application();

        // Associate the logged-in user with the application.
        application.setUser(user);

        // Associate the selected job with the application.
        application.setJob(job);

        // Every newly created application starts with APPLIED status.
        application.setStatus(ApplicationStatus.APPLIED);

        // ==========================================================
        // STEP 7 : Persist the application in the database.
        // ==========================================================
        Application savedApplication =
                applicationRepository.save(application);

        // ==========================================================
        // STEP 8 : Convert Entity → Response DTO and return it.
        // ==========================================================
        return ApplicationMapper.toApplicationResponseDto(savedApplication);
    }


    /*
     * ============================================================
     * GET APPLICATION BY ID
     * ============================================================
     *
     * Current ownership rule:
     *
     * Application
     *      ↓
     * Applicant/User
     *      ↓
     * Must be logged-in user
     */

    @Override
    public ApplicationResponseDto getApplicationById(
            Long id,
            String email) {

        // id represents the Application ID.
        //
        // id
        //   ↓
        // Find the requested application.
        Application application =
                applicationRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Application not found with id: "
                                                + id
                                )
                        );


        // email represents the person making the request.
        //
        // email
        //   ↓
        // Find the currently logged-in user.
        User loggedInUser =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Logged-in user not found"
                                )
                        );


        // Check whether the application belongs
        // to the currently logged-in user.
        //
        // Application's User
        //          VS
        // Logged-in User
        if (!application.getUser().getId()
                .equals(loggedInUser.getId())) {


            // The application belongs to another user.
            throw new AccessDeniedException(
                    "You are not allowed to view this application"
            );
        }


        // If ownership check passes,
        // return the application.
        return ApplicationMapper
                .toApplicationResponseDto(application);
    }


    /*
     * ============================================================
     * GET ALL APPLICATIONS OF LOGGED-IN USER
     * ============================================================
     *
     * This endpoint is for the applicant.
     *
     * Logged-in User
     *       ↓
     * Find all applications created by this user.
     */

    @Override
    public List<ApplicationResponseDto> getAllApplications(
            String email) {


        // Find the currently logged-in user.
        User loggedInUser =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Logged-in user not found"
                                )
                        );


        // Find only applications belonging
        // to the logged-in user.
        //
        // This prevents one user from seeing
        // another user's applications.
        List<Application> applications =
                applicationRepository
                        .findAllByUserAndDeletedFalse(loggedInUser);


        // Convert every Application entity
        // into ApplicationResponseDto.
        return applications.stream()
                .map(ApplicationMapper::toApplicationResponseDto)
                .toList();
    }


    /*
     * ============================================================
     * DELETE APPLICATION
     * ============================================================
     *
     * Only the applicant who created the application
     * can delete it.
     */

    @Override
    public void deleteApplication(
            Long id,
            String email) {


        // id
        //   ↓
        // Find the application to delete.
        Application application =
                applicationRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Application not found with id: "
                                                + id
                                )
                        );


        // email
        //   ↓
        // Find the person making the request.
        User loggedInUser =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Logged-in user not found"
                                )
                        );


        // Check whether the logged-in user
        // is the owner/applicant of this application.

        // Application Applicant
        //          VS
        // Logged-in User
        if (!application.getUser().getId()
                .equals(loggedInUser.getId())) {


            // Another user cannot delete
            // someone else's application.
            throw new AccessDeniedException(
                    "You are not allowed to delete this application"
            );
        }

        if (application.isDeleted()) {
            throw new JobApplicationException(
                    "This application has been removed and is no longer accepting applications."
            );
        }

//        // Ownership check passed.

//        applicationRepository.delete(application);

        application.setStatus(ApplicationStatus.WITHDRAWN);
        application.setDeleted(true);
        application.setDeletedAt(LocalDateTime.now());
        applicationRepository.save(application);
    }


    @Override
    public ApplicationResponseDto updateApplicationStatus(
            Long applicationId,
            ApplicationStatus applicationStatus,
            String email) {

        // ==========================================================
        // STEP 1 : Find the application using the applicationId.
        // This is the application whose status will be updated.
        // ==========================================================
        Application application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Application not found with id: "
                                                + applicationId
                                )
                        );

        // ==========================================================
        // STEP 2 : Find the currently logged-in user.
        // The email comes from the authenticated JWT token.
        // ==========================================================
        User loggedInUser =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Logged-in user not found"
                                )
                        );

        // ==========================================================
        // STEP 3 : RECRUITER VALIDATION
        // Ensure only ACTIVE recruiters can update
        // an application's status.
        // ==========================================================

        // Only recruiters are allowed to update application status.
        if (loggedInUser.getRole() != Role.RECRUITER) {
            throw new AccessDeniedException(
                    "Only recruiters can update application status."
            );
        }

        // Recruiter's account must be ACTIVE.
        if (loggedInUser.getStatus() != UserStatus.ACTIVE) {
            throw new AccessDeniedException(
                    "Your account is not active."
            );
        }

        // ==========================================================
        // STEP 4 : OWNERSHIP VALIDATION
        // Ensure the logged-in recruiter owns the job
        // for which this application was submitted.
        // ==========================================================

        // Application
        //      ↓
        // Job
        //      ↓
        // Recruiter (Job Owner)
        User recruiter = application.getJob().getRecruiter();

        // Logged-in recruiter must be the owner
        // of the job to update this application.
        if (!recruiter.getId().equals(loggedInUser.getId())) {
            throw new AccessDeniedException(
                    "You are not allowed to update this application."
            );
        }

        // ==========================================================
        // STEP 5 : APPLICATION VALIDATION
        // Before updating the application's status,
        // verify that the application is still valid
        // and eligible for a status change.
        // ==========================================================

        // A deleted application should not be updated.
        //
        // Deleted Application
        //        ↓
        // No further business operations allowed.
        if (application.isDeleted()) {
            throw new JobApplicationException(
                    "This application has already been deleted."
            );
        }

        // Prevent updating to the same status.
        //
        // Current Status
        //        VS
        // Requested Status
        //
        // Example:
        //
        // APPLIED
        //    ↓
        // APPLIED   ❌
        //
        // Since there is no actual change,
        // reject the request instead of performing
        // an unnecessary database update.
        if (application.getStatus() == applicationStatus) {
            throw new JobApplicationException(
                    "Application is already in " + applicationStatus + " status."
            );
        }

        if (applicationStatus == null) {
            throw new JobApplicationException(
                    "Application status cannot be null."
            );
        }


        // ==========================================================
        // STEP 6 : STATUS TRANSITION VALIDATION
        // Ensure the requested status change follows
        // the allowed application workflow.
        // ==========================================================
        validateStatusTransition(
                application.getStatus(),
                applicationStatus
        );

        // ==========================================================
        // STEP 7 : Update the application's status.
        // ==========================================================
        application.setStatus(applicationStatus);

        // ==========================================================
        // STEP 8 : Save the updated application.
        // ==========================================================
        Application updatedApplication =
                applicationRepository.save(application);

        // ==========================================================
        // STEP 9 : Convert Entity → Response DTO and return it.
        // ==========================================================
        return ApplicationMapper
                .toApplicationResponseDto(updatedApplication);
    }


    /*
     * ============================================================
     * GET APPLICATIONS FOR LOGGED-IN RECRUITER
     * ============================================================
     *
     * This is different from getAllApplications().
     *
     * Applicant:
     *
     * Logged-in User
     *       ↓
     * Find applications submitted by this user.
     *
     *
     * Recruiter:
     *
     * Logged-in Recruiter
     *       ↓
     * Find Jobs owned by recruiter
     *       ↓
     * Find Applications for those jobs.
     */

    @Override
    public List<ApplicationResponseDto> getRecruiterApplications(
            String email) {


        // Find the currently logged-in recruiter.
        //
        // email
        //   ↓
        // Find User
        User recruiter =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Logged-in recruiter not found"
                                )
                        );


        // Find all applications where:
        //
        // application.job.recruiter == recruiter
        //
        // In other words:
        //
        // Recruiter's Jobs
        //       ↓
        // Applications received for those jobs.
        List<Application> applications =
                applicationRepository
                        .findAllByJobRecruiter(recruiter);


        System.out.println("Logged in recruiter id : " + recruiter.getId());
        System.out.println("Logged in recruiter email : " + recruiter.getEmail());

        // Convert entities into DTOs.
        return applications.stream()
                .map(ApplicationMapper::toApplicationResponseDto)
                .toList();
    }


    private void validateStatusTransition(
            ApplicationStatus currentStatus,
            ApplicationStatus newStatus
    ) {

        switch (currentStatus) {
            case APPLIED -> {
                if (newStatus != ApplicationStatus.SHORTLISTED &&
                        newStatus != ApplicationStatus.REJECTED &&
                        newStatus != ApplicationStatus.WITHDRAWN) {
                    throw new JobApplicationException(
                            "Application in APPLIED Status can only be updated to SHORTLISTED, REJECTED, OR WITHDRAWN."
                    );
                }
            }

            case SHORTLISTED -> {
                if (newStatus != ApplicationStatus.INTERVIEWED &&
                        newStatus != ApplicationStatus.REJECTED) {

                    throw new JobApplicationException(
                            "Application in SHORTLISTED Status can only be updated to INTERVIEWED or REJECTED. "
                    );
                }
            }

            case INTERVIEWED -> {
                if (newStatus != ApplicationStatus.ACCEPTED &&
                        newStatus != ApplicationStatus.REJECTED) {
                    throw new JobApplicationException(
                            "Application in INTERVIEWED status can only be updated to ACCEPTED or REJECTED."
                    );
                }
            }

            case ACCEPTED, REJECTED, WITHDRAWN -> {
                throw new JobApplicationException(
                        "Application in " + currentStatus +
                                " status cannot be updated further."
                );
            }
        }
    }

}

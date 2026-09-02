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
import com.jobportal.talenthub.service.EmailService;
import com.jobportal.talenthub.service.NotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    // Repository responsible for Application database operations.
    private final ApplicationRepository applicationRepository;

    // Repository used to find authenticated users.
    private final UserRepository userRepository;

    // Repository used to find jobs.
    private final JobRepository jobRepository;

    private final EmailService emailService;
    private final NotificationService notificationService;

    // Constructor Injection:
    // Spring injects the required dependencies.
    public ApplicationServiceImpl(
            ApplicationRepository applicationRepository,
            UserRepository userRepository,
            JobRepository jobRepository, EmailService emailService, NotificationService notificationService) {

        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }


    // =========================================================
    // APPLY FOR A JOB
    // =========================================================
    //
    // Flow:
    //
    // JWT
    //  ↓
    // Authenticated email
    //  ↓
    // Find User
    //  ↓
    // Validate User
    //  ↓
    // Find Job
    //  ↓
    // Validate Job
    //  ↓
    // Check duplicate application
    //  ↓
    // Create Application
    //  ↓
    // Save
    //  ↓
    // Return DTO

    @Override
    public ApplicationResponseDto applyJob(
            ApplicationRequestDto applicationRequestDto,
            String email) {

        // Identify the authenticated user from the email
        // obtained from the authentication/JWT flow.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Logged-in user not found"
                        )
                );


        // Only JOB_SEEKER users can apply for jobs.
        if (user.getRole() != Role.JOB_SEEKER) {

            throw new AccessDeniedException(
                    "Only Job Seekers can apply for jobs."
            );
        }


        // The user's account must be ACTIVE.
        if (user.getStatus() != UserStatus.ACTIVE) {

            throw new AccessDeniedException(
                    "Your account is not active."
            );
        }


        // Find the job selected by the applicant.
        Job job = jobRepository.findByIdAndDeletedFalse(
                        applicationRequestDto.jobId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found with id: "
                                        + applicationRequestDto.jobId()
                        )
                );


        // A recruiter cannot apply to their own job.
        if (job.getRecruiter().getId().equals(user.getId())) {

            throw new JobApplicationException(
                    "You cannot apply to your own job."
            );
        }


        // Soft-deleted jobs cannot receive applications.
        if (job.isDeleted()) {

            throw new JobApplicationException(
                    "This job has been removed and is no longer accepting applications."
            );
        }


        // Only ACTIVE jobs are available for applications.
        if (job.getStatus() != JobStatus.ACTIVE) {

            throw new JobApplicationException(
                    "You can only apply for an active job."
            );
        }


        // Current time is used to check the job's
        // application start/end window.
        LocalDateTime now = LocalDateTime.now();


        // Prevent applications before the application window opens.
        if (job.getApplicationStartTime() != null
                && now.isBefore(job.getApplicationStartTime())) {

            throw new JobApplicationException(
                    "Applications for this job have not started yet."
            );
        }


        // Prevent applications after the application window closes.
        if (job.getApplicationEndTime() != null
                && now.isAfter(job.getApplicationEndTime())) {

            throw new JobApplicationException(
                    "The application period for this job has already ended."
            );
        }


        // Prevent the same user from applying to the same job twice.
        //
        // The database also has a unique constraint on
        // (user_id, job_id), providing an additional safeguard.
        if (applicationRepository.existsByUserAndJob(user, job)) {

            throw new DuplicateApplicationException(
                    "You have already applied for this job."
            );
        }


        // Create a new Application entity.
        Application application = new Application();


        // Associate the authenticated applicant with the application.
        application.setUser(user);

        // Associate the selected job with the application.
        application.setJob(job);

        // Every new application starts with APPLIED status.
        application.setStatus(ApplicationStatus.APPLIED);


        // Save the application to the database.
        Application savedApplication =
                applicationRepository.save(application);


        // Convert the saved entity into a response DTO.
        return ApplicationMapper
                .toApplicationResponseDto(savedApplication);
    }


    // =========================================================
    // GET APPLICATION BY ID
    // =========================================================
    //
    // Security rule:
    //
    // Application
    //     ↓
    // Applicant/User
    //     ↓
    // Must match logged-in User

    @Override
    public ApplicationResponseDto getApplicationById(
            Long id,
            String email) {

        // Find the requested application.
        Application application =
                applicationRepository.findByIdAndDeletedFalse(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Application not found with id: "
                                                + id
                                )
                        );


        // Find the currently authenticated user.
        User loggedInUser =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Logged-in user not found"
                                )
                        );


        // Ownership check:
        // Users can view only their own applications.
        if (!application.getUser().getId()
                .equals(loggedInUser.getId())) {

            throw new AccessDeniedException(
                    "You are not allowed to view this application"
            );
        }


        // Ownership validation passed.
        return ApplicationMapper
                .toApplicationResponseDto(application);
    }


    // =========================================================
    // GET ALL APPLICATIONS OF LOGGED-IN USER
    // =========================================================

    @Override
    public List<ApplicationResponseDto> getAllApplications(
            String email) {

        // Identify the currently authenticated user.
        User loggedInUser =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Logged-in user not found"
                                )
                        );


        // Fetch only applications belonging to this user
        // and exclude soft-deleted applications.
        List<Application> applications =
                applicationRepository
                        .findAllByUserAndDeletedFalse(loggedInUser);


        // Convert entities into response DTOs.
        return applications.stream()
                .map(ApplicationMapper::toApplicationResponseDto)
                .toList();
    }


    // =========================================================
    // DELETE / WITHDRAW APPLICATION
    // =========================================================
    //
    // This is a soft delete.
    //
    // Application
    //     ↓
    // status = WITHDRAWN
    // deleted = true
    // deletedAt = current time

    @Override
    public void deleteApplication(
            Long id,
            String email) {

        // Find the application.
        Application application =
                applicationRepository.findByIdAndDeletedFalse(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Application not found with id: "
                                                + id
                                )
                        );


        // Find the currently authenticated user.
        User loggedInUser =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Logged-in user not found"
                                )
                        );


        // Only the applicant who created the application
        // can withdraw/delete it.
        if (!application.getUser().getId()
                .equals(loggedInUser.getId())) {

            throw new AccessDeniedException(
                    "You are not allowed to delete this application"
            );
        }


        // Prevent repeated deletion/withdrawal.
        if (application.isDeleted()) {

            throw new JobApplicationException(
                    "This application has been removed and is no longer accepting applications."
            );
        }


        // Mark the application as WITHDRAWN.
        application.setStatus(ApplicationStatus.WITHDRAWN);


        // Soft delete instead of physically removing the row.
        application.setDeleted(true);


        // Store when the withdrawal/deletion occurred.
        application.setDeletedAt(LocalDateTime.now());


        // Persist the changes.
        applicationRepository.save(application);
    }


    // =========================================================
    // UPDATE APPLICATION STATUS
    // =========================================================
    //
    // Security flow:
    //
    // JWT
    //  ↓
    // Logged-in Recruiter
    //  ↓
    // Find Application
    //  ↓
    // Find Application's Job
    //  ↓
    // Find Job's Recruiter
    //  ↓
    // Compare Recruiter IDs
    //  ↓
    // Validate Status Transition
    //  ↓
    // Update Status

    @Override
    public ApplicationResponseDto updateApplicationStatus(
            Long applicationId,
            ApplicationStatus applicationStatus,
            String email) {

        // Find the application whose status will be changed.
        Application application =
                applicationRepository.findByIdAndDeletedFalse(applicationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Application not found with id: "
                                                + applicationId
                                )
                        );


        // Identify the authenticated user.
        User loggedInUser =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Logged-in user not found"
                                )
                        );


        // Only recruiters can update application status.
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


        // Find the recruiter who owns the job
        // associated with this application.
        User recruiter =
                application.getJob().getRecruiter();


        // Ownership check:
        // Only the job owner can manage applications
        // received for that job.
        if (!recruiter.getId()
                .equals(loggedInUser.getId())) {

            throw new AccessDeniedException(
                    "You are not allowed to update this application."
            );
        }


        // Deleted applications cannot be updated.
        if (application.isDeleted()) {

            throw new JobApplicationException(
                    "This application has already been deleted."
            );
        }

        // Requested status must not be null.
        if (applicationStatus == null) {

            throw new JobApplicationException(
                    "Application status cannot be null."
            );
        }

        // A status update must actually change the status.
        if (application.getStatus() == applicationStatus) {

            throw new JobApplicationException(
                    "Application is already in "
                            + applicationStatus
                            + " status."
            );
        }


        // Validate that the requested transition
        // follows the application's business workflow.
        validateStatusTransition(
                application.getStatus(),
                applicationStatus
        );


        // Apply the new status.
        application.setStatus(applicationStatus);


        // Save the updated application.
        Application updatedApplication =
                applicationRepository.save(application);

//+

        notificationService.createNotification(
                application.getUser(),
                "Your application for "
                        + application.getJob().getTitle()
                        + " has been updated to "
                        + applicationStatus
        );

        // Return the updated application as a DTO.
        return ApplicationMapper
                .toApplicationResponseDto(updatedApplication);
    }


    // =========================================================
    // GET APPLICATIONS FOR LOGGED-IN RECRUITER
    // =========================================================
    //
    // Recruiter
    //    ↓
    // Recruiter's Jobs
    //    ↓
    // Applications received for those Jobs

    @Override
    public List<ApplicationResponseDto> getRecruiterApplications(
            String email) {

        // Find the currently authenticated recruiter.
        User recruiter =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Logged-in recruiter not found"
                                )
                        );


        // Fetch applications belonging to jobs
        // owned by this recruiter.
        List<Application> applications =
                applicationRepository
                        .findAllByJobRecruiter(recruiter);


        // Convert entities into response DTOs.
        return applications.stream()
                .map(ApplicationMapper::toApplicationResponseDto)
                .toList();
    }


    // =========================================================
    // APPLICATION STATUS TRANSITION RULES
    // =========================================================
    //
    // APPLIED
    //    ↓
    // SHORTLISTED / REJECTED / WITHDRAWN
    //
    // SHORTLISTED
    //    ↓
    // INTERVIEWED / REJECTED
    //
    // INTERVIEWED
    //    ↓
    // ACCEPTED / REJECTED
    //
    // ACCEPTED / REJECTED / WITHDRAWN
    //    ↓
    // FINAL STATE

    private void validateStatusTransition(
            ApplicationStatus currentStatus,
            ApplicationStatus newStatus) {

        switch (currentStatus) {

            case APPLIED -> {

                // APPLIED applications can move only to:
                // SHORTLISTED, REJECTED or WITHDRAWN.
                if (newStatus != ApplicationStatus.SHORTLISTED
                        && newStatus != ApplicationStatus.REJECTED
                        && newStatus != ApplicationStatus.WITHDRAWN) {

                    throw new JobApplicationException(
                            "Application in APPLIED Status can only be updated to SHORTLISTED, REJECTED, OR WITHDRAWN."
                    );
                }
            }


            case SHORTLISTED -> {

                // SHORTLISTED applications can move only to:
                // INTERVIEWED or REJECTED.
                if (newStatus != ApplicationStatus.INTERVIEWED
                        && newStatus != ApplicationStatus.REJECTED) {

                    throw new JobApplicationException(
                            "Application in SHORTLISTED Status can only be updated to INTERVIEWED or REJECTED."
                    );
                }
            }


            case INTERVIEWED -> {

                // INTERVIEWED applications can move only to:
                // ACCEPTED or REJECTED.
                if (newStatus != ApplicationStatus.ACCEPTED
                        && newStatus != ApplicationStatus.REJECTED) {

                    throw new JobApplicationException(
                            "Application in INTERVIEWED status can only be updated to ACCEPTED or REJECTED."
                    );
                }
            }


            case ACCEPTED, REJECTED, WITHDRAWN -> {

                // These are terminal states.
                // No further status transition is allowed.
                throw new JobApplicationException(
                        "Application in "
                                + currentStatus
                                + " status cannot be updated further."
                );
            }
        }
    }
}
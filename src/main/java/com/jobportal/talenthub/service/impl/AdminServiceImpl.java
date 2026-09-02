package com.jobportal.talenthub.service.impl;

import com.jobportal.talenthub.dto.ApplicationResponseDto;
import com.jobportal.talenthub.dto.JobResponseDto;
import com.jobportal.talenthub.dto.UserResponseDto;
import com.jobportal.talenthub.entity.*;
import com.jobportal.talenthub.exception.ResourceNotFoundException;
import com.jobportal.talenthub.mapper.ApplicationMapper;
import com.jobportal.talenthub.mapper.JobMapper;
import com.jobportal.talenthub.mapper.UserMapper;
import com.jobportal.talenthub.repository.ApplicationRepository;
import com.jobportal.talenthub.repository.JobRepository;
import com.jobportal.talenthub.repository.UserRepository;
import com.jobportal.talenthub.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;


    public AdminServiceImpl(
            UserRepository userRepository,
            JobRepository jobRepository,
            ApplicationRepository applicationRepository) {

        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }


    // =========================================================
    // GET ALL USERS
    // =========================================================

    // Admin can view all users that are not soft-deleted.

    @Override
    public List<UserResponseDto> getAllUsers() {

        // Fetch only users where deleted = false.
        List<User> users =
                userRepository.findAllByDeletedFalse();

        // Convert User entities into response DTOs.
        //
        // DTO prevents exposing sensitive entity information,
        // especially the user's password.
        return users
                .stream()
                .map(UserMapper::toResponseDto)
                .toList();
    }


    // =========================================================
    // GET USER BY ID
    // =========================================================

    @Override
    public UserResponseDto getUsersById(Long id) {

        // Find the requested user only if the user
        // has not been soft-deleted.
        User user =
                userRepository.findByIdAndDeletedFalse(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id " + id
                                )
                        );

        // Convert entity into response DTO.
        return UserMapper.toResponseDto(user);
    }


    // =========================================================
    // DELETE USER
    // =========================================================
    //
    // Admin performs a soft delete instead of permanently
    // removing the user from the database.

    @Override
    public void deleteUserById(Long id) {

        // Find only an active/non-deleted user.
        User user =
                userRepository.findByIdAndDeletedFalse(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id " + id
                                )
                        );


        // Mark the user as soft-deleted.
        user.setDeleted(true);

        // Store when the deletion occurred.
        user.setDeletedAt(LocalDateTime.now());

        // Change account status so the user
        // can no longer operate as an active account.
        user.setStatus(UserStatus.DEACTIVATED);

        // Save the soft-delete changes.
        userRepository.save(user);
    }

    // =========================================================
    // GET ALL JOBS
    // =========================================================

//    @Override
//    public List<JobResponseDto> getAllJobs() {
//
//        // Fetch only jobs that have not been soft-deleted.
//        List<Job> jobs =
//                jobRepository.findAllByDeletedFalse();
//
//        // Convert Job entities into response DTOs.
//        return jobs
//                .stream()
//                .map(JobMapper::toResponseDto)
//                .toList();
//    }

    @Override
    public Page<JobResponseDto> getAllJobs(Pageable pageable) {
        Page<Job> jobs = jobRepository.findAllByDeletedFalse(pageable);
        return jobs.map(JobMapper::toResponseDto);
    }

    // =========================================================
    // GET JOB BY ID
    // =========================================================

    @Override
    public JobResponseDto getJobById(Long id) {

        // Find the job only if it has not been soft-deleted.
        Job job =
                jobRepository.findByIdAndDeletedFalse(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Job not found with id " + id
                                )
                        );

        // Convert entity into response DTO.
        return JobMapper.toResponseDto(job);
    }


    // =========================================================
    // DELETE JOB
    // =========================================================

    // Admin removes the job logically using soft delete.

    @Override
    public void deleteJobById(Long id) {

        // Find the requested job if it is not already deleted.
        Job job =
                jobRepository.findByIdAndDeletedFalse(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Job not found with id " + id
                                )
                        );


        // Mark the job as deleted.
        job.setDeleted(true);

        // Record the deletion timestamp.
        job.setDeletedAt(LocalDateTime.now());

        // Update the job status to DELETED.
        job.setStatus(JobStatus.DELETED);

        // Persist the soft-delete changes.
        jobRepository.save(job);
    }


    // =========================================================
    // GET ALL APPLICATIONS
    // =========================================================

    // Admin can view applications across the entire system.

    @Override
    public List<ApplicationResponseDto> getAllApplications() {

        // Fetch only applications that have not been soft-deleted.
        List<Application> applications =
                applicationRepository.findAllByDeletedFalse();

        // Convert Application entities into response DTOs.
        return applications
                .stream()
                .map(ApplicationMapper::toApplicationResponseDto)
                .toList();
    }


    // =========================================================
    // GET APPLICATION BY ID
    // =========================================================

    @Override
    public ApplicationResponseDto getApplicationById(Long id) {

        // Find the application only if it has not been deleted.
        Application application =
                applicationRepository.findByIdAndDeletedFalse(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Application not found with id " + id
                                )
                        );

        // Convert entity into response DTO.
        return ApplicationMapper
                .toApplicationResponseDto(application);
    }


    // =========================================================
    // DELETE APPLICATION
    // =========================================================

    // Admin performs a soft delete.
    // The database record remains available for historical
    // or auditing purposes.

    @Override
    public void deleteApplicationById(Long id) {

        // Find the application only if it has not already
        // been soft-deleted.
        Application application =
                applicationRepository
                        .findByIdAndDeletedFalse(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Application not found with id "
                                                + id
                                )
                        );


        // Mark the application as soft-deleted.
        application.setDeleted(true);

        // Store the time when the application was deleted.
        application.setDeletedAt(LocalDateTime.now());

        // Save the soft-delete information.
        applicationRepository.save(application);

        // Physical deletion is intentionally not used.

        // applicationRepository.delete(application);
    }
}
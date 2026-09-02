package com.jobportal.talenthub.service;

import com.jobportal.talenthub.dto.ApplicationResponseDto;
import com.jobportal.talenthub.dto.JobResponseDto;
import com.jobportal.talenthub.dto.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {

    // =========================================================
    // USER MANAGEMENT
    // =========================================================

    // Get all users that are not soft-deleted.
    List<UserResponseDto> getAllUsers();

    // Get a specific user by ID.
    UserResponseDto getUsersById(Long id);

    // Soft-delete a user by ID.
    void deleteUserById(Long id);


    // =========================================================
    // JOB MANAGEMENT
    // =========================================================

    // Get all jobs that are not soft-deleted.
    Page<JobResponseDto> getAllJobs(Pageable pageable);

    // Get a specific job by ID.
    JobResponseDto getJobById(Long id);

    // Soft-delete a job by ID.
    void deleteJobById(Long id);


    // =========================================================
    // APPLICATION MANAGEMENT
    // =========================================================

    // Get all applications that are not soft-deleted.
    List<ApplicationResponseDto> getAllApplications();

    // Get a specific application by ID.
    ApplicationResponseDto getApplicationById(Long id);

    // Soft-delete an application by ID.
    void deleteApplicationById(Long id);
}
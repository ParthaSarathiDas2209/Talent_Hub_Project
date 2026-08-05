package com.jobportal.talenthub.service;

import com.jobportal.talenthub.dto.JobPatchDto;
import com.jobportal.talenthub.dto.JobRequestDto;
import com.jobportal.talenthub.dto.JobResponseDto;

import java.util.List;

public interface JobService {

    // =========================================================
    // JOB MANAGEMENT
    // =========================================================

    // Create a new job for the logged-in recruiter.
    JobResponseDto createJob(
            JobRequestDto jobRequestDto,
            String email
    );

    // Fully update an existing job owned by the logged-in recruiter.
    JobResponseDto updateJob(
            Long id,
            JobRequestDto jobRequestDto,
            String email
    );

    // Get all jobs that are not soft-deleted.
    List<JobResponseDto> getAllJobs();

    // Partially update selected fields of a job.
    JobResponseDto patchJob(
            Long id,
            JobPatchDto jobPatchDto,
            String email
    );

    // Get a specific job by ID.
    JobResponseDto getJobById(Long id);

    // Soft-delete a job owned by the logged-in recruiter.
    void deleteJob(
            Long id,
            String email
    );
}
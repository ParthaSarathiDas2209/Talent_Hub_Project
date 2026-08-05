package com.jobportal.talenthub.service.impl;

import com.jobportal.talenthub.dto.JobPatchDto;
import com.jobportal.talenthub.dto.JobRequestDto;
import com.jobportal.talenthub.dto.JobResponseDto;
import com.jobportal.talenthub.entity.*;
import com.jobportal.talenthub.exception.AccessDeniedException;
import com.jobportal.talenthub.exception.JobApplicationException;
import com.jobportal.talenthub.exception.ResourceNotFoundException;
import com.jobportal.talenthub.mapper.JobMapper;
import com.jobportal.talenthub.repository.JobRepository;
import com.jobportal.talenthub.repository.UserRepository;
import com.jobportal.talenthub.service.JobService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobServiceImpl implements JobService {

    // Repository used for Job database operations.
    private final JobRepository jobRepository;

    // Repository used to find the authenticated recruiter/user.
    private final UserRepository userRepository;


    // Constructor Injection:
    // Spring injects the required repositories automatically.
    public JobServiceImpl(
            JobRepository jobRepository,
            UserRepository userRepository) {

        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }


    // =========================================================
    // CREATE JOB
    // =========================================================

    @Override
    public JobResponseDto createJob(
            JobRequestDto jobRequestDto,
            String email) {

        // Find the currently authenticated recruiter.
        //
        // email comes from the authenticated user/JWT flow.
        User recruiter =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Logged-in recruiter not found with email: " + email
                                )
                        );


        // Only users with RECRUITER role can create jobs.
        if (recruiter.getRole() != Role.RECRUITER) {

            throw new AccessDeniedException(
                    "Only recruiters can create jobs."
            );
        }


        // Recruiter's account must be ACTIVE.
        if (recruiter.getStatus() != UserStatus.ACTIVE) {

            throw new AccessDeniedException(
                    "Your account is not active."
            );
        }


        // Convert validated request DTO into Job entity.
        Job job = JobMapper.toEntity(jobRequestDto);


        // Validate that the application start/end times
        // form a valid application window.
        validateApplicationWindow(job);


        // Associate the authenticated recruiter with the job.
        //
        // IMPORTANT:
        // Recruiter is taken from authentication,
        // NOT from the request body.
        job.setRecruiter(recruiter);


        // Newly created jobs are immediately ACTIVE
        // in the current TalentHub design.
        job.setStatus(JobStatus.ACTIVE);


        // Save the new Job entity.
        Job savedJob = jobRepository.save(job);


        // Convert saved entity into a safe response DTO.
        return JobMapper.toResponseDto(savedJob);
    }


    // =========================================================
    // FULL UPDATE / PUT
    // =========================================================

    @Override
    public JobResponseDto updateJob(
            Long id,
            JobRequestDto jobRequestDto,
            String email) {

        // Find the requested job.
        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found with id : " + id
                        )
                );


        // Find the currently authenticated user.
        User loggedInUser =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Logged-in recruiter not found with email: " + email
                                )
                        );


        // Only recruiters can update jobs.
        if (loggedInUser.getRole() != Role.RECRUITER) {

            throw new AccessDeniedException(
                    "Only Recruiters can update this job."
            );
        }


        // Recruiter's account must be ACTIVE.
        if (loggedInUser.getStatus() != UserStatus.ACTIVE) {

            throw new AccessDeniedException(
                    "Your account is not active."
            );
        }


        // Ownership check:
        // Only the recruiter who created the job
        // can update that job.
        if (!job.getRecruiter().getId()
                .equals(loggedInUser.getId())) {

            throw new AccessDeniedException(
                    "You are not allowed to update this Job"
            );
        }


        // A soft-deleted job cannot be modified.
        if (job.isDeleted()) {

            throw new JobApplicationException(
                    "This Job has already been deleted."
            );
        }


        // PUT/full update:
        // Replace the existing job fields with the
        // values supplied in the request.
        job.setTitle(jobRequestDto.title());
        job.setDescription(jobRequestDto.description());
        job.setCompanyName(jobRequestDto.companyName());
        job.setCompanyEmail(jobRequestDto.companyEmail());
        job.setCompanyPhone(jobRequestDto.companyPhone());
        job.setLocation(jobRequestDto.location());
        job.setSalary(jobRequestDto.salary());

        job.setApplicationStartTime(
                jobRequestDto.applicationStartTime()
        );

        job.setApplicationEndTime(
                jobRequestDto.applicationEndTime()
        );


        // Revalidate the application window after updating it.
        validateApplicationWindow(job);


        // Save the updated job.
        Job updatedJob = jobRepository.save(job);


        // Return updated job as a response DTO.
        return JobMapper.toResponseDto(updatedJob);
    }


    // =========================================================
    // GET ALL JOBS
    // =========================================================

    @Override
    public List<JobResponseDto> getAllJobs() {

        // Fetch only jobs that have NOT been soft-deleted.
        List<Job> jobs =
                jobRepository.findAllByDeletedFalse();


        // Convert each Job entity into JobResponseDto.
        return jobs.stream()
                .map(JobMapper::toResponseDto)
                .toList();
    }


    // =========================================================
    // PATCH / PARTIAL UPDATE
    // =========================================================

    @Override
    public JobResponseDto patchJob(
            Long id,
            JobPatchDto jobPatchDto,
            String email) {

        // Find the job that should be partially updated.
        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found with id : " + id
                        )
                );


        // Find the currently authenticated recruiter.
        User loggedInUser =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Logged-in recruiter not found with email: " + email
                                )
                        );


        // Only recruiters can modify jobs.
        if (loggedInUser.getRole() != Role.RECRUITER) {

            throw new AccessDeniedException(
                    "Only recruiters can update jobs."
            );
        }


        // Recruiter's account must be ACTIVE.
        if (loggedInUser.getStatus() != UserStatus.ACTIVE) {

            throw new AccessDeniedException(
                    "Your account is not active."
            );
        }


        // Ownership check:
        // Recruiter can modify only their own job.
        if (!job.getRecruiter().getId()
                .equals(loggedInUser.getId())) {

            throw new AccessDeniedException(
                    "You are not allowed to update this Job"
            );
        }


        // Soft-deleted jobs cannot be modified.
        if (job.isDeleted()) {

            throw new JobApplicationException(
                    "This Job has already been deleted."
            );
        }


        // PATCH:
        // Update only fields that were supplied.
        //
        // null means "keep the existing value".

        if (jobPatchDto.title() != null) {
            job.setTitle(jobPatchDto.title());
        }

        if (jobPatchDto.description() != null) {
            job.setDescription(jobPatchDto.description());
        }

        if (jobPatchDto.companyName() != null) {
            job.setCompanyName(jobPatchDto.companyName());
        }

        if (jobPatchDto.companyEmail() != null) {
            job.setCompanyEmail(jobPatchDto.companyEmail());
        }

        if (jobPatchDto.companyPhone() != null) {
            job.setCompanyPhone(jobPatchDto.companyPhone());
        }

        if (jobPatchDto.location() != null) {
            job.setLocation(jobPatchDto.location());
        }

        if (jobPatchDto.salary() != null) {
            job.setSalary(jobPatchDto.salary());
        }

        if (jobPatchDto.applicationStartTime() != null) {

            job.setApplicationStartTime(
                    jobPatchDto.applicationStartTime()
            );
        }

        if (jobPatchDto.applicationEndTime() != null) {

            job.setApplicationEndTime(
                    jobPatchDto.applicationEndTime()
            );
        }


        // Validate the complete application window
        // after applying the partial changes.
        validateApplicationWindow(job);


        // Save the partially updated job.
        Job savedJob = jobRepository.save(job);


        // Convert entity into response DTO.
        return JobMapper.toResponseDto(savedJob);
    }


    // =========================================================
    // GET JOB BY ID
    // =========================================================

    @Override
    public JobResponseDto getJobById(Long id) {

        // Find the requested job.
        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found with id : " + id
                        )
                );


        // Convert entity into response DTO.
        return JobMapper.toResponseDto(job);
    }


    // =========================================================
    // SOFT DELETE JOB
    // =========================================================

    @Override
    public void deleteJob(
            Long id,
            String email) {

        // Find the job that should be deleted.
        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found with id :" + id
                        )
                );


        // Find the currently authenticated recruiter.
        User loggedInUser =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Logged-in recruiter not found with email : " + email
                                )
                        );


        // Ownership check:
        // Only the recruiter who owns the job
        // can delete it.
        if (!job.getRecruiter().getId()
                .equals(loggedInUser.getId())) {

            throw new AccessDeniedException(
                    "You are not allowed to delete this Job"
            );
        }


        // Soft delete:
        // Keep the database record but mark it as deleted.
        job.setDeleted(true);


        // Store the exact date/time when the job was deleted.
        job.setDeletedAt(LocalDateTime.now());


        // Change the business status to DELETED.
        job.setStatus(JobStatus.DELETED);


        // Persist the soft-delete changes.
        jobRepository.save(job);
    }


    // =========================================================
    // APPLICATION WINDOW VALIDATION
    // =========================================================

    private void validateApplicationWindow(Job job) {

        // Application end time cannot be before
        // application start time.
        if (job.getApplicationEndTime()
                .isBefore(job.getApplicationStartTime())) {

            throw new JobApplicationException(
                    "Application end time must be after application start time."
            );
        }
    }
}
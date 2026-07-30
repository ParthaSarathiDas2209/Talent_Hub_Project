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

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobServiceImpl(JobRepository jobRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    @Override
    public JobResponseDto createJob(JobRequestDto jobRequestDto,
                                    String email) {

        // ==========================================================
        // STEP 1 : Find the currently logged-in recruiter.
        // The email comes from the authenticated JWT token.
        // ==========================================================
        User recruiter =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Logged-in recruiter not found with email: " + email
                                )
                        );

        // ==========================================================
        // STEP 2 : RECRUITER VALIDATION
        // Ensure only ACTIVE recruiters can create jobs.
        // ==========================================================

        // Only recruiters can create jobs.
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

        // ==========================================================
        // STEP 3 : Convert DTO → Entity.
        // ==========================================================
        Job job = JobMapper.toEntity(jobRequestDto);
        
        System.out.println("Start Time = " + jobRequestDto.applicationStartTime());
        System.out.println("EndTime = " + jobRequestDto.applicationEndTime());

        // ==========================================================
        // STEP 4 : Validate application window.
        // ==========================================================
        validateApplicationWindow(job);

        // ==========================================================
        // STEP 5 : Set recruiter and initial job status.
        // ==========================================================
        job.setRecruiter(recruiter);
        job.setStatus(JobStatus.ACTIVE);

        // ==========================================================
        // STEP 6 : Save the job.
        // ==========================================================
        Job savedJob = jobRepository.save(job);

        // ==========================================================
        // STEP 7 : Convert Entity → DTO.
        // ==========================================================
        return JobMapper.toResponseDto(savedJob);

    }

    // ==========================================================
    // STEP 1 : Find the job using the job ID.
    // This is the job that will be completely updated.
    // ==========================================================

    // ==========================================================
    // STEP 2 : Find the currently logged-in recruiter.
    // The email comes from the authenticated JWT token.
    // ==========================================================

    // ==========================================================
    // STEP 3 : RECRUITER VALIDATION
    // Ensure only ACTIVE recruiters can update jobs.
    // ==========================================================

    // Only recruiters can update jobs.

    // Recruiter's account must be ACTIVE.


    // ==========================================================
    // STEP 4 : OWNERSHIP VALIDATION
    // Ensure the logged-in recruiter owns this job.
    // ==========================================================

    // Job Recruiter
    //        VS
    // Logged-in Recruiter

    // ==========================================================
    // STEP 5 : JOB VALIDATION
    // Deleted jobs cannot be updated.
    // ==========================================================

    // Deleted Job
    //      ↓
    // No further business operations allowed.

    // ==========================================================
    // STEP 6 : Replace all job fields.

    // PUT replaces the entire resource with
    // the new values received from the client.
    // ==========================================================

    // ==========================================================
    // STEP 7 : Validate the updated application window.
    // ==========================================================

    // ==========================================================
    // STEP 8 : Save the updated job.
    // ==========================================================

    // ==========================================================
    // STEP 9 : Convert Entity → Response DTO and return it.
    // ==========================================================
    @Override
    public JobResponseDto updateJob(Long id, JobRequestDto jobRequestDto, String email) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found with id : " + id)
                );

        User loggedInUser = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Logged-in recruiter not found with email: " + email
                        )
                );

        if (loggedInUser.getRole() != Role.RECRUITER) {
            throw new AccessDeniedException(
                    "Only Recruiters can update this job."
            );
        }

        if (loggedInUser.getStatus() != UserStatus.ACTIVE) {
            throw new AccessDeniedException(
                    "Your account is not active."
            );
        }

        if (!job.getRecruiter().getId()
                .equals(loggedInUser.getId())) {
            throw new AccessDeniedException(
                    "You are not allowed to update this Job"
            );
        }

        if (job.isDeleted()) {
            throw new JobApplicationException(
                    "This Job has already been deleted."
            );
        }

        job.setTitle(jobRequestDto.title());
        job.setDescription(jobRequestDto.description());
        job.setCompanyName(jobRequestDto.companyName());
        job.setCompanyEmail(jobRequestDto.companyEmail());
        job.setCompanyPhone(jobRequestDto.companyPhone());
        job.setLocation(jobRequestDto.location());
        job.setSalary(jobRequestDto.salary());
        job.setApplicationStartTime(jobRequestDto.applicationStartTime());
        job.setApplicationEndTime(jobRequestDto.applicationEndTime());

        validateApplicationWindow(job);

        Job updatedJob = jobRepository.save(job);
        return JobMapper.toResponseDto(updatedJob);
    }


    // ==========================================================
    // STEP 1 : Fetch all jobs that are NOT deleted.
    //
    // Deleted jobs should never be shown
    // to end users.
    // ==========================================================

    // Convert every Job entity into JobResponseDto
    // before returning the response.
    @Override
    public List<JobResponseDto> getAllJobs() {
        return jobRepository.findAllByDeletedFalse()
                .stream()
                .map(JobMapper::toResponseDto)
                .toList();
    }

    // ==========================================================
    // STEP 1 : Find the job using the job ID.
    // This is the job that will be partially updated.
    // ==========================================================

    // ==========================================================
    // STEP 2 : Find the currently logged-in recruiter.
    // The email comes from the authenticated JWT token.
    // ==========================================================

    // ==========================================================
    // STEP 3 : RECRUITER VALIDATION
    // Ensure only ACTIVE recruiters can update jobs.
    // ==========================================================

    // Only recruiters can update jobs.

    // Recruiter's account must be ACTIVE.

    // ==========================================================
    // STEP 4 : OWNERSHIP VALIDATION
    // Ensure the logged-in recruiter owns this job.
    // ==========================================================

    // Job Recruiter
    //        VS
    // Logged-in Recruiter

    // ==========================================================
    // STEP 5 : JOB VALIDATION
    // Deleted jobs cannot be updated.
    // ==========================================================

    // Deleted Job
    //      ↓
    // No further business operations allowed.

    // ==========================================================
    // STEP 6 : UPDATE ONLY PROVIDED FIELDS.
    //
    // PATCH updates only the fields present
    // in the request body.
    //
    // Fields not provided remain unchanged.
    // ==========================================================

    // ==========================================================
    // STEP 7 : Validate the updated application window.
    // ==========================================================

    // ==========================================================
    // STEP 8 : Save the updated job.
    // ==========================================================


    // ==========================================================
    // STEP 9 : Convert Entity → Response DTO and return it.
    // ==========================================================
    @Override
    public JobResponseDto patchJob(Long id, JobPatchDto jobPatchDto,
                                   String email) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found with id : " + id)
                );

        User loggedInUser = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Logged-in recruiter not found with email: " + email
                        )
                );

        if (loggedInUser.getRole() != Role.RECRUITER) {
            throw new AccessDeniedException(
                    "Only recruiters can update jobs."
            );
        }

        if (loggedInUser.getStatus() != UserStatus.ACTIVE) {
            throw new AccessDeniedException(
                    "Your account is not active."
            );
        }

        if (!job.getRecruiter().getId()
                .equals(loggedInUser.getId())) {
            throw new AccessDeniedException(
                    "You are not allowed to update this Job"
            );
        }

        if (job.isDeleted()) {
            throw new JobApplicationException(
                    "This Job has already been deleted."
            );
        }

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

        validateApplicationWindow(job);

        Job savedJob = jobRepository.save(job);

        return JobMapper.toResponseDto(savedJob);
    }

    // ==========================================================
    // STEP 1 : Find the requested job.

    // Throw an exception if the job does not exist.
    // ==========================================================

    // ==========================================================
    // STEP 2 : Convert Entity → Response DTO.
    // ==========================================================

    @Override
    public JobResponseDto getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found with id : " + id
                        ));

        return JobMapper.toResponseDto(job);
    }

    // ==========================================================
    // STEP 1 : Find the job using the job ID.
    // This is the job that will be deleted.
    // ==========================================================

    // ==========================================================
    // STEP 2 : Find the currently logged-in recruiter.
    // The email comes from the authenticated JWT token.
    // ==========================================================

    // ==========================================================
    // STEP 3 : OWNERSHIP VALIDATION
    // Ensure the logged-in recruiter owns this job.
    // ==========================================================

    // Job Recruiter
    //        VS
    // Logged-in Recruiter

    // ==========================================================
    // STEP 4 : SOFT DELETE

    // Instead of removing the record permanently,
    // mark the job as deleted.

    // ACTIVE
    //     ↓
    // DELETED
    //
    // This preserves historical records.
    // ==========================================================

    // ==========================================================
    // STEP 5 : Save the updated job.
    // ==========================================================
    @Override
    public void deleteJob(Long id, String email) {
//        if (jobRepository.existsById(id)) {
//            jobRepository.deleteById(id);
//        } else {
//            throw new ResourceNotFoundException("Job not found with id : " + id);
//        }

        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found with id :" + id
                        )
                );

        User loggedInUser = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Logged-in recruiter not found with email : " + email
                        )
                );

        if (!job.getRecruiter().getId()
                .equals(loggedInUser.getId())) {
            throw new AccessDeniedException(
                    "You are not allowed to delete this Job"
            );
        }

//        jobRepository.delete(job);

        job.setDeleted(true);
        job.setDeletedAt(LocalDateTime.now());
        job.setStatus(JobStatus.DELETED);

        jobRepository.save(job);
    }

    // ==========================================================
    // APPLICATION WINDOW VALIDATION
    //
    // Ensure the application window is valid.
    //
    // Application Start Time
    //           ↓
    // Must be BEFORE
    //           ↓
    // Application End Time
    //
    // Otherwise,
    // reject the request.
    // ==========================================================
    private void validateApplicationWindow(Job job) {

        if (job.getApplicationEndTime()
                .isBefore(job.getApplicationStartTime())) {
            throw new JobApplicationException(
                    "Application end time must be after application start time."
            );
        }
    }
}
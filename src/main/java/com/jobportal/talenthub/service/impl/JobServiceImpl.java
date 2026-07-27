package com.jobportal.talenthub.service.impl;

import com.jobportal.talenthub.dto.JobPatchDto;
import com.jobportal.talenthub.dto.JobRequestDto;
import com.jobportal.talenthub.dto.JobResponseDto;
import com.jobportal.talenthub.entity.Job;
import com.jobportal.talenthub.entity.JobStatus;
import com.jobportal.talenthub.entity.User;
import com.jobportal.talenthub.exception.AccessDeniedException;
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

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Logged-in recruiter not found with email: " + email
                        )
                );

        Job job = JobMapper.toEntity(jobRequestDto);

        job.setRecruiter(recruiter);
        job.setStatus(JobStatus.ACTIVE);

        Job savedJob = jobRepository.save(job);
        
        return JobMapper.toResponseDto(savedJob);
    }

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

        if (!job.getRecruiter().getId()
                .equals(loggedInUser.getId())) {
            throw new AccessDeniedException(
                    "You are not allowed to update this Job"
            );
        }

        job.setTitle(jobRequestDto.title());
        job.setDescription(jobRequestDto.description());
        job.setCompanyName(jobRequestDto.companyName());
        job.setCompanyEmail(jobRequestDto.companyEmail());
        job.setCompanyPhone(jobRequestDto.companyPhone());
        job.setLocation(jobRequestDto.location());
        job.setSalary(jobRequestDto.salary());

        Job updatedJob = jobRepository.save(job);
        return JobMapper.toResponseDto(updatedJob);
    }

    @Override
    public List<JobResponseDto> getAllJobs() {
        return jobRepository.findAllByDeletedFalse()
                .stream()
                .map(JobMapper::toResponseDto)
                .toList();
    }

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

        if (!job.getRecruiter().getId()
                .equals(loggedInUser.getId())) {
            throw new AccessDeniedException(
                    "You are not allowed to update this Job"
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

        Job savedJob = jobRepository.save(job);
        return JobMapper.toResponseDto(savedJob);
    }

    @Override
    public JobResponseDto getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found with id : " + id
                        ));

        return JobMapper.toResponseDto(job);
    }

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
}
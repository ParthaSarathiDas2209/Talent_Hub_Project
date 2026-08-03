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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    public AdminServiceImpl(UserRepository userRepository, JobRepository jobRepository, ApplicationRepository applicationRepository) {
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }


    @Override
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAllByDeletedFalse();

        return users
                .stream()
                .map(UserMapper::toResponseDto)
                .toList();
    }

    @Override
    public UserResponseDto getUsersById(Long id) {

        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id " + id
                        )
                );

        return UserMapper.toResponseDto(user);
    }

    @Override
    public void deleteUserById(Long id) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id " + id
                        )
                );
        user.setDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        user.setStatus(UserStatus.DEACTIVATED);

        userRepository.save(user);
    }

    @Override
    public List<JobResponseDto> getAllJobs() {
        List<Job> jobs = jobRepository.findAllByDeletedFalse();
        return jobs
                .stream()
                .map(JobMapper::toResponseDto)
                .toList();
    }

    @Override
    public JobResponseDto getJobById(Long id) {

        Job job = jobRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found with id " + id
                        )
                );

        return JobMapper.toResponseDto(job);
    }

    @Override
    public void deleteJobById(Long id) {
        Job job = jobRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found with id " + id
                        )
                );

        job.setDeleted(true);
        job.setDeletedAt(LocalDateTime.now());
        job.setStatus(JobStatus.DELETED);

        jobRepository.save(job);
    }

    @Override
    public List<ApplicationResponseDto> getAllApplications() {
        List<Application> applications =
                applicationRepository.findAllByDeletedFalse();
        return applications
                .stream()
                .map(ApplicationMapper::toApplicationResponseDto)
                .toList();
    }

    @Override
    public ApplicationResponseDto getApplicationById(Long id) {

        Application application =
                applicationRepository.findByIdAndDeletedFalse(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Application not found with id " + id
                                )
                        );

        return ApplicationMapper.toApplicationResponseDto(application);
    }

    @Override
    public void deleteApplicationById(Long id) {
        Application application = applicationRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Application deleted successfully with Id : " + id
                        )
                );

        application.setDeleted(true);
        application.setDeletedAt(LocalDateTime.now());
        applicationRepository.save(application);

//        applicationRepository.delete(application);
        
    }
}
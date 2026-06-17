package com.jobportal.talenthub.service.impl;

import com.jobportal.talenthub.dto.ApplicationRequestDto;
import com.jobportal.talenthub.dto.ApplicationResponseDto;
import com.jobportal.talenthub.entity.Application;
import com.jobportal.talenthub.entity.ApplicationStatus;
import com.jobportal.talenthub.entity.Job;
import com.jobportal.talenthub.entity.User;
import com.jobportal.talenthub.exception.DuplicateApplicationException;
import com.jobportal.talenthub.exception.ResourceNotFoundException;
import com.jobportal.talenthub.mapper.ApplicationMapper;
import com.jobportal.talenthub.repository.ApplicationRepository;
import com.jobportal.talenthub.repository.JobRepository;
import com.jobportal.talenthub.repository.UserRepository;
import com.jobportal.talenthub.service.ApplicationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;


    public ApplicationServiceImpl(ApplicationRepository applicationRepository, UserRepository userRepository, JobRepository jobRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    @Override
    public ApplicationResponseDto applyJob(ApplicationRequestDto applicationRequestDto) {
        User user = userRepository.findById(applicationRequestDto.userId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User Id not found! "
                                + applicationRequestDto.userId())
                );

        Job job = jobRepository.findById(applicationRequestDto.jobId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Job Id not found! "
                                + applicationRequestDto.jobId())
                );

        if (applicationRepository.existsByUserAndJob(user, job)) {
            throw new DuplicateApplicationException(
                    "You Have already applied for this job. "
                            + applicationRequestDto.jobId()
            );
        }

        Application application = new Application();
        application.setUser(user);
        application.setJob(job);
        application.setStatus(ApplicationStatus.APPLIED);

        Application savedApplication = applicationRepository.save(application);

        return ApplicationMapper.toApplicationResponseDto(savedApplication);

    }

    @Override
    public ApplicationResponseDto getApplicationById(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Application not found with id : " + id)
                );

        return ApplicationMapper.toApplicationResponseDto(application);
    }

    @Override
    public List<ApplicationResponseDto> getAllApplications() {
        List<Application> applications = applicationRepository.findAll();

        return applications.stream()
                .map(ApplicationMapper::toApplicationResponseDto)
                .toList();
    }

    @Override
    public void deleteApplication(Long id) {
//        if (applicationRepository.existsById(id)) {
//            applicationRepository.deleteById(id);
//        } else {
//            throw new ResourceNotFoundException("Application not found with id : " + id);
//        }

        Application application = applicationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Application not found with id : " + id)
                );

        applicationRepository.delete(application);
    }
}
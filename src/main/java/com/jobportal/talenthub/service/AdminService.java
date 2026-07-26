package com.jobportal.talenthub.service;

import com.jobportal.talenthub.dto.ApplicationResponseDto;
import com.jobportal.talenthub.dto.JobResponseDto;
import com.jobportal.talenthub.dto.UserResponseDto;

import java.util.List;

public interface AdminService {

    //  Users
    List<UserResponseDto> getAllUsers();

    UserResponseDto getUsersById(Long id);

    void deleteUserById(Long id);

    //  Job
    List<JobResponseDto> getAllJobs();

    JobResponseDto getJobById(Long id);

    void deleteJobById(Long id);

//  Applications

    List<ApplicationResponseDto> getAllApplications();

    ApplicationResponseDto getApplicationById(Long id);

    void deleteApplicationById(Long id);
}

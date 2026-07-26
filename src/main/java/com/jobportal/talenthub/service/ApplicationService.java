package com.jobportal.talenthub.service;

import com.jobportal.talenthub.dto.ApplicationRequestDto;
import com.jobportal.talenthub.dto.ApplicationResponseDto;
import com.jobportal.talenthub.entity.ApplicationStatus;

import java.util.List;

public interface ApplicationService {

    ApplicationResponseDto applyJob(ApplicationRequestDto applicationRequestDto, String email);

    ApplicationResponseDto getApplicationById(Long id, String email);

    List<ApplicationResponseDto> getAllApplications(String email);

    void deleteApplication(Long id, String email);

    ApplicationResponseDto updateApplicationStatus(Long applicationId, ApplicationStatus applicationStatus, String email);

    List<ApplicationResponseDto> getRecruiterApplications(String email);

}

package com.jobportal.talenthub.service;

import com.jobportal.talenthub.dto.ApplicationRequestDto;
import com.jobportal.talenthub.dto.ApplicationResponseDto;

import java.util.List;

public interface ApplicationService {

    ApplicationResponseDto applyJob(ApplicationRequestDto applicationRequestDto);

    ApplicationResponseDto getApplicationById(Long id);

    List<ApplicationResponseDto> getAllApplications();

    void deleteApplication(Long id);

}

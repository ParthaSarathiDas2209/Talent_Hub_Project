package com.jobportal.talenthub.service;

import com.jobportal.talenthub.dto.JobRequestDto;
import com.jobportal.talenthub.dto.JobResponseDto;

import java.util.List;

public interface JobService {

    JobResponseDto createJob(JobRequestDto jobRequestDto);

    JobResponseDto updateJob(Long id, JobRequestDto jobRequestDto);

    List<JobResponseDto> getAllJobs();

//    JobResponseDto patchJob(Long id, JobPatchDto jobPatchDto);
    
    JobResponseDto getJobById(Long id);

    void deleteJob(Long id);

}

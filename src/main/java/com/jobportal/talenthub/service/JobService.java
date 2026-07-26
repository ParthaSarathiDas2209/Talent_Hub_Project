package com.jobportal.talenthub.service;

import com.jobportal.talenthub.dto.JobPatchDto;
import com.jobportal.talenthub.dto.JobRequestDto;
import com.jobportal.talenthub.dto.JobResponseDto;

import java.util.List;

public interface JobService {

    JobResponseDto createJob(JobRequestDto jobRequestDto, String email);

    JobResponseDto updateJob(Long id, JobRequestDto jobRequestDto, String email);

    List<JobResponseDto> getAllJobs();

    JobResponseDto patchJob(Long id, JobPatchDto jobPatchDto, String email);

    JobResponseDto getJobById(Long id);

    void deleteJob(Long id, String email);

}

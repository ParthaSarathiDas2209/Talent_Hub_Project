package com.jobportal.talenthub.service;

import com.jobportal.talenthub.dto.JobPatchDto;
import com.jobportal.talenthub.dto.JobRequestDto;
import com.jobportal.talenthub.dto.JobResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JobService {

    // =========================================================
    // JOB MANAGEMENT
    // =========================================================

    JobResponseDto createJob(
            JobRequestDto jobRequestDto,
            String email
    );

    JobResponseDto updateJob(
            Long id,
            JobRequestDto jobRequestDto,
            String email
    );


//    List<JobResponseDto> getAllJobs();

    JobResponseDto patchJob(
            Long id,
            JobPatchDto jobPatchDto,
            String email
    );

    JobResponseDto getJobById(Long id);

    void deleteJob(
            Long id,
            String email
    );

    List<JobResponseDto> searchJobs(String keyword);

    Page<JobResponseDto> getAllJobs(Pageable pageable);

    Page<JobResponseDto> filterByLocation(String location, Pageable pageable);

    Page<JobResponseDto> filterBySalary(Long minSalary, Long maxSalary, Pageable pageable);

    Page<JobResponseDto> filterByCompanyAndTitle(String companyName, String title, Pageable pageable);
    
}
package com.jobportal.talenthub.mapper;

import com.jobportal.talenthub.dto.JobRequestDto;
import com.jobportal.talenthub.dto.JobResponseDto;
import com.jobportal.talenthub.entity.Job;

public class JobMapper {

    public static Job toEntity(JobRequestDto jobRequestDto) {

        Job job = new Job();

        job.setTitle(jobRequestDto.title());
        job.setDescription(jobRequestDto.description());
        job.setCompanyName(jobRequestDto.companyName());
        job.setCompanyEmail(jobRequestDto.companyEmail());
        job.setCompanyPhone(jobRequestDto.companyPhone());
        job.setLocation(jobRequestDto.location());
        job.setSalary(jobRequestDto.salary());

        return job;
    }

    public static JobResponseDto toResponseDto(Job job) {
        return new JobResponseDto(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.getCompanyName(),
                job.getCompanyEmail(),
                job.getCompanyPhone(),
                job.getLocation(),
                job.getSalary(),
                job.getRecruiter().getId()
        );
    }
}

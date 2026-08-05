package com.jobportal.talenthub.mapper;

import com.jobportal.talenthub.dto.JobRequestDto;
import com.jobportal.talenthub.dto.JobResponseDto;
import com.jobportal.talenthub.entity.Job;

// Utility class responsible for converting between
// Job entities and Job DTOs.
//
// Keeps DTO-to-entity and entity-to-DTO conversion
// separate from the service layer.
public class JobMapper {

    // Converts JobRequestDto into a Job entity.
    // Used when creating a new job.
    public static Job toEntity(JobRequestDto jobRequestDto) {

        // Create a new Job entity.
        Job job = new Job();

        // Copy validated job details from the DTO into the entity.
        job.setTitle(jobRequestDto.title());
        job.setDescription(jobRequestDto.description());
        job.setCompanyName(jobRequestDto.companyName());
        job.setCompanyEmail(jobRequestDto.companyEmail());
        job.setCompanyPhone(jobRequestDto.companyPhone());
        job.setLocation(jobRequestDto.location());
        job.setSalary(jobRequestDto.salary());

        // Set the application window for the job.
        job.setApplicationStartTime(
                jobRequestDto.applicationStartTime()
        );

        job.setApplicationEndTime(
                jobRequestDto.applicationEndTime()
        );

        // IMPORTANT:
        // recruiter is NOT set from JobRequestDto.
        //
        // The authenticated recruiter should be obtained
        // from the SecurityContext/JWT in the service layer.
        //
        // Example:
        // job.setRecruiter(authenticatedRecruiter);

        return job;
    }


    // Converts a Job entity into JobResponseDto.
    // Used when returning job information through the API.
    public static JobResponseDto toResponseDto(Job job) {

        return new JobResponseDto(

                // Job ID.
                job.getId(),

                // Job details.
                job.getTitle(),
                job.getDescription(),
                job.getCompanyName(),
                job.getCompanyEmail(),
                job.getCompanyPhone(),
                job.getLocation(),
                job.getSalary(),

                // Return only the recruiter's ID instead of
                // exposing the complete User entity.
                job.getRecruiter().getId(),

                // Application window.
                job.getApplicationStartTime(),
                job.getApplicationEndTime()
        );
    }


    // =========================================================
    // FUTURE IMPROVEMENTS
    // =========================================================

    // 1. If JobResponseDto later includes createdAt,
    //    updatedAt, or status, map those fields here.
    //
    // 2. Do not map deleted/deletedAt into the normal
    //    JobResponseDto unless an admin-specific response
    //    actually requires those fields.
    //
    // 3. If a future API needs recruiter details,
    //    prefer a dedicated RecruiterResponseDto instead
    //    of exposing the complete User entity.
    //
    // 4. JobPatchDto currently needs separate PATCH mapping
    //    logic because PATCH should update only supplied fields.
}
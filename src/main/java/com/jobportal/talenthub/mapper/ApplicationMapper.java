package com.jobportal.talenthub.mapper;

import com.jobportal.talenthub.dto.ApplicationResponseDto;
import com.jobportal.talenthub.entity.Application;

// Utility class responsible for converting Application
// entities into ApplicationResponseDto objects.
//
// There is currently no toEntity() method because
// application creation requires business/security logic
// handled by ApplicationService.
public class ApplicationMapper {

    // Converts an Application entity into the response DTO.
    // Used when returning application information through the API.
    public static ApplicationResponseDto toApplicationResponseDto(
            Application application) {

        return new ApplicationResponseDto(

                // Application ID.
                application.getId(),

                // ID of the user who submitted the application.
                // Only the ID is exposed instead of the complete User entity.
                application.getUser().getId(),

                // ID of the job being applied for.
                // Only the ID is exposed instead of the complete Job entity.
                application.getJob().getId(),

                // Current application status.
                application.getStatus(),

                // Date and time when the application was submitted.
                application.getAppliedAt()
        );
    }


    // =========================================================
    // FUTURE IMPROVEMENTS
    // =========================================================

    // 1. If ApplicationResponseDto later includes updatedAt,
    //    map application.getUpdatedAt() here.
    //
    // 2. If dashboards later need candidate/job information,
    //    additional DTO fields such as userName, userEmail,
    //    or jobTitle can be mapped here.
    //
    // 3. Avoid returning complete User or Job entities
    //    directly from the mapper.
    //
    // 4. ApplicationPatchDto is NOT required currently because
    //    application status has its own dedicated update operation.
}
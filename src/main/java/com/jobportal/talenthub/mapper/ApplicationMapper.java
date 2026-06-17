package com.jobportal.talenthub.mapper;

import com.jobportal.talenthub.dto.ApplicationResponseDto;
import com.jobportal.talenthub.entity.Application;

public class ApplicationMapper {

    public static ApplicationResponseDto toApplicationResponseDto(Application application) {
        return new ApplicationResponseDto(
                application.getId(),
                application.getUser().getId(),
                application.getJob().getId(),
                application.getStatus(),
                application.getAppliedAt()
        );
    }
}

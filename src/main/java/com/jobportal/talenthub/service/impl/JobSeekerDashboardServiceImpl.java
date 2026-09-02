package com.jobportal.talenthub.service.impl;

import com.jobportal.talenthub.dto.JobSeekerDashboardDto;
import com.jobportal.talenthub.entity.ApplicationStatus;
import com.jobportal.talenthub.entity.User;
import com.jobportal.talenthub.exception.ResourceNotFoundException;
import com.jobportal.talenthub.repository.ApplicationRepository;
import com.jobportal.talenthub.repository.UserRepository;
import com.jobportal.talenthub.service.JobSeekerDashboardService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class JobSeekerDashboardServiceImpl implements JobSeekerDashboardService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public JobSeekerDashboardServiceImpl(ApplicationRepository applicationRepository, UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public JobSeekerDashboardDto getDashboard() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        Long userId = user.getId();

        long total = applicationRepository.countByUserId(user.getId());

        long applied = applicationRepository.countByUserIdAndStatus(
                userId, ApplicationStatus.APPLIED
        );

        long shortlisted = applicationRepository.countByUserIdAndStatus(
                userId, ApplicationStatus.SHORTLISTED
        );

        long interviewed = applicationRepository.countByUserIdAndStatus(
                userId, ApplicationStatus.INTERVIEWED
        );

        long accepted = applicationRepository.countByUserIdAndStatus(
                userId, ApplicationStatus.ACCEPTED
        );

        long rejected = applicationRepository.countByUserIdAndStatus(
                userId, ApplicationStatus.REJECTED
        );

        long withdrawn = applicationRepository.countByUserIdAndStatus(
                userId, ApplicationStatus.WITHDRAWN
        );

        return new JobSeekerDashboardDto(
                total,
                applied,
                shortlisted,
                interviewed,
                accepted,
                rejected,
                withdrawn
        );
    }
}

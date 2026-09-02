package com.jobportal.talenthub.repository;

import com.jobportal.talenthub.entity.Application;
import com.jobportal.talenthub.entity.ApplicationStatus;
import com.jobportal.talenthub.entity.Job;
import com.jobportal.talenthub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findAllByDeletedFalse();

    boolean existsByUserAndJob(User user, Job job);

    List<Application> findAllByJobRecruiter(User recruiter);

    //    List<Application> findAllByJobRecruiterId(Long recruiterId);

    List<Application> findAllByUserAndDeletedFalse(User user);

    Optional<Application> findByIdAndDeletedFalse(Long id);

    Long countByUserId(Long userId);

    Long countByUserIdAndStatus(Long userId, ApplicationStatus applicationStatus);

    Long countByJobRecruiterId(Long recruiterId);

    Long countByJobRecruiterIdAndStatus(
            Long recruiterId,
            ApplicationStatus applicationStatus
    );

    Long countByStatus(ApplicationStatus applicationStatus);
}
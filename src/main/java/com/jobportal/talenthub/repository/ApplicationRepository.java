package com.jobportal.talenthub.repository;

import com.jobportal.talenthub.entity.Application;
import com.jobportal.talenthub.entity.Job;
import com.jobportal.talenthub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByUserAndJob(User user, Job job); // Has this user already applied to this job?
    
    List<Application> findAllByJobRecruiter(User recruiter); // Who has applied to this recruiter's jobs?

    List<Application> findAllByUserAndDeletedFalse(User user);
}
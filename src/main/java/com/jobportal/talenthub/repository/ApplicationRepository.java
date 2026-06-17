package com.jobportal.talenthub.repository;

import com.jobportal.talenthub.entity.Application;
import com.jobportal.talenthub.entity.Job;
import com.jobportal.talenthub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByUserAndJob(User user, Job job);
}
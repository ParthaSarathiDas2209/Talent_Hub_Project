package com.jobportal.talenthub.repository;

import com.jobportal.talenthub.entity.Application;
import com.jobportal.talenthub.entity.Job;
import com.jobportal.talenthub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByUserAndJob(User user, Job job);

    List<Application> findAllByUser(User user);

}
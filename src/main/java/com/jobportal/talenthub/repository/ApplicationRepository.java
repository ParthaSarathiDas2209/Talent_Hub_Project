package com.jobportal.talenthub.repository;

import com.jobportal.talenthub.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
}
package com.jobportal.talenthub.repository;

import com.jobportal.talenthub.entity.Job;
import com.jobportal.talenthub.entity.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

//    List<Job> findAllByDeletedFalse();

    Page<Job> findAllByDeletedFalse(Pageable pageable);

    Optional<Job> findByIdAndDeletedFalse(Long id);

    @Query("""
            SELECT j
            FROM Job j
            WHERE j.deleted = false
            AND(
                    LOWER(j.title) LIKE CONCAT ('%' , LOWER(:keyword), '%')
                                OR LOWER(j.description) LIKE CONCAT('%', LOWER(:keyword), '%')
                                OR LOWER(j.companyName) LIKE CONCAT('%', LOWER(:keyword), '%')
                                OR LOWER(j.location) LIKE CONCAT('%', LOWER(:keyword), '%')
            )
            """)
    List<Job> searchJobs(@Param("keyword") String keyword);

    Page<Job> findByLocationContainingIgnoreCaseAndDeletedFalse(
            String location, Pageable pageable
    );

    Page<Job> findByCompanyNameContainingIgnoreCaseAndTitleContainingIgnoreCaseAndDeletedFalse(
            String companyName, String title, Pageable pageable);

    Page<Job> findBySalaryGreaterThanEqualAndSalaryLessThanEqualAndDeletedFalse(
            Long minSalary, Long maxSalary, Pageable pageable);


    Long countByDeletedTrue();
    
    Long countByRecruiterIdAndDeletedFalse(Long recruiterId);

    Long countByRecruiterIdAndStatusAndDeletedFalse(
            Long recruiterId, JobStatus status
    );

    Long countByDeletedFalse();

    Long countByStatusAndDeletedFalse(JobStatus status);
}
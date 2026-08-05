package com.jobportal.talenthub.repository;
// Repository layer for Application database operations.

import com.jobportal.talenthub.entity.Application;
// Application entity managed by this repository.

import com.jobportal.talenthub.entity.Job;
// Job entity used for application-related queries.

import com.jobportal.talenthub.entity.User;
// User entity used for application-related queries.

import org.springframework.data.jpa.repository.JpaRepository;
// Provides built-in CRUD operations such as:
// save(), findById(), findAll(), delete(), existsById(), etc.

import org.springframework.stereotype.Repository;
// Marks this interface as a repository component.

import java.util.List;
// Used for methods that return multiple applications.

import java.util.Optional;
// Used when an application may or may not exist.


@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
// ApplicationRepository manages Application entities.
// Long is the type of the Application primary key.
//
// JpaRepository already provides the basic database operations.
// We define only the custom queries required by TalentHub.


    List<Application> findAllByDeletedFalse();
    // Returns all applications that have NOT been soft-deleted.
    //
    // Conceptually:
    // SELECT * FROM applications
    // WHERE deleted = false
    //
    // Used mainly by AdminService.


    boolean existsByUserAndJob(User user, Job job);
    // Checks whether this user has already applied to this job.
    //
    // Conceptually:
    // WHERE user_id = ? AND job_id = ?
    //
    // true  -> user already applied
    // false -> user has not applied
    //
    // Important business rule:
    // One user should not apply to the same job twice.


    List<Application> findAllByJobRecruiter(User recruiter);
    // Finds applications for jobs posted by a particular recruiter.
    //
    // Spring Data follows the entity relationships:
    //
    // Application
    //      ↓
    //     Job
    //      ↓
    //   recruiter
    //      ↓
    //     User
    //
    // Meaning:
    // "Give me applications where the application's job
    //  belongs to this recruiter."
    //
    // Used for recruiter application management.


    List<Application> findAllByUserAndDeletedFalse(User user);
    // Returns all non-deleted applications submitted by a particular user.
    //
    // Conceptually:
    // WHERE user_id = ?
    // AND deleted = false
    //
    // Used when a job seeker wants to see their applications.


    Optional<Application> findByIdAndDeletedFalse(Long id);
    // Finds an application by ID only when it is not soft-deleted.
    //
    // Conceptually:
    // WHERE id = ?
    // AND deleted = false
    //
    // Prevents normal operations from accessing an already
    // soft-deleted application.

}
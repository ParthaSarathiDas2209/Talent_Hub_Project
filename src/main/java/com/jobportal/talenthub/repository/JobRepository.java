package com.jobportal.talenthub.repository;
// Repository layer for Job database operations.

import com.jobportal.talenthub.entity.Job;
// Job entity managed by this repository.

import org.springframework.data.jpa.repository.JpaRepository;
// Provides built-in CRUD operations such as:
// save(), findById(), findAll(), delete(), existsById(), etc.

import org.springframework.stereotype.Repository;
// Marks this interface as a repository component.

import java.util.List;
// Used when returning multiple jobs.

import java.util.Optional;
// Used when a job may or may not be found.

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
// JobRepository manages:
// Job -> entity
// Long -> Job primary-key type.
//
// JpaRepository automatically provides the basic database operations.
// We only define custom queries needed by TalentHub.


    List<Job> findAllByDeletedFalse();
    // Returns only jobs that have NOT been soft-deleted.
    //
    // Conceptually:
    // SELECT * FROM jobs WHERE deleted = false
    //
    // Used when displaying the normal list of available jobs.
    //
    // Important because TalentHub uses SOFT DELETE.


    Optional<Job> findByIdAndDeletedFalse(Long id);
    // Finds a job by ID only if it has not been soft-deleted.
    //
    // Conceptually:
    // SELECT * FROM jobs
    // WHERE id = ? AND deleted = false
    //
    // Useful when getting/updating/deleting a job that should
    // still be considered active.

}
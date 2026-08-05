package com.jobportal.talenthub.entity;
// Entity layer.
// Represents a job posting in the TalentHub database.

import jakarta.persistence.*;
// JPA annotations used to map this class to the database.

import lombok.AllArgsConstructor;
// Generates a constructor with all fields.

import lombok.Getter;
// Generates getter methods.

import lombok.NoArgsConstructor;
// Generates a no-argument constructor required by JPA.

import lombok.Setter;
// Generates setter methods.

import org.hibernate.annotations.CreationTimestamp;
// Automatically records when the job is created.

import org.hibernate.annotations.UpdateTimestamp;
// Automatically records when the job is updated.

import java.time.LocalDateTime;
// Used for timestamps and application window.

import java.util.List;
// Used for the Job → Applications relationship.


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
// Marks Job as a JPA entity.

@Table(name = "jobs")
// Maps this entity to the "jobs" database table.

public class Job {


    @Id
    // Primary key of the job.

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Database automatically generates the job ID.

    private Long id;


    @Column(nullable = false)
    // Job title is mandatory.

    private String title;


    @Column(nullable = false, columnDefinition = "TEXT")
    // Job description is mandatory.
    // TEXT allows longer descriptions than a normal VARCHAR column.

    private String description;


    @Column(nullable = false)
    // Company name is required.

    private String companyName;


    @Column(nullable = false)
    // Company email is required.

    private String companyEmail;


    @Column(nullable = false)
    // Company phone is required.

    private String companyPhone;


    @Column(nullable = false)
    // Job location is required.

    private String location;


    @Column(nullable = false)
    // Salary is required.

    private Long salary;


    @ManyToOne(fetch = FetchType.LAZY)
    // Many jobs can belong to one recruiter.
    //
    // Example:
    // Recruiter A
    //    ↓
    // ├── Job 1
    // ├── Job 2
    // └── Job 3

    @JoinColumn(name = "recruiter_id", nullable = false)
    // Creates/uses recruiter_id as the foreign-key column
    // in the jobs table.
    //
    // nullable = false means every job must have a recruiter.

    private User recruiter;


    @CreationTimestamp
    // Automatically records when the job was created.

    @Column(name = "created_at")
    // Maps to created_at in the database.

    private LocalDateTime createdAt;


    @UpdateTimestamp
    // Automatically updates when the job is modified.

    @Column(name = "updated_at")
    // Maps to updated_at in the database.

    private LocalDateTime updatedAt;


    @Enumerated(EnumType.STRING)
    // Stores JobStatus as text instead of numeric ordinal.
    //
    // Example:
    // DRAFT
    // OPEN
    // CLOSED
    // DELETED

    @Column(nullable = false)
    // Every job must have a status.

    private JobStatus status = JobStatus.DRAFT;
    // New jobs start in DRAFT status.


    private boolean deleted = false;
    // Soft-delete flag.
    //
    // false -> normal job
    // true  -> logically deleted job
    //
    // The database record remains.


    @Column(name = "application_start_time")
    // Beginning of the period during which candidates
    // are allowed to apply.

    private LocalDateTime applicationStartTime;


    @Column(name = "application_end_time")
    // End of the application period.

    private LocalDateTime applicationEndTime;


    private LocalDateTime deletedAt;
    // Stores when the job was soft-deleted.
    //
    // Example:
    // deleted = true
    // deletedAt = 2026-08-03T22:30:00


    @OneToMany(mappedBy = "job")
    // One Job can have many Applications.
    //
    // The "job" field inside Application owns this relationship.

    private List<Application> applications;
    // All applications submitted for this job.

}
package com.jobportal.talenthub.entity;
// Entity layer.
// Represents a job application in the TalentHub database.

import jakarta.persistence.*;
// JPA annotations for database/entity mapping.

import lombok.AllArgsConstructor;
// Generates a constructor with all fields.

import lombok.Getter;
// Generates getter methods.

import lombok.NoArgsConstructor;
// Generates the no-argument constructor required by JPA.

import lombok.Setter;
// Generates setter methods.

import org.hibernate.annotations.CreationTimestamp;
// Automatically records when the application is created.

import java.time.LocalDateTime;
// Used for the application and deletion timestamps.


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
// Marks Application as a JPA entity.

@Table(
        name = "applications",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"user_id", "job_id"}
                )
        }
)
// Maps the entity to the "applications" table.
//
// The unique constraint means:
// ONE user cannot apply to the SAME job more than once.
//
// Combination must be unique:
// user_id + job_id
//
// Example:
// User 5 + Job 10 -> allowed once
// User 5 + Job 10 -> duplicate -> database rejects it

public class Application {


    @Id
    // Primary key of the application.

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Database automatically generates the application ID.

    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    // Many applications can belong to one user.
    //
    // Example:
    // User A
    //   ↓
    // ├── Application 1
    // ├── Application 2
    // └── Application 3
    //
    // LAZY means the User entity is not loaded unnecessarily
    // until it is actually accessed.

    @JoinColumn(name = "user_id", nullable = false)
    // Creates/uses user_id as the foreign-key column.
    //
    // Every application must belong to a user.

    private User user;


    @ManyToOne(fetch = FetchType.LAZY)
    // Many applications can belong to one job.
    //
    // One job can receive applications from many users.

    @JoinColumn(name = "job_id", nullable = false)
    // Creates/uses job_id as the foreign-key column.
    //
    // Every application must belong to a job.

    private Job job;


    @Enumerated(EnumType.STRING)
    // Stores ApplicationStatus as text instead of its numeric ordinal.
    //
    // Example:
    // APPLIED
    // SHORTLISTED
    // REJECTED
    // etc.

    @Column(nullable = false)
    // Every application must have a status.

    private ApplicationStatus status;


    @CreationTimestamp
    // Hibernate automatically sets this when the application is created.

    @Column(name = "created_at", updatable = false)
    // Database column = created_at.
    // updatable = false means the original application time
    // should not be changed later.

    private LocalDateTime appliedAt;


    private boolean deleted = false;
    // Soft-delete flag.
    //
    // false -> normal application
    // true  -> logically deleted application
    //
    // The database record is still preserved.


    private LocalDateTime deletedAt;
    // Stores the date/time when the application was soft-deleted.

}


//Future improvement — Application updatedAt

// Currently NOT required.
// appliedAt tells us when the application was created,
// while deletedAt tells us when it was soft deleted.
//
// Later, if needed:
// @UpdateTimestamp
// @Column(name = "updated_at")
// private LocalDateTime updatedAt;
//
// However, for tracking status changes properly,
// a separate ApplicationStatusHistory design would be
// more useful than only storing updatedAt.
//
// Example future history:
// APPLIED → SHORTLISTED → INTERVIEWED → ACCEPTED
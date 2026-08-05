package com.jobportal.talenthub.entity;
// Entity layer of TalentHub.
// This class represents a user record in the database.

import jakarta.persistence.*;
// JPA annotations for mapping this Java class to a database table.

import lombok.AllArgsConstructor;
// Lombok generates a constructor containing all fields.

import lombok.Getter;
// Lombok generates getter methods.

import lombok.NoArgsConstructor;
// Lombok generates a no-argument constructor.
// JPA requires a no-argument constructor.

import lombok.Setter;
// Lombok generates setter methods.

import org.hibernate.annotations.CreationTimestamp;
// Automatically sets the creation timestamp.

import org.hibernate.annotations.UpdateTimestamp;
// Automatically updates the timestamp whenever the entity is updated.

import java.time.LocalDateTime;
// Used for createdAt, updatedAt and deletedAt.

import java.util.List;
// Used for the one-to-many relationship with applications.


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
// Marks User as a JPA entity.
// Hibernate will map this class to a database table.

@Table(name = "users")
// Explicitly defines the database table name as "users".

public class User {


    @Id
    // Marks id as the primary key.

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Database generates the ID automatically.
    // PostgreSQL identity/auto-increment behavior is used.

    private Long id;


    @Column(nullable = false)
    // firstName cannot be NULL in the database.

    private String firstName;


    @Column(nullable = false)
    // lastName cannot be NULL.

    private String lastName;


    @Column(nullable = false, unique = true, length = 100)
    // nullable = false -> email is required.
    // unique = true   -> duplicate emails are not allowed.
    // length = 100    -> database column length is limited to 100 characters.

    private String email;


    @Column(nullable = false)
    // Password is required in the database.
    //
    // Important:
    // The password stored here should be BCrypt-hashed,
    // not the user's plain-text password.

    private String password;


    @Enumerated(EnumType.STRING)
    // Stores the enum value as text instead of its numeric ordinal.
    //
    // Example:
    // JOB_SEEKER
    // RECRUITER
    // ADMIN

    @Column(nullable = false)
    // Every user must have a role.

    private Role role;


    @CreationTimestamp
    // Hibernate automatically sets this when the user is created.

    @Column(name = "created_at", updatable = false)
    // Database column name = created_at.
    // updatable = false means it should not be changed after creation.

    private LocalDateTime createdAt;


    @UpdateTimestamp
    // Hibernate automatically updates this timestamp
    // whenever the entity is updated.

    @Column(name = "updated_at")
    // Maps this field to the updated_at database column.

    private LocalDateTime updatedAt;


    @Enumerated(EnumType.STRING)
    // Stores UserStatus as text.
    //
    // Example:
    // ACTIVE
    // DEACTIVATED

    @Column(nullable = false)
    // Every user must have a status.

    private UserStatus status = UserStatus.ACTIVE;
    // New users are ACTIVE by default.
    //
    // This is separate from "deleted":
    //
    // status  -> business/account state
    // deleted -> soft-delete flag


    private boolean deleted = false;
    // Soft-delete flag.
    //
    // false -> normal user
    // true  -> logically deleted user
    //
    // The database record remains; it is not physically deleted.


    private LocalDateTime deletedAt;
    // Stores the date and time when the user was soft-deleted.
    //
    // Example:
    // deleted = true
    // deletedAt = 2026-08-03T22:30:00


    @OneToMany(mappedBy = "user")
    // One User can have many Applications.
    //
    // mappedBy = "user" means:
    // The "user" field inside Application owns the relationship.
    //
    // User
    //   ↓
    // Applications
    //   ↓
    // Application.user


    private List<Application> applications;
    // Collection of applications submitted by this user.

}
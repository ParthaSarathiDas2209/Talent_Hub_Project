package com.jobportal.talenthub.repository;
// Repository layer of the TalentHub application.

import com.jobportal.talenthub.entity.User;
// User entity that this repository manages.

import org.springframework.data.jpa.repository.JpaRepository;
// Provides built-in CRUD/database methods such as:
// save(), findById(), findAll(), delete(), existsById(), etc.

import org.springframework.stereotype.Repository;
// Marks this interface as a repository component.

import java.util.List;
// Used for methods returning multiple users.

import java.util.Optional;
// Used when a query may return one user or no user.

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
// UserRepository works with:
// User -> entity
// Long -> User's primary-key type
//
// JpaRepository gives us common database operations automatically.
// We only need to define custom queries that are specific to TalentHub.


    Optional<User> findByEmail(String email);
    // Finds a user using their email.
    //
    // Spring Data JPA understands the method name automatically.
    //
    // Conceptually:
    // SELECT * FROM users WHERE email = ?
    //
    // Returns Optional.empty() if no user is found.
    //
    // Mainly used during login/authentication.


    boolean existsByEmail(String email);
    // Checks whether a user with the given email already exists.
    //
    // true  -> email exists
    // false -> email does not exist
    //
    // Mainly used during registration to prevent duplicate emails.


    List<User> findAllByDeletedFalse();
    // Returns only users whose deleted flag is false.
    //
    // Conceptually:
    // SELECT * FROM users WHERE deleted = false
    //
    // Important for TalentHub's soft-delete system.
    // Deleted users remain in the database but are hidden
    // from normal user listings.


    Optional<User> findByIdAndDeletedFalse(Long id);
    // Finds a user by ID only when the user is not soft-deleted.
    //
    // Conceptually:
    // SELECT * FROM users
    // WHERE id = ? AND deleted = false
    //
    // Prevents normal application operations from accessing
    // a soft-deleted user.

}
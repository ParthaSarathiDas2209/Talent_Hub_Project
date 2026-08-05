package com.jobportal.talenthub.service.impl;

import com.jobportal.talenthub.dto.UserPatchDto;
import com.jobportal.talenthub.dto.UserRequestDto;
import com.jobportal.talenthub.dto.UserResponseDto;
import com.jobportal.talenthub.entity.User;
import com.jobportal.talenthub.entity.UserStatus;
import com.jobportal.talenthub.exception.JobApplicationException;
import com.jobportal.talenthub.exception.ResourceNotFoundException;
import com.jobportal.talenthub.mapper.UserMapper;
import com.jobportal.talenthub.repository.UserRepository;
import com.jobportal.talenthub.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    // Repository responsible for User database operations.
    private final UserRepository userRepository;

    // BCrypt encoder used to securely hash passwords
    // before they are stored in the database.
    private final PasswordEncoder passwordEncoder;


    // Constructor injection:
    // Spring provides the required dependencies automatically.
    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // =========================================================
    // CREATE USER
    // =========================================================

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {

        // Check whether another user already uses this email.
        // Email is unique in the database as well.
        if (userRepository.existsByEmail(userRequestDto.email())) {

            throw new JobApplicationException(
                    "Email already exists"
            );
        }


        // Convert the incoming DTO into a User entity.
        User user = UserMapper.toEntity(userRequestDto);


        // Encode the plain-text password before persistence.
        // The database must never contain the plain password.
        user.setPassword(
                passwordEncoder.encode(
                        userRequestDto.password()
                )
        );


        // New users start with ACTIVE status.
        user.setStatus(UserStatus.ACTIVE);


        // New users are not soft-deleted.
        user.setDeleted(false);
        user.setDeletedAt(null);


        // Save the entity to PostgreSQL.
        User savedUser = userRepository.save(user);


        // Return a DTO instead of exposing the entity directly.
        return UserMapper.toResponseDto(savedUser);
    }


    // =========================================================
    // FULL UPDATE
    // =========================================================

    @Override
    public UserResponseDto updateUser(
            Long id,
            UserRequestDto userRequestDto) {

        // Find only a non-deleted user.
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User Not Found with id : " + id
                        )
                );


        // Check whether the new email already belongs to
        // a different user.
        //
        // The current user's existing email is allowed.
        if (userRepository.existsByEmail(userRequestDto.email())
                && !user.getEmail().equals(userRequestDto.email())) {

            throw new JobApplicationException(
                    "Email Already Exists."
            );
        }


        // Update the user's basic information.
        user.setFirstName(userRequestDto.firstName());
        user.setLastName(userRequestDto.lastName());
        user.setEmail(userRequestDto.email());


        // Encode the new password before storing it.
        user.setPassword(
                passwordEncoder.encode(
                        userRequestDto.password()
                )
        );


        // Update the user's role.
        user.setRole(userRequestDto.role());


        // Save the modified entity.
        User updatedUser = userRepository.save(user);


        // Return the updated data through a DTO.
        return UserMapper.toResponseDto(updatedUser);
    }


    // =========================================================
    // PARTIAL UPDATE
    // =========================================================

    @Override
    public UserResponseDto patchUser(
            Long id,
            UserPatchDto userPatchDto) {

        // Find only a non-deleted user.
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User Not Found with id : " + id
                        )
                );


        // Update first name only when the client provides it.
        if (userPatchDto.firstName() != null) {

            user.setFirstName(
                    userPatchDto.firstName()
            );
        }


        // Update last name only when the client provides it.
        if (userPatchDto.lastName() != null) {

            user.setLastName(
                    userPatchDto.lastName()
            );
        }


        // Check email uniqueness only when email is being changed.
        if (userPatchDto.email() != null
                && userRepository.existsByEmail(userPatchDto.email())
                && !user.getEmail().equals(userPatchDto.email())) {

            throw new JobApplicationException(
                    "Email Already Exists."
            );
        }


        // Update email only when supplied.
        if (userPatchDto.email() != null) {

            user.setEmail(
                    userPatchDto.email()
            );
        }


        // Update password only when supplied.
        // The new password is encoded before persistence.
        if (userPatchDto.password() != null) {

            user.setPassword(
                    passwordEncoder.encode(
                            userPatchDto.password()
                    )
            );
        }


        // Update role only when supplied.
        if (userPatchDto.role() != null) {

            user.setRole(
                    userPatchDto.role()
            );
        }


        // Save the partially modified entity.
        User updatedUser = userRepository.save(user);


        // Return the updated user through a DTO.
        return UserMapper.toResponseDto(updatedUser);
    }


    // =========================================================
    // GET ALL USERS
    // =========================================================

    @Override
    public List<UserResponseDto> getAllUsers() {

        // Fetch only users that have not been soft-deleted.
        List<User> users =
                userRepository.findAllByDeletedFalse();


        // Convert every entity into a response DTO.
        //
        // This also prevents sensitive fields such as password
        // from being exposed through the API.
        return users.stream()
                .map(UserMapper::toResponseDto)
                .toList();
    }


    // =========================================================
    // GET USER BY ID
    // =========================================================

    @Override
    public UserResponseDto getUserById(Long id) {

        // Find the user only if it has not been soft-deleted.
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User Not Found with id : " + id
                        )
                );


        // Convert entity into a safe response DTO.
        return UserMapper.toResponseDto(user);
    }


    // =========================================================
    // SOFT DELETE USER
    // =========================================================

    @Override
    public void deleteUser(Long id) {

        // Find only a user that has not already been soft-deleted.
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User Not Found with id: " + id
                        )
                );


        // Defensive check in case this method is called directly
        // without the repository's deleted=false condition.
        if (user.isDeleted()) {

            throw new JobApplicationException(
                    "User has already been deleted"
            );
        }


        // Soft delete:
        // Keep the database row but mark the user as deleted.
        user.setDeleted(true);


        // Record exactly when the soft delete happened.
        user.setDeletedAt(LocalDateTime.now());


        // Deactivated users should no longer be treated
        // as active accounts.
        user.setStatus(UserStatus.DEACTIVATED);


        // Persist the soft-delete changes.
        userRepository.save(user);
    }


    // =========================================================
    // FUTURE IMPROVEMENTS
    // =========================================================

    // 1. PATCH validation refinement:
    //    UserPatchDto currently allows null fields, which is correct
    //    for PATCH, but supplied non-null values should still satisfy
    //    appropriate validation rules.
    //
    // 2. Ownership/security:
    //    The service currently accepts a user ID.
    //    Whether a normal user may update/delete that ID should be
    //    controlled by the security/controller/business layer.
    //
    // 3. Role changes:
    //    Changing ROLE_ADMIN/RECRUITER/JOB_SEEKER is a sensitive operation.
    //    In a production application, normal users should generally
    //    not be allowed to change their own role.
    //
    // 4. Password:
    //    Password is correctly encoded before save.
    //
    // 5. updatedAt:
    //    User already has @UpdateTimestamp, so Hibernate automatically
    //    updates updatedAt when the entity is modified.
    //
    // 6. Soft delete:
    //    The record remains in the database.
    //    findAllByDeletedFalse() and findByIdAndDeletedFalse()
    //    hide deleted users from normal operations.
}

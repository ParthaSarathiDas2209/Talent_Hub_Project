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

    // Repository used to perform User database operations.
    private final UserRepository userRepository;

    // Used to encode passwords securely using BCrypt.
    private final PasswordEncoder passwordEncoder;


    // Constructor Injection:
    // Spring injects the required dependencies.
    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {

        // STEP 1:
        // Check whether the email is already registered.
        if (userRepository.existsByEmail(userRequestDto.email())) {

            throw new JobApplicationException(
                    "Email already exists"
            );
        }


        // STEP 2:
        // Convert the incoming DTO into a User entity.
        User user = UserMapper.toEntity(userRequestDto);


        // STEP 3:
        // Encode the plain-text password using BCrypt
        // before storing it in the database.
        user.setPassword(
                passwordEncoder.encode(
                        userRequestDto.password()
                )
        );


        // STEP 4:
        // Newly created users start with ACTIVE status.
        user.setStatus(UserStatus.ACTIVE);


        // STEP 5:
        // Newly created users are not soft-deleted.
        user.setDeleted(false);
        user.setDeletedAt(null);


        // STEP 6:
        // Save the new user in the database.
        User savedUser = userRepository.save(user);


        // STEP 7:
        // Return a response DTO instead of exposing the entity.
        return UserMapper.toResponseDto(savedUser);
    }


    @Override
    public UserResponseDto updateUser(
            Long id,
            UserRequestDto userRequestDto) {

        // STEP 1:
        // Find the user only if the user is not soft-deleted.
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User Not Found with id : " + id
                        )
                );


        // STEP 2:
        // Check whether the requested email belongs
        // to another user.
        //
        // The current user's existing email is allowed.
        if (userRepository.existsByEmail(userRequestDto.email())
                && !user.getEmail().equals(userRequestDto.email())) {

            throw new JobApplicationException(
                    "Email Already Exists."
            );
        }


        // STEP 3:
        // Update the user's basic information.
        user.setFirstName(userRequestDto.firstName());
        user.setLastName(userRequestDto.lastName());
        user.setEmail(userRequestDto.email());


        // STEP 4:
        // Encode the new password using BCrypt.
        // Never store the plain-text password.
        user.setPassword(
                passwordEncoder.encode(
                        userRequestDto.password()
                )
        );


        // STEP 5:
        // Update the user's role.
        user.setRole(userRequestDto.role());


        // STEP 6:
        // Save the updated user.
        User updatedUser = userRepository.save(user);


        // STEP 7:
        // Return the updated user as a response DTO.
        return UserMapper.toResponseDto(updatedUser);
    }


    @Override
    public UserResponseDto patchUser(
            Long id,
            UserPatchDto userPatchDto) {

        // STEP 1:
        // Find the user only if the user is not soft-deleted.
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User Not Found with id : " + id
                        )
                );


        // STEP 2:
        // Update first name only when a value is provided.
        if (userPatchDto.firstName() != null) {

            user.setFirstName(
                    userPatchDto.firstName()
            );
        }


        // STEP 3:
        // Update last name only when a value is provided.
        if (userPatchDto.lastName() != null) {

            user.setLastName(
                    userPatchDto.lastName()
            );
        }


        // STEP 4:
        // Check email uniqueness only when email is being changed.
        //
        // The current user's existing email is allowed.
        if (userPatchDto.email() != null
                && userRepository.existsByEmail(userPatchDto.email())
                && !user.getEmail().equals(userPatchDto.email())) {

            throw new JobApplicationException(
                    "Email Already Exists."
            );
        }


        // STEP 5:
        // Update email only when a new value is provided.
        if (userPatchDto.email() != null) {

            user.setEmail(
                    userPatchDto.email()
            );
        }


        // STEP 6:
        // Update password only when a new password is provided.
        //
        // The password is encoded before being stored.
        if (userPatchDto.password() != null) {

            user.setPassword(
                    passwordEncoder.encode(
                            userPatchDto.password()
                    )
            );
        }


        // STEP 7:
        // Update role only when a new role is provided.
        if (userPatchDto.role() != null) {

            user.setRole(
                    userPatchDto.role()
            );
        }


        // STEP 8:
        // Save the partially updated user.
        User updatedUser = userRepository.save(user);


        // STEP 9:
        // Return the updated user as a response DTO.
        return UserMapper.toResponseDto(updatedUser);
    }


    @Override
    public List<UserResponseDto> getAllUsers() {

        // STEP 1:
        // Fetch only users who have not been soft-deleted.
        List<User> users = userRepository.findAllByDeletedFalse();


        // STEP 2:
        // Convert User entities into response DTOs.
        //
        // The DTO prevents sensitive entity data,
        // such as the password, from being exposed.
        return users.stream()
                .map(UserMapper::toResponseDto)
                .toList();
    }


    @Override
    public UserResponseDto getUserById(Long id) {

        // STEP 1:
        // Find the user only if the user is not soft-deleted.
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User Not Found with id : " + id
                        )
                );


        // STEP 2:
        // Convert the User entity into a response DTO.
        return UserMapper.toResponseDto(user);
    }


    @Override
    public void deleteUser(Long id) {

        // STEP 1:
        // Find the user only if the user is not already soft-deleted.
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User Not Found with id: " + id
                        )
                );


        // STEP 2:
        // Prevent deletion if the account has already been deleted.
        if (user.isDeleted()) {

            throw new JobApplicationException(
                    "User has already been deleted"
            );
        }


        // STEP 3:
        // Mark the user as deleted instead of physically
        // removing the database record.
        user.setDeleted(true);


        // STEP 4:
        // Store the exact date and time of the soft delete.
        user.setDeletedAt(LocalDateTime.now());


        // STEP 5:
        // Mark the account as DEACTIVATED because
        // the user is no longer an active account.
        user.setStatus(UserStatus.DEACTIVATED);


        // STEP 6:
        // Save the soft-delete information in the database.
        userRepository.save(user);
    }
}
package com.jobportal.talenthub.service;

import com.jobportal.talenthub.dto.UserPatchDto;
import com.jobportal.talenthub.dto.UserRequestDto;
import com.jobportal.talenthub.dto.UserResponseDto;

import java.util.List;

public interface UserService {

    // =========================================================
    // USER MANAGEMENT
    // =========================================================

    // Create a new user.
    UserResponseDto createUser(
            UserRequestDto userRequestDto
    );

    // Fully update an existing user.
    UserResponseDto updateUser(
            Long id,
            UserRequestDto userRequestDto
    );

    // Partially update only the fields provided by the client.
    UserResponseDto patchUser(
            Long id,
            UserPatchDto userPatchDto
    );

//    // Alternative PATCH approach using a dynamic Map.
//    // Not used because UserPatchDto provides better type safety.
//    UserResponseDto patchUser(
//            Long id,
//            Map<String, Object> updates
//    );

    // Get all users that are not soft-deleted.
    List<UserResponseDto> getAllUsers();

    // Get a specific user by ID.
    UserResponseDto getUserById(
            Long id
    );

    // Soft-delete a user instead of physically deleting
    // the database record.
    void deleteUser(
            Long id
    );
}
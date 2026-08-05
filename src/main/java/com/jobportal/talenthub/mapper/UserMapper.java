package com.jobportal.talenthub.mapper;

import com.jobportal.talenthub.dto.UserRequestDto;
import com.jobportal.talenthub.dto.UserResponseDto;
import com.jobportal.talenthub.entity.User;

// Utility class responsible for converting between
// User entities and User DTOs.
//
// Keeps mapping logic out of the service layer.
public class UserMapper {

    // Converts incoming UserRequestDto data into a User entity.
    // Used when creating/registering a new user.
    public static User toEntity(UserRequestDto userRequestDto) {

        // Create a new User entity.
        User user = new User();

        // Copy validated DTO values into the entity.
        user.setFirstName(userRequestDto.firstName());
        user.setLastName(userRequestDto.lastName());
        user.setEmail(userRequestDto.email());

        // Password is initially copied from the DTO.
        // IMPORTANT:
        // PasswordEncoder must encode it before the entity
        // is persisted to the database.
        user.setPassword(userRequestDto.password());

        // Set the requested role.
        user.setRole(userRequestDto.role());

        return user;
    }


    // Converts a User entity into UserResponseDto.
    // Used when returning user information through the API.
    public static UserResponseDto toResponseDto(User user) {

        return new UserResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    // =========================================================
    // FUTURE IMPROVEMENTS
    // =========================================================

    // 1. Password must NEVER be mapped into UserResponseDto.
    //    Your current implementation correctly excludes it.
    //
    // 2. If UserResponseDto later includes updatedAt/status,
    //    those fields can be mapped here as well.
    //
    // 3. If the project grows, this class can be made a final
    //    utility class with a private constructor to prevent
    //    unnecessary object creation.
}
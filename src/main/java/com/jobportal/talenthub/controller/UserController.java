package com.jobportal.talenthub.controller;

import com.jobportal.talenthub.dto.UserPatchDto;
import com.jobportal.talenthub.dto.UserRequestDto;
import com.jobportal.talenthub.dto.UserResponseDto;
import com.jobportal.talenthub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // Service layer responsible for User business logic.
    private final UserService userService;

    // Constructor Injection:
    // Spring injects the UserService implementation.
    public UserController(UserService userService) {
        this.userService = userService;
    }


    // =========================================================
    // CREATE USER
    // =========================================================

    // POST /api/users
    // Creates a new user.
    //
    // @Valid:
    // Triggers validation rules defined in UserRequestDto.
    //
    // @RequestBody:
    // Converts the incoming JSON request into UserRequestDto.
    //
    // 201 CREATED:
    // Indicates that a new resource was successfully created.
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(
            @Valid @RequestBody UserRequestDto userRequestDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(userRequestDto));
    }


    // =========================================================
    // FULL UPDATE USER
    // =========================================================

    // PUT /api/users/{id}
    // Fully updates an existing user.
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto userRequestDto) {

        return ResponseEntity.ok(
                userService.updateUser(id, userRequestDto)
        );
    }


    // =========================================================
    // GET ALL USERS
    // =========================================================

    // GET /api/users
    // Returns all users that are not soft-deleted.
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {

        return ResponseEntity.ok(
                userService.getAllUsers()
        );
    }


    // =========================================================
    // GET USER BY ID
    // =========================================================

    // GET /api/users/{id}
    // Returns a specific user by ID.
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                userService.getUserById(id)
        );
    }


//    // Temporary endpoint used earlier to verify
//    // whether the user endpoint was reachable.
//    @GetMapping("/{id}")
//    public ResponseEntity<String> getUserById(
//            @PathVariable Long id) {
//
//        return ResponseEntity.ok(
//                "User endpoint is working : " + id
//        );
//    }


    // =========================================================
    // DELETE USER
    // =========================================================

    // DELETE /api/users/{id}
    // Performs a soft delete through UserService.
    //
    // The database record is not physically removed.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity.ok(
                "User have been Deleted Successfully!"
        );
    }


//    // Previous PATCH approach using Map<String, Object>.
//    // Kept only for reference; UserPatchDto is the preferred approach.
//    @PatchMapping("/{id}")
//    public ResponseEntity<UserResponseDto> patchUser(
//            @PathVariable Long id,
//            @RequestBody Map<String, Object> updates) {
//
//        return ResponseEntity.ok(
//                userService.patchUser(id, updates)
//        );
//    }


    // =========================================================
    // PARTIAL UPDATE USER
    // =========================================================

    // PATCH /api/users/{id}
    // Updates only the fields provided in UserPatchDto.
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDto> patchUser(
            @PathVariable Long id,
            @RequestBody UserPatchDto userPatchDto) {

        return ResponseEntity.ok(
                userService.patchUser(id, userPatchDto)
        );
    }
}
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

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
//        return ResponseEntity.ok(userService.createUser(userRequestDto)); // 200 OK
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(userRequestDto));   // 201 Created
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok(userService.updateUser(id, userRequestDto));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
//        return ResponseEntity.noContent().build();
        return ResponseEntity.ok("User have been Deleted Successfully!");
    }

//    @PatchMapping("/{id}")
//    public ResponseEntity<UserResponseDto> patchUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
//        return ResponseEntity.ok(userService.patchUser(id, updates));
//    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDto> patchUser(@PathVariable Long id, @RequestBody UserPatchDto userPatchDto) {
        return ResponseEntity.ok(userService.patchUser(id, userPatchDto));
    }
}
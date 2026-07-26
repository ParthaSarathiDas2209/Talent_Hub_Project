package com.jobportal.talenthub.controller;

import com.jobportal.talenthub.dto.ApplicationResponseDto;
import com.jobportal.talenthub.dto.JobResponseDto;
import com.jobportal.talenthub.dto.UserResponseDto;
import com.jobportal.talenthub.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers()
        );
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> getUsersById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUsersById(id)
        );
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id) {
        adminService.deleteUserById(id);
        return ResponseEntity.ok("User have been Deleted Successfully : " + id);
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<JobResponseDto>> getAllJobs() {
        return ResponseEntity.ok(adminService.getAllJobs());
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<JobResponseDto> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getJobById(id));
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<String> deleteJobById(@PathVariable Long id) {
        adminService.deleteJobById(id);
        return ResponseEntity.ok("Job has been deleted Successfully : " + id);
    }

    @GetMapping("/applications")
    public ResponseEntity<List<ApplicationResponseDto>> getAllApplications() {
        return ResponseEntity.ok(adminService.getAllApplications());
    }

    @GetMapping("/applications/{id}")
    public ResponseEntity<ApplicationResponseDto> getApplicationById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getApplicationById(id));
    }

    @DeleteMapping("/applications/{id}")
    public ResponseEntity<String> deleteApplicationById(@PathVariable Long id) {
        adminService.deleteApplicationById(id);
        return ResponseEntity.ok("Application id not found :" + id);
    }


}


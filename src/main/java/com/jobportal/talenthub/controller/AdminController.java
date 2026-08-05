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

    // Service layer containing admin-specific business operations.
    private final AdminService adminService;

    // Constructor Injection:
    // Spring injects the AdminService dependency.
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }


    // ==========================================================
    // USER MANAGEMENT
    // ==========================================================

    // GET /api/admin/users
    // Fetch all users that are not soft-deleted.
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {

        return ResponseEntity.ok(
                adminService.getAllUsers()
        );
    }


    // GET /api/admin/users/{id}
    // Fetch a specific user by ID.
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> getUsersById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                adminService.getUsersById(id)
        );
    }


    // DELETE /api/admin/users/{id}
    // Soft-delete a user instead of physically removing
    // the user record from the database.
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUserById(
            @PathVariable Long id) {

        adminService.deleteUserById(id);

        return ResponseEntity.ok(
                "User have been Deleted Successfully : " + id
        );
    }


    // ==========================================================
    // JOB MANAGEMENT
    // ==========================================================

    // GET /api/admin/jobs
    // Fetch all jobs that are not soft-deleted.
    @GetMapping("/jobs")
    public ResponseEntity<List<JobResponseDto>> getAllJobs() {

        return ResponseEntity.ok(
                adminService.getAllJobs()
        );
    }


    // GET /api/admin/jobs/{id}
    // Fetch a specific job by ID.
    @GetMapping("/jobs/{id}")
    public ResponseEntity<JobResponseDto> getJobById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                adminService.getJobById(id)
        );
    }


    // DELETE /api/admin/jobs/{id}
    // Soft-delete a job instead of physically deleting
    // the database record.
    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<String> deleteJobById(
            @PathVariable Long id) {

        adminService.deleteJobById(id);

        return ResponseEntity.ok(
                "Job has been deleted Successfully : " + id
        );
    }


    // ==========================================================
    // APPLICATION MANAGEMENT
    // ==========================================================

    // GET /api/admin/applications
    // Fetch all applications that are not soft-deleted.
    @GetMapping("/applications")
    public ResponseEntity<List<ApplicationResponseDto>> getAllApplications() {

        return ResponseEntity.ok(
                adminService.getAllApplications()
        );
    }


    // GET /api/admin/applications/{id}
    // Fetch a specific application by ID.
    @GetMapping("/applications/{id}")
    public ResponseEntity<ApplicationResponseDto> getApplicationById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                adminService.getApplicationById(id)
        );
    }


    // DELETE /api/admin/applications/{id}
    // Soft-delete an application instead of physically
    // removing it from the database.
    @DeleteMapping("/applications/{id}")
    public ResponseEntity<String> deleteApplicationById(
            @PathVariable Long id) {

        adminService.deleteApplicationById(id);

        return ResponseEntity.ok(
                "Application has been deleted successfully : " + id
        );
    }
}
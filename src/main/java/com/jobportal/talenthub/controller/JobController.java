package com.jobportal.talenthub.controller;

import com.jobportal.talenthub.dto.JobPatchDto;
import com.jobportal.talenthub.dto.JobRequestDto;
import com.jobportal.talenthub.dto.JobResponseDto;
import com.jobportal.talenthub.service.JobService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    // Service layer responsible for Job business logic.
    private final JobService jobService;

    // Constructor Injection:
    // Spring injects the JobService implementation.
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }


    // =========================================================
    // CREATE JOB
    // =========================================================

    // POST /api/jobs
    // Creates a new job for the logged-in recruiter.
    //
    // Authentication.getName()
    //      ↓
    // Returns the authenticated user's email from Spring Security.
    //
    // @Valid:
    // Triggers validation rules defined in JobRequestDto.
    @PostMapping
    public ResponseEntity<JobResponseDto> createJob(
            @Valid @RequestBody JobRequestDto jobRequestDto,
            Authentication authentication) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        jobService.createJob(
                                jobRequestDto,
                                authentication.getName()
                        )
                );
    }


    // =========================================================
    // FULL UPDATE JOB
    // =========================================================

    // PUT /api/jobs/{id}
    // Fully updates an existing job.
    //
    // The authenticated user's email is passed to the service
    // so the service can verify job ownership.
    @PutMapping("/{id}")
    public ResponseEntity<JobResponseDto> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobRequestDto jobRequestDto,
            Authentication authentication) {

        return ResponseEntity.ok(
                jobService.updateJob(
                        id,
                        jobRequestDto,
                        authentication.getName()
                )
        );
    }


//    // Previous implementation kept for reference.
//    // The updated version passes the authenticated user's email
//    // so ownership can be checked.
//    return ResponseEntity.status(HttpStatus.OK)
//            .body(jobService.updateJob(id, jobRequestDto));


    // =========================================================
    // DELETE JOB
    // =========================================================

    // DELETE /api/jobs/{id}
    // Soft-deletes the job owned by the logged-in recruiter.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJob(
            @PathVariable Long id,
            Authentication authentication) {

        jobService.deleteJob(
                id,
                authentication.getName()
        );

        return ResponseEntity.ok(
                "Job has been deleted successfully!"
        );
    }


    // =========================================================
    // GET JOB BY ID
    // =========================================================

    // GET /api/jobs/{id}
    // Returns a specific job by ID.
    //
    // No Authentication parameter is required because
    // viewing a job is currently treated as a general operation.
    @GetMapping("/{id}")
    public ResponseEntity<JobResponseDto> getJobById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                jobService.getJobById(id)
        );
    }


    // =========================================================
    // GET ALL JOBS
    // =========================================================

    // GET /api/jobs
    // Returns all jobs that are not soft-deleted.

//    @GetMapping
//    public ResponseEntity<List<JobResponseDto>> getAllJobs() {
//
//        return ResponseEntity.ok(
//                jobService.getAllJobs()
//        );
//    }

    @GetMapping
    public ResponseEntity<Page<JobResponseDto>> getAllJobs(Pageable pageable) {
        return ResponseEntity.ok(jobService.getAllJobs(pageable));
    }

    // =========================================================
    // PARTIAL UPDATE JOB
    // =========================================================

    // PATCH /api/jobs/{id}
    // Updates only the fields supplied in JobPatchDto.
    //
    // @Valid:
    // Triggers validation rules defined in JobPatchDto.
    //
    // Authentication.getName():
    // Provides the logged-in user's email for ownership validation.
    @PatchMapping("/{id}")
    public ResponseEntity<JobResponseDto> patchJob(
            @PathVariable Long id,
            @Valid @RequestBody JobPatchDto jobPatchDto,
            Authentication authentication) {

        return ResponseEntity.ok(
                jobService.patchJob(
                        id,
                        jobPatchDto,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<JobResponseDto>> searchJobs(
            @RequestParam String keyword) {
        return ResponseEntity.ok(
                jobService.searchJobs(keyword)
        );
    }


    @GetMapping("/filter/location")
    public ResponseEntity<Page<JobResponseDto>> filterByLocation(
            @RequestParam String location, Pageable pageable) {
        return ResponseEntity.ok(
                jobService.filterByLocation(location, pageable));
    }


    @GetMapping("/filter/salary")
    public ResponseEntity<Page<JobResponseDto>> filterBySalary(
            @RequestParam Long minSalary,
            @RequestParam Long maxSalary,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                jobService.filterBySalary(
                        minSalary, maxSalary, pageable
                )
        );
    }

    @GetMapping("/filter/company-title")
    public ResponseEntity<Page<JobResponseDto>> filterByCompanyAndTitle(
            @RequestParam String companyName,
            @RequestParam String title,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                jobService.filterByCompanyAndTitle(
                        companyName,
                        title,
                        pageable
                )
        );
    }


}
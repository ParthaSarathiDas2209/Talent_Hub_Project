package com.jobportal.talenthub.controller;

import com.jobportal.talenthub.dto.JobPatchDto;
import com.jobportal.talenthub.dto.JobRequestDto;
import com.jobportal.talenthub.dto.JobResponseDto;
import com.jobportal.talenthub.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public ResponseEntity<JobResponseDto> createJob(@Valid @RequestBody JobRequestDto jobRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobService.createJob(jobRequestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobResponseDto> updateJob(@PathVariable Long id, @Valid @RequestBody JobRequestDto jobRequestDto) {
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(jobService.updateJob(id, jobRequestDto));

        return ResponseEntity.ok(
                jobService.updateJob(id, jobRequestDto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.ok("Job has been deleted successfully! ");
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponseDto> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @GetMapping
    public ResponseEntity<List<JobResponseDto>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<JobResponseDto> patchJob(@PathVariable Long id, @Valid @RequestBody JobPatchDto jobPatchDto) {
        return ResponseEntity.ok(jobService.patchJob(id, jobPatchDto));
    }

}

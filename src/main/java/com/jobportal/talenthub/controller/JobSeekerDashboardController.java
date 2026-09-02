package com.jobportal.talenthub.controller;


import com.jobportal.talenthub.dto.JobSeekerDashboardDto;
import com.jobportal.talenthub.service.JobSeekerDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard/job-seeker")
public class JobSeekerDashboardController {

    private final JobSeekerDashboardService jobSeekerDashboardService;

    public JobSeekerDashboardController(JobSeekerDashboardService jobSeekerDashboardService) {
        this.jobSeekerDashboardService = jobSeekerDashboardService;
    }

    @GetMapping
    public ResponseEntity<JobSeekerDashboardDto> getDashboard() {
        return ResponseEntity.ok(
                jobSeekerDashboardService.getDashboard()
        );
    }
}

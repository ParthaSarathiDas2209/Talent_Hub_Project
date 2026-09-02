package com.jobportal.talenthub.controller;

import com.jobportal.talenthub.dto.RecruiterDashboardDto;
import com.jobportal.talenthub.service.RecruiterDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard/recruiter")
public class RecruiterDashboardController {

    private final RecruiterDashboardService recruiterDashboardService;

    public RecruiterDashboardController(RecruiterDashboardService recruiterDashboardService) {
        this.recruiterDashboardService = recruiterDashboardService;
    }

    @GetMapping
    public ResponseEntity<RecruiterDashboardDto> getRecruiterDashboard() {
        return ResponseEntity.ok(
                recruiterDashboardService.getRecruiterDashboard()
        );
    }
}

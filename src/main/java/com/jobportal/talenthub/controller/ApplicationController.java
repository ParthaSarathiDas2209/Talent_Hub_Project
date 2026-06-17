package com.jobportal.talenthub.controller;


import com.jobportal.talenthub.dto.ApplicationRequestDto;
import com.jobportal.talenthub.dto.ApplicationResponseDto;
import com.jobportal.talenthub.service.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }


    @PostMapping
    public ResponseEntity<ApplicationResponseDto> applyJob(@RequestBody ApplicationRequestDto applicationRequestDto) {
//        return ResponseEntity.ok(applicationService.applyJob(applicationRequestDto));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicationService.applyJob(applicationRequestDto)
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponseDto> getApplicationById(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getApplicationById(id));
    }

    @GetMapping
    public ResponseEntity<List<ApplicationResponseDto>> getAllApplications() {
        return ResponseEntity.ok(applicationService.getAllApplications());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteApplicationById(@PathVariable Long id) {
        applicationService.deleteApplication(id);
        return ResponseEntity.ok("Application has been deleted successfully ! ");
    }
}

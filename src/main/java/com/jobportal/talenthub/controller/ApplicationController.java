package com.jobportal.talenthub.controller;


import com.jobportal.talenthub.dto.ApplicationRequestDto;
import com.jobportal.talenthub.dto.ApplicationResponseDto;
import com.jobportal.talenthub.entity.ApplicationStatus;
import com.jobportal.talenthub.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<ApplicationResponseDto> applyJob(
            @Valid @RequestBody ApplicationRequestDto applicationRequestDto,
            Authentication authentication) {
//        return ResponseEntity.ok(applicationService.applyJob(applicationRequestDto));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicationService.applyJob(
                        applicationRequestDto,
                        authentication.getName())
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponseDto> getApplicationById(@PathVariable Long id,
                                                                     Authentication authentication) {
        return ResponseEntity.ok(
                applicationService.getApplicationById(
                        id,
                        authentication.getName())
        );
    }

    @GetMapping
    public ResponseEntity<List<ApplicationResponseDto>> getAllApplications(Authentication authentication) {
        return ResponseEntity.ok(applicationService
                .getAllApplications(
                        authentication.getName())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteApplicationById(@PathVariable Long id,
                                                        Authentication authentication) {
        applicationService.deleteApplication(id,
                authentication.getName()
        );

        return ResponseEntity.ok("Application deleted successfully ! ");
    }

    @PatchMapping("/{applicationId}/status")
    public ResponseEntity<ApplicationResponseDto> updateApplicationStatus(@PathVariable Long applicationId,
                                                                          @RequestParam ApplicationStatus applicationStatus,
                                                                          Authentication authentication) {

        ApplicationResponseDto updatedApplication =
                applicationService.updateApplicationStatus(applicationId, applicationStatus,
                        authentication.getName()
                );

        return ResponseEntity.ok(updatedApplication);
    }

    @GetMapping("/recruiter")
    public ResponseEntity<List<ApplicationResponseDto>> getRecruiterApplications(Authentication authentication) {
        return ResponseEntity.ok(applicationService.getRecruiterApplications(
                authentication.getName()
        ));
    }
}

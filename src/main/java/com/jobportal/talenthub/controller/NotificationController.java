package com.jobportal.talenthub.controller;

import com.jobportal.talenthub.dto.NotificationResponseDto;
import com.jobportal.talenthub.entity.User;
import com.jobportal.talenthub.exception.ResourceNotFoundException;
import com.jobportal.talenthub.repository.UserRepository;
import com.jobportal.talenthub.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public NotificationController(NotificationService notificationService, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getMyNotifications(
            Authentication authentication
    ) {

        User loggedInUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Logged-in user not found"
                        )
                );

        List<NotificationResponseDto> notifications = notificationService
                .getUserNotifications(loggedInUser);

        return ResponseEntity.ok(notifications);

    }

    @PutMapping("/{notificationId}/read")
    public void markAsRead(@PathVariable Long notificationId, Authentication authentication) {
        User loggedInUser = userRepository.findByEmail(
                        authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Logged-in user not found"
                        )
                );


        notificationService.markAsRead(notificationId, loggedInUser);
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> countUnreadNotifications(Authentication authentication) {
        User loggedInUser = userRepository.findByEmail(
                authentication.getName()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Logged-in user not found"
                )
        );

        long count = notificationService.countUnreadNotifications(loggedInUser);
        
        return ResponseEntity.ok(count);
    }

}

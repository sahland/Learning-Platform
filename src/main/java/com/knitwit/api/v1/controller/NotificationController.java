package com.knitwit.api.v1.controller;

import com.knitwit.api.v1.dto.request.NotificationRequest;
import com.knitwit.api.v1.dto.response.NotificationResponse;
import com.knitwit.mapper.NotificationMapper;
import com.knitwit.model.Notification;
import com.knitwit.model.User;
import com.knitwit.repository.UserRepository;
import com.knitwit.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
@SecurityRequirement(name = "Keycloak")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;
    private final UserRepository userRepository;

    @Operation(summary = "Добавление уведомления")
    @PostMapping("/save")
    public ResponseEntity<NotificationResponse> createNotification(@RequestBody NotificationRequest request,
                                                                   @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaim("preferred_username");
        Optional<User> creator = userRepository.findByKeycloakLogin(username);
        Notification notification = notificationMapper.toEntity(request);
        notification.setSenderId(creator.get().getUserId());
        Notification savedNotification = notificationService.createNotification(notification);
        NotificationResponse response = notificationMapper.toResponse(savedNotification);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получение всех уведомлений")
    @GetMapping("/all")
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        List<NotificationResponse> responses = notificationMapper.toResponseList(notifications);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Получение уведомления по его ID")
    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable int notificationId) {
        Notification notification = notificationService.getNotificationById(notificationId);
        NotificationResponse response = notificationMapper.toResponse(notification);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Удаление уведомления по его ID")
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotificationById(@PathVariable int notificationId) {
        notificationService.deleteNotificationById(notificationId);
        return ResponseEntity.ok().build();
    }
}

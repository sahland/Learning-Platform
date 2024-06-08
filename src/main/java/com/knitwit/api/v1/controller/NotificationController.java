package com.knitwit.api.v1.controller;

import com.knitwit.api.v1.dto.request.NotificationRequest;
import com.knitwit.api.v1.dto.response.NotificationResponse;
import com.knitwit.api.v1.dto.mapper.NotificationMapper;
import com.knitwit.model.Notification;
import com.knitwit.model.User;
import com.knitwit.repository.UserRepository;
import com.knitwit.service.AuthService;
import com.knitwit.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Operation(summary = "Добавление уведомления (ADMIN)")
    @Secured("ROLE_ADMIN")
    @PostMapping("/save")
    public ResponseEntity<NotificationResponse> createNotification(@RequestBody NotificationRequest request) {
        String username = authService.getCurrentUsername();
        Optional<User> creator = userRepository.findByUsername(username);
        Notification notification = notificationMapper.toEntity(request);
        notification.setSenderId(creator.get().getUserId());
        Notification savedNotification = notificationService.createNotification(notification);
        NotificationResponse response = notificationMapper.toResponse(savedNotification);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получение всех уведомлений (USER)" )
    @Secured("ROLE_USER")
    @GetMapping("/all")
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        List<NotificationResponse> responses = notificationMapper.toResponseList(notifications);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Получение уведомления по его ID (USER)")
    @Secured("ROLE_USER")
    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable int notificationId) {
        Notification notification = notificationService.getNotificationById(notificationId);
        NotificationResponse response = notificationMapper.toResponse(notification);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Удаление уведомления по его ID (ADMIN)")
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{notificationId}/delete")
    public ResponseEntity<?> deleteNotificationById(@PathVariable int notificationId) {
        notificationService.deleteNotificationById(notificationId);
        return ResponseEntity.ok().build();
    }
}

package com.knitwit.api.v1.controller;

import com.knitwit.model.UserNotification;
import com.knitwit.service.UserNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user-notifications")
public class UserNotificationController {

    @Autowired
    private UserNotificationService userNotificationService;

    @Operation(summary = "добавления уведомления пользователю")
    @PostMapping("/save")
    public ResponseEntity<UserNotification> saveUserNotification(@RequestBody UserNotification userNotification) {
        UserNotification savedNotification = userNotificationService.saveUserNotification(userNotification);
        return ResponseEntity.ok(savedNotification);
    }

    @Operation(summary = "Отметка уведомления как прочитанного")
    @PutMapping("/{userNotificationId}/mark-as-read")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable int userNotificationId) {
        userNotificationService.markNotificationAsRead(userNotificationId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получение всех уведомлений пользователя")
    @GetMapping("/all")
    public ResponseEntity<List<UserNotification>> getAllUserNotifications() {
        List<UserNotification> userNotifications = userNotificationService.getAllUserNotifications();
        return ResponseEntity.ok(userNotifications);
    }

    @Operation(summary = "Получение всех непрочитанных уведомлений пользователя")
    @GetMapping("/unread")
    public ResponseEntity<List<UserNotification>> getAllUnreadUserNotifications() {
        List<UserNotification> unreadNotifications = userNotificationService.getAllUnreadUserNotifications();
        return ResponseEntity.ok(unreadNotifications);
    }

    @Operation(summary = "Получение количества непрочитанных уведомлений пользователя")
    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadUserNotificationCount() {
        long count = userNotificationService.getUnreadUserNotificationCount();
        return ResponseEntity.ok(count);
    }
}

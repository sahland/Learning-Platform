package com.knitwit.service;

import com.knitwit.model.Notification;
import com.knitwit.repository.NotificationRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Сервис для работы с уведомлениями")
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public Notification createNotification(Notification notification) {
        notification.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public Notification getNotificationById(int notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Уведомление не найдено с ID: " + notificationId));
    }

    @Transactional
    public void deleteNotificationById(int notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}

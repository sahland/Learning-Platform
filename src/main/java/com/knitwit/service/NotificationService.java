package com.knitwit.service;

import com.knitwit.model.Notification;
import com.knitwit.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // Метод для сохранения уведомления
    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    // Метод для получения всех уведомлений
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    // Метод для получения уведомления по его ID
    public Notification getNotificationById(int notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with ID: " + notificationId));
    }

    // Метод для удаления уведомления по его ID
    public void deleteNotificationById(int notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}

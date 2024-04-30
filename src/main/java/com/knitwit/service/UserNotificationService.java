package com.knitwit.service;

import com.knitwit.model.UserNotification;
import com.knitwit.repository.UserNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserNotificationService {

    private final UserNotificationRepository userNotificationRepository;

    @Autowired
    public UserNotificationService(UserNotificationRepository userNotificationRepository) {
        this.userNotificationRepository = userNotificationRepository;
    }

    // Метод для сохранения уведомления пользователя
    public UserNotification saveUserNotification(UserNotification userNotification) {
        return userNotificationRepository.save(userNotification);
    }

    // Метод для отметки уведомления как прочитанного
    public void markNotificationAsRead(int userNotificationId) {
        Optional<UserNotification> optionalUserNotification = userNotificationRepository.findById(userNotificationId);
        if (optionalUserNotification.isPresent()) {
            UserNotification userNotification = optionalUserNotification.get();
            userNotification.setRead(true);
            userNotificationRepository.save(userNotification);
        } else {
            throw new IllegalArgumentException("User Notification not found with ID: " + userNotificationId);
        }
    }

    // Метод для получения всех уведомлений пользователя
    public List<UserNotification> getAllUserNotifications() {
        return userNotificationRepository.findAll();
    }

    // Метод для получения непрочитанных уведомлений пользователя
    public List<UserNotification> getAllUnreadUserNotifications() {
        return userNotificationRepository.findByIsReadFalse();
    }

    // Метод для получения количества непрочитанных уведомлений пользователя
    public long getUnreadUserNotificationCount() {
        return userNotificationRepository.countByIsReadFalse();
    }
}

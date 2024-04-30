package com.knitwit.repository;

import com.knitwit.model.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Integer> {

    // Метод для получения списка непрочитанных уведомлений пользователя
    List<UserNotification> findByIsReadFalse();

    // Метод для получения количества непрочитанных уведомлений пользователя
    int countByIsReadFalse();
}

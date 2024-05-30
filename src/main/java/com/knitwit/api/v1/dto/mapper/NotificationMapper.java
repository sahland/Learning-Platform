package com.knitwit.api.v1.dto.mapper;

import com.knitwit.api.v1.dto.request.NotificationRequest;
import com.knitwit.api.v1.dto.response.NotificationResponse;
import com.knitwit.model.Notification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationMapper {

    public Notification toEntity(NotificationRequest request) {
        Notification notification = new Notification();
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        return notification;
    }

    public NotificationResponse toResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setNotificationId(notification.getNotificationId());
        response.setSenderId(notification.getSenderId());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setCreatedAt(notification.getCreatedAt());
        return response;
    }

    public List<NotificationResponse> toResponseList(List<Notification> notifications) {
        return notifications.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}

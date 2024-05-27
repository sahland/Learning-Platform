package com.knitwit.api.v1.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private int notificationId;
    private int senderId;
    private String title;
    private String message;
    private LocalDateTime createdAt;
}

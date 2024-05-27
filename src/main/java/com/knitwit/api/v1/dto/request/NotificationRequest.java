package com.knitwit.api.v1.dto.request;

import lombok.Data;

@Data
public class NotificationRequest {
    private String title;
    private String message;
}

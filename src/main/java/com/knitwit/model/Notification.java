package com.knitwit.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Schema(description = "Сущность уведомления")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification")
public class Notification {
    @Schema(description = "ID уведомления", example = "2")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private int notificationId;

    @Schema(description = "ID создателя уведомления", example = "1")
    @Column(name = "sender_id")
    private int senderId;

    @Schema(description = "Название уведомления", example = "Обновление 1.1")
    @Column(name = "title")
    private String title;

    @Schema(description = "Содержимое уведомления", example = "Сегодня наше приложение обновилосб до версии 1.1")
    @Column(name = "message")
    private String message;

    @Schema(description = "Дата и время уведомления", example = "2024-05-14 18:38:07")
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}


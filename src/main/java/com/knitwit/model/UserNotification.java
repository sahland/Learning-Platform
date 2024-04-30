package com.knitwit.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user_notification")
public class UserNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_notification_id")
    private int userNotificationId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "notification_id", referencedColumnName = "notification_id")
    private Notification notification;

    @Column(name = "is_read")
    private boolean isRead;
}

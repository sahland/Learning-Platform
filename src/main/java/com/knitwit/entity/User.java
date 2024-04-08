package com.knitwit.entity;

import com.knitwit.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "avatar_file_id")
    private Long avatarFileId;

    @Column(name = "username")
    private String username;

    @Column(name = "email", unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private UserRole userRole;
}

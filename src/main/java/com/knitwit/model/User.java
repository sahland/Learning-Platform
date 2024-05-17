package com.knitwit.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Set;

@Schema(description = "Сущность пользователя")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Schema(description = "ID пользователя", example = "35")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Schema(description = "Nickname пользователя", example = "Vasya2007")
    @Column(name = "nickname")
    private String nickname;

    @Schema(description = "Ключ аватара пользователя")
    @Column(name = "user_avatar_key")
    private String userAvatarKey;

    @Schema(description = "Курсы на которые подписан пользователь")
    @ManyToMany
    @JsonBackReference("user-subscriptions")
    @JoinTable(
            name = "course_subscription",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();

    @Schema(description = "Секции которые пользователь отметил пройденными")
    @OneToMany(mappedBy = "user")
    @JsonBackReference("user-learningProgresses")
    private Set<LearningProgress> learningProgresses = new HashSet<>();

    @Schema(description = "Оценки которые пользователь поставил курсам")
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference("user-ratings")
    private Set<CourseRating> ratings = new HashSet<>();
}

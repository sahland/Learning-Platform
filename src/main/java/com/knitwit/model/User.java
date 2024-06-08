package com.knitwit.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Schema(description = "Сущность пользователя")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@EqualsAndHashCode(exclude = {"courses", "learningProgresses", "ratings"})
public class User implements UserDetails {
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

    @Schema(description = "Логин пользователя", example = "Vasya2007")
    @Column(name = "username")
    private String username;

    @Schema(description = "Электронная почта пользователя", example = "vasya@example.com")
    @Column(name = "email")
    private String email;

    @Schema(description = "Пароль пользователя", example = "password123")
    @Column(name = "password")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles;

    @Schema(description = "Курсы, на которые подписан пользователь")
    @ManyToMany
    @JsonBackReference("user-subscriptions")
    @JoinTable(
            name = "course_subscription",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();

    @Schema(description = "Секции, которые пользователь отметил пройденными")
    @OneToMany(mappedBy = "user")
    @JsonBackReference("user-learningProgresses")
    private Set<LearningProgress> learningProgresses = new HashSet<>();

    @Schema(description = "Оценки, которые пользователь поставил курсам")
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference("user-ratings")
    private Set<CourseRating> ratings = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

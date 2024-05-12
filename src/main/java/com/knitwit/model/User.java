package com.knitwit.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(name = "avatar_file_id")
    private int avatarFileId;

    @ManyToMany
    @JsonBackReference
    @JoinTable(
            name = "course_subscription",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )

    private Set<Course> courses = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<LearningProgress> learningProgresses = new HashSet<>();
}

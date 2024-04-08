package com.knitwit.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "favorite_course")
public class FavoriteCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private Course course;
}
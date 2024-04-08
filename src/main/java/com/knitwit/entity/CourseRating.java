package com.knitwit.entity;

import com.knitwit.enums.RatingValue;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "course_rating")
public class CourseRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(name = "value")
    private RatingValue value;
}
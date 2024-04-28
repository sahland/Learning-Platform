package com.knitwit.model;

import com.knitwit.enums.RatingValue;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "course_rating")
public class CourseRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private int ratingId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "course_id")
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(name = "value")
    private RatingValue value;
}

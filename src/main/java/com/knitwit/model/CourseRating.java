package com.knitwit.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    @JsonIgnoreProperties({"avatarFileId", "learningProgresses"})
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @ManyToOne
    @JsonIgnoreProperties({"title", "creator", "sections", "subscribers", "tags", "publishedDate", "status"})
    @JoinColumn(name = "course_id", referencedColumnName = "course_id")
    private Course course;

    @Column(name = "value")
    private int value;
}

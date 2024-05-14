package com.knitwit.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Сущность оценки курса пользователем")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "course_rating")
public class CourseRating {
    @Schema(description = "ID оценки", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private int ratingId;

    @Schema(description = "Пользователь который оценил курс")
    @ManyToOne
    @JsonIgnoreProperties({"avatarFileId", "learningProgresses", "ratings"})
    @JoinColumn(name = "user_id")
    private User user;

    @Schema(description = "Курс который оценил пользователь")
    @ManyToOne
    @JsonIgnoreProperties({"title", "creator", "sections", "subscribers", "tags", "publishedDate", "status"})
    @JoinColumn(name = "course_id")
    private Course course;

    @Schema(description = "Оценка которую поставил пользователь курсу", example = "5")
    @Column(name = "value")
    private int value;
}

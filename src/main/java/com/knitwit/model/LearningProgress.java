package com.knitwit.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Сущность прогресса изучения раздела курса")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "learning_progress")
public class LearningProgress {
    @Schema(description = "ID прогресса изучения раздела курса", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private int progressId;

    @Schema(description = "ID пользователя, который изучил раздел курса", example = "15")
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Schema(description = "ID раздела курса, который изучил пользователь", example = "10")
    @ManyToOne
    @JoinColumn(name = "section_id", referencedColumnName = "section_id")
    private CourseSection section;

    @Schema(description = "Отметка об изучении курса", example = "true")
    @Column(name = "is_completed")
    private boolean completed;
}
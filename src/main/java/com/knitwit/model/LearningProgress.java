package com.knitwit.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "learning_progress")
public class LearningProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private int progressId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "section_id", referencedColumnName = "section_id")
    private CourseSection section;

    @Column(name = "is_completed")
    private boolean completed;
}

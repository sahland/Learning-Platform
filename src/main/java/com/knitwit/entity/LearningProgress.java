package com.knitwit.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "learning_progress")
public class LearningProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "section_id", referencedColumnName = "id")
    private CourseSection section;

    @Column(name = "is_completed")
    private boolean completed;
}

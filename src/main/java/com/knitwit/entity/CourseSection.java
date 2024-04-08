package com.knitwit.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "course_section")
public class CourseSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private Course course;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "section_number")
    private Integer sectionNumber;
}
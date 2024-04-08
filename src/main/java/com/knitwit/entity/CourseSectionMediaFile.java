package com.knitwit.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "course_section_media_file")
public class CourseSectionMediaFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "section_id", referencedColumnName = "id")
    private CourseSection section;

    @ManyToOne
    @JoinColumn(name = "file_id", referencedColumnName = "id")
    private MediaFile file;
}

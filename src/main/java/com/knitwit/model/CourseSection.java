package com.knitwit.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "course_section")
public class    CourseSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_id")
    private int sectionId;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonBackReference
    private Course course;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "section_number")
    private Integer sectionNumber;

    @ManyToMany
    @JoinTable(
            name = "course_section_media_file",
            joinColumns = @JoinColumn(name = "section_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private Set<MediaFile> mediaFiles = new HashSet<>();
}

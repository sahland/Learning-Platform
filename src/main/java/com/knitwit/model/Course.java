package com.knitwit.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.knitwit.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "course")
@EqualsAndHashCode(exclude = "sections")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private int courseId;

    @ManyToOne
    @JoinColumn(name = "creator_id", referencedColumnName = "user_id")
    private User creator;

    @Column(name = "title")
    private String title;

    @Column(name = "published_date")
    private LocalDate publishedDate;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<CourseSection> sections = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "course_tag",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany(mappedBy = "courses")
    private Set<User> subscribers = new HashSet<>();

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CourseStatus status;
}

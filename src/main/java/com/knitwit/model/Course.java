package com.knitwit.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.knitwit.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "course")
@EqualsAndHashCode(exclude = {"sections", "tags", "subscribers"})
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
    private List<CourseSection> sections = new ArrayList<>();

    @OneToOne
    @JsonIgnoreProperties({"fileSize", "createdAt", "fileKey", "fileType"})
    @JoinTable(
            name = "course_avatar",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private MediaFile courseAvatar;


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "course_tag",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany(mappedBy = "courses", cascade = CascadeType.ALL)
    private Set<User> subscribers = new HashSet<>();

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CourseStatus status;
}

package com.knitwit.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.knitwit.enums.CourseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Schema(description = "Сущность курса")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "course")
@EqualsAndHashCode(exclude = {"sections", "tags", "subscribers"})
public class Course {
    @Schema(description = "ID курса", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private int courseId;

    @Schema(description = "Создатель курса")
    @ManyToOne
    @JoinColumn(name = "creator_id", referencedColumnName = "user_id")
    private User creator;

    @Schema(description = "Название курса", example = "Выжигание по дереву")
    @Column(name = "title")
    private String title;

    @Schema(description = "Дата публикации курса", example = "2024-05-14")
    @Column(name = "published_date")
    private LocalDate publishedDate;

    @Schema(description = "Разделы курса")
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    @JsonManagedReference("course-sections")
    private List<CourseSection> sections = new ArrayList<>();

    @Schema(description = "Иконка курса")
    @OneToOne
    @JsonIgnoreProperties({"fileSize", "createdAt", "fileKey", "fileType"})
    @JoinTable(
            name = "course_avatar",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private MediaFile courseAvatar;

    @Schema(description = "Теги курса")
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "course_tag",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @Schema(description = "Подписчики курса")
    @ManyToMany(mappedBy = "courses", cascade = CascadeType.ALL)
    @JsonBackReference("user-subscriptions")
    private Set<User> subscribers = new HashSet<>();

    @Schema(description = "Статус курса", example = "PUBLISHED")
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CourseStatus status;
}
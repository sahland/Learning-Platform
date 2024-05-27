package com.knitwit.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Schema(description = "Сущность тега")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tag")
public class Tag {
    @Schema(description = "ID тега", example = "5")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private int tagId;

    @Schema(description = "Название тега", example = "Спорт")
    @Column(name = "tag_name")
    private String tagName;

    @Schema(description = "Курсы, которые отмечены тегом")
    @ManyToMany(mappedBy = "tags", cascade = CascadeType.ALL)
    @JsonBackReference("course-tags")
    private Set<Course> courses = new HashSet<>();
}

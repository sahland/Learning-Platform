package com.knitwit.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "Сущность раздела курса")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "course_section")
public class CourseSection {
    @Schema(description = "ID раздела курса", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_id")
    private int sectionId;

    @Schema(description = "ID курса, к которому принадлежит раздел", example = "1")
    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonBackReference("course-sections")
    private Course course;

    @Schema(description = "Содержимое раздела", example = "Пирография — техника, применяемая в декоративно-прикладном искусстве и художественной графике.")
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Schema(description = "Номер секции курса", example = "1")
    @Column(name = "section_number")
    private Integer sectionNumber;
}

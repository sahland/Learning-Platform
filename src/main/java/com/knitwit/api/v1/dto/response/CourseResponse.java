package com.knitwit.api.v1.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CourseResponse {
    private int courseId;
    private UserResponse creator;
    private String title;
    private LocalDateTime publishedDate;
    private String courseAvatarKey;
    private List<CourseSectionResponse> sections;
    private List<TagResponse> tags;
    private String status;

    public void setSections(List<CourseSectionResponse> sections) {
        this.sections = sections.stream()
                .sorted(Comparator.comparingInt(CourseSectionResponse::getSectionNumber))
                .collect(Collectors.toList());
    }
}
package com.knitwit.api.v1.dto.request;

import lombok.Data;

@Data
public class CourseSectionRequest {
    private int courseId;
    private String title;
    private String content;
    private Integer sectionNumber;
}

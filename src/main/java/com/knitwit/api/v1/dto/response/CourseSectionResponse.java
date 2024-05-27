package com.knitwit.api.v1.dto.response;

import lombok.Data;

@Data
public class CourseSectionResponse {
    private int sectionId;
    private int courseId;
    private String title;
    private String content;
    private Integer sectionNumber;
}

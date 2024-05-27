package com.knitwit.api.v1.dto.request;

import com.knitwit.model.Course;
import com.knitwit.model.CourseSection;
import com.knitwit.model.Tag;
import lombok.Data;

import java.util.List;

@Data
public class CourseWithSectionsAndTagsRequest {
    private Course course;
    private List<CourseSection> sections;
    private List<Tag> tags;
}

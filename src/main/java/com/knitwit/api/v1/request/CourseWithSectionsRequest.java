package com.knitwit.api.v1.request;

import com.knitwit.model.Course;
import com.knitwit.model.CourseSection;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class CourseWithSectionsRequest {
    private Course course;
    private List<CourseSection> sections;
}

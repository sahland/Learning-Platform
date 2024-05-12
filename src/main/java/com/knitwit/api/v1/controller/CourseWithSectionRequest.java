package com.knitwit.api.v1.controller;

import com.knitwit.model.Course;
import com.knitwit.model.CourseSection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseWithSectionRequest {
    private Course course;
    private CourseSection section;
}

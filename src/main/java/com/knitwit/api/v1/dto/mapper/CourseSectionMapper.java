package com.knitwit.api.v1.dto.mapper;

import com.knitwit.api.v1.dto.request.CourseSectionRequest;
import com.knitwit.api.v1.dto.response.CourseSectionResponse;
import com.knitwit.model.CourseSection;
import com.knitwit.model.Course;
import com.knitwit.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CourseSectionMapper {

    private final CourseRepository courseRepository;

    public CourseSection toEntity(CourseSectionRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Курс не найден по ID: " + request.getCourseId()));

        CourseSection section = new CourseSection();
        section.setCourse(course);
        section.setTitle(request.getTitle());
        section.setContent(request.getContent());
        section.setSectionNumber(request.getSectionNumber());
        return section;
    }

    public CourseSectionResponse toResponse(CourseSection section) {
        CourseSectionResponse response = new CourseSectionResponse();
        response.setSectionId(section.getSectionId());
        response.setCourseId(section.getCourse().getCourseId());
        response.setTitle(section.getTitle());
        response.setContent(section.getContent());
        response.setSectionNumber(section.getSectionNumber());
        return response;
    }

    public List<CourseSectionResponse> toResponseList(Set<CourseSection> sections) {
        return sections.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}

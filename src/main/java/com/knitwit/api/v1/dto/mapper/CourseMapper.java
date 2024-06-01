package com.knitwit.api.v1.dto.mapper;

import com.knitwit.api.v1.dto.response.CourseResponse;
import com.knitwit.model.Course;
import com.knitwit.model.CourseSection;
import com.knitwit.model.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CourseMapper {

    private final CourseSectionMapper courseSectionMapper;
    private final TagMapper tagMapper;
    private final UserMapper userMapper;

    public CourseResponse toResponse(Course course) {
        CourseResponse response = new CourseResponse();
        response.setCourseId(course.getCourseId());
        response.setCreator(userMapper.toResponse(course.getCreator()));
        response.setTitle(course.getTitle());
        response.setPublishedDate(course.getPublishedDate().atStartOfDay());
        response.setCourseAvatarKey(course.getCourseAvatarKey());
        Set<CourseSection> sections = new HashSet<>(course.getSections());
        Set<Tag> tags = new HashSet<>(course.getTags());
        response.setSections(courseSectionMapper.toResponseList(sections));
        response.setTags(tagMapper.toResponseList(tags));
        response.setStatus(String.valueOf(course.getStatus()));

        return response;
    }
}
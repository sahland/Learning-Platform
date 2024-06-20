package com.knitwit.service;

import com.knitwit.model.CourseSection;
import com.knitwit.repository.CourseSectionRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Schema(description = "Сервис для работы с разделами курсов")
@Service
@RequiredArgsConstructor
public class CourseSectionService {

    private final CourseSectionRepository courseSectionRepository;

    public CourseSection getSectionById(int courseId, int sectionId) {
        return courseSectionRepository.findByCourseCourseIdAndSectionId(courseId, sectionId).orElse(null);
    }
    public Optional<CourseSection> findById(int sectionId) {
        return courseSectionRepository.findById(sectionId);
    }
}

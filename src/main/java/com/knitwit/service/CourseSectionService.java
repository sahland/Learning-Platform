package com.knitwit.service;

import com.knitwit.api.v1.dto.request.CourseSectionRequest;
import com.knitwit.model.CourseSection;
import com.knitwit.repository.CourseSectionRepository;
import com.knitwit.repository.CourseRepository;
import com.knitwit.mapper.CourseSectionMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Schema(description = "Сервис для работы с разделами курсов")
@Service
@RequiredArgsConstructor
public class CourseSectionService {

    private final CourseSectionRepository courseSectionRepository;
    private final CourseRepository courseRepository;
    private final CourseSectionMapper courseSectionMapper;

    public CourseSection getSectionById(int sectionId) {
        return courseSectionRepository.findById(sectionId).orElse(null);
    }

    public Optional<CourseSection> findById(int sectionId) {
        return courseSectionRepository.findById(sectionId);
    }

    public CourseSection createSection(CourseSectionRequest request) {
        CourseSection section = courseSectionMapper.toEntity(request);
        return courseSectionRepository.save(section);
    }
}

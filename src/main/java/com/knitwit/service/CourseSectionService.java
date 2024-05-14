package com.knitwit.service;

import com.knitwit.model.CourseSection;
import com.knitwit.model.MediaFile;
import com.knitwit.repository.CourseSectionRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Schema(description = "Сервис для работы с разделами курсов")
@Service
public class CourseSectionService {

    @Autowired
    private CourseSectionRepository courseSectionRepository;

    public CourseSection getSectionById(int sectionId) {
        return courseSectionRepository.findById(sectionId).orElse(null);
    }

    public Optional<CourseSection> findById(int sectionId) {
        return courseSectionRepository.findById(sectionId);
    }

    public void addMediaFilesToCourseSection(int sectionId, List<MediaFile> mediaFiles) {
        CourseSection section = courseSectionRepository.findById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("Секция курса с указанным ID не найдена: " + sectionId));
        section.getMediaFiles().addAll(mediaFiles);
        courseSectionRepository.save(section);
    }
}

package com.knitwit.api.v1.dto.mapper;

import com.knitwit.api.v1.dto.request.LearningProgressRequest;
import com.knitwit.api.v1.dto.response.LearningProgressResponse;
import com.knitwit.model.LearningProgress;
import com.knitwit.model.CourseSection;
import com.knitwit.service.CourseSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LearningProgressMapper {

    private final CourseSectionService courseSectionService;

    public LearningProgress toEntity(LearningProgressRequest request) {
        CourseSection section = courseSectionService.findById(request.getSectionId())
                .orElseThrow(() -> new IllegalArgumentException("Раздел курса не найден по ID: " + request.getSectionId()));

        LearningProgress progress = new LearningProgress();
        progress.setSection(section);
        progress.setCompleted(request.isCompleted());
        return progress;
    }

    public LearningProgressResponse toResponse(LearningProgress progress) {
        LearningProgressResponse response = new LearningProgressResponse();
        response.setProgressId(progress.getProgressId());
        response.setUserId(progress.getUser().getUserId());
        response.setSectionId(progress.getSection().getSectionId());
        response.setCompleted(progress.isCompleted());
        return response;
    }
}

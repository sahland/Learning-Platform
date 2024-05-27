package com.knitwit.service;

import com.knitwit.api.v1.dto.request.LearningProgressRequest;
import com.knitwit.model.LearningProgress;
import com.knitwit.model.User;
import com.knitwit.repository.LearningProgressRepository;
import com.knitwit.repository.CourseSectionRepository;
import com.knitwit.mapper.LearningProgressMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Schema(description = "Сервис для работы с прогрессом прохождения пользователем курсов")
@Service
@RequiredArgsConstructor
public class LearningProgressService {

    private final LearningProgressRepository learningProgressRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final LearningProgressMapper learningProgressMapper;

    @Transactional
    public LearningProgress markSectionAsCompleted(User user, LearningProgressRequest request) {
        return saveLearningProgress(user, request, true);
    }

    @Transactional
    public LearningProgress markSectionAsIncomplete(User user, LearningProgressRequest request) {
        return saveLearningProgress(user, request, false);
    }

    private LearningProgress saveLearningProgress(User user, LearningProgressRequest request, boolean completed) {
        Optional<LearningProgress> optionalLearningProgress = learningProgressRepository.findByUserUserIdAndSectionSectionId(user.getUserId(), request.getSectionId());
        LearningProgress learningProgress;
        if (optionalLearningProgress.isPresent()) {
            learningProgress = optionalLearningProgress.get();
            learningProgress.setCompleted(completed);
        } else {
            learningProgress = learningProgressMapper.toEntity(request);
            learningProgress.setUser(user);
            learningProgress.setCompleted(completed);
        }
        return learningProgressRepository.save(learningProgress);
    }

    public int getCompletionPercentageForUserAndCourse(int userId, int courseId) {
        long totalSections = courseSectionRepository.countByCourseCourseId(courseId);
        long completedSections = learningProgressRepository.countByUserUserIdAndSectionCourseCourseIdAndCompletedTrue(userId, courseId);

        if (totalSections == 0) {
            return 0;
        }
        return (int) Math.round(((double) completedSections / totalSections) * 100);
    }
}

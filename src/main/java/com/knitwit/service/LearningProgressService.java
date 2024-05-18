package com.knitwit.service;

import com.knitwit.model.CourseSection;
import com.knitwit.model.LearningProgress;
import com.knitwit.model.User;
import com.knitwit.repository.CourseSectionRepository;
import com.knitwit.repository.LearningProgressRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Schema(description = "Сервис для работы с прогрессм=ом прохождения пользователем курсов")
@Service
public class LearningProgressService {

    private final LearningProgressRepository learningProgressRepository;
    private final UserService userService;
    private final CourseSectionService courseSectionService;
    private final CourseSectionRepository courseSectionRepository;

    public LearningProgressService(LearningProgressRepository learningProgressRepository, UserService userService,
                                   CourseSectionService courseSectionService,
                                   CourseSectionRepository courseSectionRepository) {
        this.learningProgressRepository = learningProgressRepository;
        this.userService = userService;
        this.courseSectionService = courseSectionService;
        this.courseSectionRepository = courseSectionRepository;
    }

    @Transactional
    public void markSectionAsCompleted(int userId, int sectionId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден по ID: " + userId));
        CourseSection section = courseSectionService.findById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("Раздел курса не найден по ID: " + sectionId));

        Optional<LearningProgress> optionalLearningProgress = learningProgressRepository.findByUserUserIdAndSectionSectionId(userId, sectionId);
        if (optionalLearningProgress.isPresent()) {
            LearningProgress learningProgress = optionalLearningProgress.get();
            learningProgress.setCompleted(true);
            learningProgressRepository.save(learningProgress);
        } else {
            LearningProgress newLearningProgress = new LearningProgress();
            newLearningProgress.setUser(user);
            newLearningProgress.setSection(section);
            newLearningProgress.setCompleted(true);
            learningProgressRepository.save(newLearningProgress);
        }
    }

    @Transactional
    public void markSectionAsIncomplete(int userId, int sectionId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден по ID: " + userId));
        CourseSection section = courseSectionService.findById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("Раздел курса не найден по ID: " + sectionId));

        Optional<LearningProgress> optionalLearningProgress = learningProgressRepository.findByUserUserIdAndSectionSectionId(userId, sectionId);
        if (optionalLearningProgress.isPresent()) {
            LearningProgress learningProgress = optionalLearningProgress.get();
            learningProgress.setCompleted(false);
            learningProgressRepository.save(learningProgress);
        } else {
            LearningProgress newLearningProgress = new LearningProgress();
            newLearningProgress.setUser(user);
            newLearningProgress.setSection(section);
            newLearningProgress.setCompleted(false);
            learningProgressRepository.save(newLearningProgress);
        }
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


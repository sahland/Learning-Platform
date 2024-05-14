package com.knitwit.service;

import com.knitwit.model.CourseSection;
import com.knitwit.model.LearningProgress;
import com.knitwit.model.User;
import com.knitwit.repository.CourseSectionRepository;
import com.knitwit.repository.LearningProgressRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Schema(description = "Сервис для работы с прогрессм=ом прохождения пользователем курсов")
@Service
public class LearningProgressService {

    @Autowired
    private LearningProgressRepository learningProgressRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CourseSectionService courseSectionService;
    @Autowired
    CourseSectionRepository courseSectionRepository;

    public void markSectionAsCompleted(int userId, int sectionId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        CourseSection section = courseSectionService.findById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("Course section not found with id: " + sectionId));

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

    public void markSectionAsIncomplete(int userId, int sectionId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        CourseSection section = courseSectionService.findById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("Course section not found with id: " + sectionId));

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


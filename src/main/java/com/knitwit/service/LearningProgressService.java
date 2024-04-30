package com.knitwit.service;

import com.knitwit.model.LearningProgress;
import com.knitwit.repository.LearningProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LearningProgressService {

    private final LearningProgressRepository learningProgressRepository;

    @Autowired
    public LearningProgressService(LearningProgressRepository learningProgressRepository) {
        this.learningProgressRepository = learningProgressRepository;
    }

    // Получение списка записей о прогрессе для конкретного пользователя и курса
    public List<LearningProgress> getUserLearningProgressByUserIdAndCourseId(int userId, int courseId) {
        return learningProgressRepository.findByUserUserIdAndSectionCourseCourseId(userId, courseId);
    }

    // Сохранение или обновление записи о прогрессе
    public LearningProgress saveLearningProgress(LearningProgress learningProgress) {
        return learningProgressRepository.save(learningProgress);
    }
}

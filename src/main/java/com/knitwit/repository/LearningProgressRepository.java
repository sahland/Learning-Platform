package com.knitwit.repository;

import com.knitwit.model.Course;
import com.knitwit.model.LearningProgress;
import com.knitwit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LearningProgressRepository extends JpaRepository<LearningProgress, Long> {
    // Получение списка записей о прогрессе для конкретного пользователя и курса
    List<LearningProgress> findByUserUserIdAndSectionCourseCourseId(int userId, int courseId);
}
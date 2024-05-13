package com.knitwit.repository;

import com.knitwit.model.LearningProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningProgressRepository extends JpaRepository<LearningProgress, Integer> {
    List<LearningProgress> findByUserUserId(int userId);
    List<LearningProgress> findBySectionCourseCourseId(int courseId);
    Optional<LearningProgress> findByUserUserIdAndSectionSectionId(int userId, int sectionId);
    long countByUserUserIdAndSectionCourseCourseId(int userId, int courseId);
    long countByUserUserIdAndSectionCourseCourseIdAndCompletedTrue(int userId, int courseId);
}
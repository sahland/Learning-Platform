package com.knitwit.repository;

import com.knitwit.model.LearningProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LearningProgressRepository extends JpaRepository<LearningProgress, Integer> {
    Optional<LearningProgress> findByUserUserIdAndSectionSectionId(int userId, int sectionId);
    long countByUserUserIdAndSectionCourseCourseIdAndCompletedTrue(int userId, int courseId);
}
package com.knitwit.repository;

import com.knitwit.model.CourseRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRatingRepository extends JpaRepository<CourseRating, Integer> {
    CourseRating findCourseRatingByCourseCourseIdAndUserUserId(int courseId, int userId);
    List<CourseRating> findByUserUserId(int userId);
    List<CourseRating> findByCourseCourseId(int courseId);
}
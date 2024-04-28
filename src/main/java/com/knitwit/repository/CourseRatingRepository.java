package com.knitwit.repository;

import com.knitwit.enums.RatingValue;
import com.knitwit.model.Course;
import com.knitwit.model.CourseRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRatingRepository extends JpaRepository<CourseRating, Integer> {

    // Получение количества всех оценок у курса
    int countByCourse(Course сourse);

    // Получение количества определенных оценок у курса
    int countByCourseAndValue(Course сourse, RatingValue value);
}
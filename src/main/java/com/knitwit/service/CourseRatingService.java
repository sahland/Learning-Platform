package com.knitwit.service;

import com.knitwit.enums.RatingValue;
import com.knitwit.model.Course;
import com.knitwit.model.CourseRating;
import com.knitwit.repository.CourseRatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CourseRatingService {

    private final CourseRatingRepository courseRatingRepository;

    @Autowired
    public CourseRatingService(CourseRatingRepository courseRatingRepository) {
        this.courseRatingRepository = courseRatingRepository;
    }

    // Создание рейтинга курса
    @Transactional
    public CourseRating createCourseRating(CourseRating courseRating) {
        return courseRatingRepository.save(courseRating);
    }

    // Удаление рейтинга курса
    @Transactional
    public void deleteCourseRating(int ratingId) {
        courseRatingRepository.deleteById(ratingId);
    }

    // Редактирование рейтинга курса
    @Transactional
    public CourseRating updateCourseRating(int ratingId, CourseRating updatedCourseRating) {
        Optional<CourseRating> optionalCourseRating = courseRatingRepository.findById(ratingId);
        if (optionalCourseRating.isPresent()) {
            updatedCourseRating.setRatingId(ratingId);
            return courseRatingRepository.save(updatedCourseRating);
        } else {
            throw new IllegalArgumentException("Course rating not found with id: " + ratingId);
        }
    }

    // Получение рейтинга курса по его идентификатору
    public CourseRating getCourseRatingById(int ratingId) {
        return courseRatingRepository.findById(ratingId)
                .orElseThrow(() -> new IllegalArgumentException("Course rating not found with id: " + ratingId));
    }

    // Получение количества всех оценок у курса
    public int countByCourse(int courseId) {
        Course course = new Course();
        course.setCourseId(courseId);
        return courseRatingRepository.countByCourse(course);
    }

    // Получение количества определенных оценок у курса
    public int countByCourseAndValue(int courseId, RatingValue value) {
        Course course = new Course();
        course.setCourseId(courseId);
        return courseRatingRepository.countByCourseAndValue(course, value);
    }
}

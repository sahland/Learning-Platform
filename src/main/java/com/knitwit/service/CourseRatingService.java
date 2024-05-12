package com.knitwit.service;

import com.knitwit.model.CourseRating;
import com.knitwit.repository.CourseRatingRepository;
import com.knitwit.repository.CourseRepository;
import com.knitwit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseRatingService {
    @Autowired
    private CourseRatingRepository courseRatingRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    UserRepository userRepository;

    @Transactional
    public CourseRating rateCourse(int courseId, int userId, int value) {
        if (value < 1 || value > 5) {
            throw new IllegalArgumentException("Rating value must be between 1 and 5.");
        }
        CourseRating existingRating = courseRatingRepository.findCourseRatingByCourseCourseIdAndUserUserId(courseId, userId);
        if (existingRating != null) {
            existingRating.setValue(value);
            return courseRatingRepository.save(existingRating);
        } else {
            CourseRating newRating = new CourseRating();
            newRating.setCourse(courseRepository.getById(courseId));
            newRating.setUser(userRepository.getById(userId));
            newRating.setValue(value);
            return courseRatingRepository.save(newRating);
        }
    }

    @Transactional
    public boolean deleteRating(int courseId, int userId) {
        CourseRating rating = courseRatingRepository.findCourseRatingByCourseCourseIdAndUserUserId(courseId, userId);
        if (rating != null) {
            courseRatingRepository.delete(rating);
            return true;
        }
        return false;
    }

    @Transactional
    public List<CourseRating> getUserRatings(int userId) {
        return courseRatingRepository.findByUserUserId(userId);
    }

    @Transactional
    public List<CourseRating> getCourseRatings(int courseId) {
        return courseRatingRepository.findByCourseCourseId(courseId);
    }

    @Transactional
    public Double getAverageRating(int courseId) {
        List<CourseRating> ratings = courseRatingRepository.findByCourseCourseId(courseId);
        if (ratings.isEmpty()) {
            return null;
        }
        double sum = ratings.stream().mapToDouble(CourseRating::getValue).sum();
        double average = sum / ratings.size();
        return Math.round(average * 10.0) / 10.0;
    }
}

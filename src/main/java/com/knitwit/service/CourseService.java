package com.knitwit.service;

import com.knitwit.entity.Course;
import com.knitwit.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public Course createCourse(Course course) {
        if (course.getId() != null) {
            throw new IllegalArgumentException("New course should not have an id");
        }
        return courseRepository.save(course);
    }

    public Optional<Course> updateCourse(Long id, Course updatedCourse) {
        return courseRepository.findById(id)
                .map(existingCourse -> {
                    updatedCourse.setId(id);
                    return courseRepository.save(updatedCourse);
                });
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }
}

package com.knitwit.repository;

import com.knitwit.model.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, Integer> {
    void deleteAllByCourseCourseId(int courseId);
    int countByCourseCourseId(int courseId);
}
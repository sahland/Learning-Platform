package com.knitwit.repository;

import com.knitwit.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    List<Course> findByTitleContaining(String keyword);

    List<Course> findByCreatorUserId(int userId);

    List<Course> findAllByCourseIdIn(List<Integer> courseIds);
}

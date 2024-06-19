package com.knitwit.repository;

import com.knitwit.enums.CourseStatus;
import com.knitwit.model.Course;
import com.knitwit.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    @Query("SELECT c FROM Course c WHERE c.title LIKE %:keyword% AND c.status = 'PUBLISHED'")
    List<Course> findByTitleContainingAndStatusPublished(String keyword);

    @Query("SELECT c FROM Course c WHERE c.creator = :creator AND c.status = 'PUBLISHED'")
    List<Course> findByCreatorAndStatusPublished(User creator);

    @Query("SELECT c FROM Course c JOIN c.tags t WHERE t.tagId = :tagId AND c.status = 'PUBLISHED'")
    Set<Course> findByTagIdAndStatusPublished(int tagId);

    @Query("SELECT c FROM Course c WHERE c.courseId = :courseId AND c.status = 'PUBLISHED'")
    Optional<Course> findByIdAndStatusPublished(int courseId);

    List<Course> findAllByStatus(CourseStatus status);
    Page<Course> findAllByStatus(CourseStatus status, Pageable pageable);

}
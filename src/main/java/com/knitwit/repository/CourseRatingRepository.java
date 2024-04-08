package com.knitwit.repository;

import com.knitwit.entity.CourseRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRatingRepository extends JpaRepository<CourseRating, Long> {
}
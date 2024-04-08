package com.knitwit.repository;

import com.knitwit.entity.FavoriteCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteCourseRepository extends JpaRepository<FavoriteCourse, Long> {
}
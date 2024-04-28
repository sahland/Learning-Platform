package com.knitwit.repository;

import com.knitwit.model.CourseRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRequestRepository extends JpaRepository<CourseRequest, Integer> {
}

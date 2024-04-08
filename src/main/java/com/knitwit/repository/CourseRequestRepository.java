package com.knitwit.repository;

import com.knitwit.entity.CourseRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRequestRepository extends JpaRepository<CourseRequest, Long> {
}

package com.knitwit.repository;

import com.knitwit.entity.LearningProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningProgressRepository extends JpaRepository<LearningProgress, Long> {
}

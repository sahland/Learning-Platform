package com.knitwit.repository;

import com.knitwit.entity.CourseSectionMediaFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseSectionMediaFileRepository extends JpaRepository<CourseSectionMediaFile, Long> {

}

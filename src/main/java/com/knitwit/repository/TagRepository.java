package com.knitwit.repository;

import com.knitwit.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
    Tag findByTagName(String tagName);
}
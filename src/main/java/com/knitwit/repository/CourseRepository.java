package com.knitwit.repository;

import com.knitwit.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    // Поиск курса по заголовку, с учетом пагинации
    Page<Course> findByTitleContaining(String keyword, Pageable pageable);

    Page<Course> findByCreatorUserId(int userId, Pageable pageable);

    // Получение списка курсов по списку идентификаторов с пагинацией
    default Page<Course> findAllByIdWithPagination(List<Integer> courseIds, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = (start + pageable.getPageSize()) > courseIds.size() ? courseIds.size() : (start + pageable.getPageSize());
        List<Integer> subList = courseIds.subList(start, end);
        List<Course> courses = findAllById(subList);
        return new PageImpl<>(courses, pageable, courseIds.size());
    }
}

package com.knitwit.repository;

import com.knitwit.enums.RequestType;
import com.knitwit.model.CourseRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRequestRepository extends JpaRepository<CourseRequest, Integer> {

    // Получение списка заявок определенного типа (удаление, публикация или редактирование) с пагинацией
    Page<CourseRequest> findByRequestType(RequestType requestType, Pageable pageable);

    // Получение всех заявок
    Page<CourseRequest> findAll(Pageable pageable);
}

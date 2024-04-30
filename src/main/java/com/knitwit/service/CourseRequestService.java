package com.knitwit.service;

import com.knitwit.enums.RequestType;
import com.knitwit.enums.Status;
import com.knitwit.model.CourseRequest;
import com.knitwit.repository.CourseRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CourseRequestService {

    private final CourseRequestRepository courseRequestRepository;

    @Autowired
    public CourseRequestService(CourseRequestRepository courseRequestRepository) {
        this.courseRequestRepository = courseRequestRepository;
    }

    // Создание заявки на публикацию курса
    @Transactional
    public CourseRequest createCoursePublicationRequest(CourseRequest courseRequest) {
        return courseRequestRepository.save(courseRequest);
    }

    // Создание заявки на редактирование курса
    @Transactional
    public CourseRequest createCourseEditRequest(CourseRequest courseRequest) {
        return courseRequestRepository.save(courseRequest);
    }

    // Создание заявки на удаление курса
    @Transactional
    public CourseRequest createCourseDeletionRequest(CourseRequest courseRequest) {
        return courseRequestRepository.save(courseRequest);
    }

    // Подтверждение заявки на курс
    @Transactional
    public void approveCourseRequest(int requestId) {
        CourseRequest courseRequest = courseRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Course request not found with id: " + requestId));
        courseRequest.setStatus(Status.CONFIRMED);
        courseRequestRepository.save(courseRequest);
    }

    // Получение списка всех заявок
    public Page<CourseRequest> getAllRequests(Pageable pageable) {
        return courseRequestRepository.findAll(pageable);
    }

    // Получение списка заявок определенного типа
    public Page<CourseRequest> getRequestsByType(RequestType requestType, Pageable pageable) {
        return courseRequestRepository.findByRequestType(requestType, pageable);
    }
}

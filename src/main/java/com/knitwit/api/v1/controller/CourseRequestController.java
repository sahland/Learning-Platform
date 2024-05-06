package com.knitwit.api.v1.controller;

import com.knitwit.enums.RequestType;
import com.knitwit.model.CourseRequest;
import com.knitwit.service.CourseRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/requests")
public class CourseRequestController {

    @Autowired
    private CourseRequestService courseRequestService;

    @Operation(summary = "Создание заявки на публикацию курса")
    @PostMapping("/publication")
    public ResponseEntity<CourseRequest> createCoursePublicationRequest(@RequestBody CourseRequest courseRequest) {
        CourseRequest createdRequest = courseRequestService.createCoursePublicationRequest(courseRequest);
        return ResponseEntity.ok(createdRequest);
    }

    @Operation(summary = "Создание заявки на редактирование курса")
    @PostMapping("/edit")
    public ResponseEntity<CourseRequest> createCourseEditRequest(@RequestBody CourseRequest courseRequest) {
        CourseRequest createdRequest = courseRequestService.createCourseEditRequest(courseRequest);
        return ResponseEntity.ok(createdRequest);
    }

    @Operation(summary = "Создание заявки на удаление курса")
    @PostMapping("/deletion")
    public ResponseEntity<CourseRequest> createCourseDeletionRequest(@RequestBody CourseRequest courseRequest) {
        CourseRequest createdRequest = courseRequestService.createCourseDeletionRequest(courseRequest);
        return ResponseEntity.ok(createdRequest);
    }

    @Operation(summary = "Подтверждение заявки на курс")
    @PutMapping("/approve/{requestId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Заявка подтверждена"),
            @ApiResponse(responseCode = "404", description = "Заявка не найдена")
    })
    public ResponseEntity<Void> approveCourseRequest(@PathVariable int requestId) {
        courseRequestService.approveCourseRequest(requestId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получение списка всех заявок")
    @GetMapping
    public ResponseEntity<Page<CourseRequest>> getAllRequests(Pageable pageable) {
        Page<CourseRequest> requests = courseRequestService.getAllRequests(pageable);
        return ResponseEntity.ok(requests);
    }

    @Operation(summary = "Получение списка заявок определенного типа")
    @GetMapping("/type/{requestType}")
    public ResponseEntity<Page<CourseRequest>> getRequestsByType(@PathVariable RequestType requestType, Pageable pageable) {
        Page<CourseRequest> requests = courseRequestService.getRequestsByType(requestType, pageable);
        return ResponseEntity.ok(requests);
    }
}

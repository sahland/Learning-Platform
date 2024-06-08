package com.knitwit.api.v1.controller;

import com.knitwit.api.v1.dto.request.CourseRatingRequest;
import com.knitwit.api.v1.dto.response.CourseRatingResponse;
import com.knitwit.api.v1.dto.mapper.CourseRatingMapper;
import com.knitwit.model.CourseRating;
import com.knitwit.service.AuthService;
import com.knitwit.service.CourseRatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ratings")
public class CourseRatingController {

    private final CourseRatingService courseRatingService;
    private final CourseRatingMapper courseRatingMapper;
    private final AuthService authService;

    @Operation(summary = "Добавить оценку курсу (USER)")
    @Secured("ROLE_USER")
    @PostMapping("/course/{courseId}")
    public ResponseEntity<CourseRatingResponse> rateCourse(
            @PathVariable("courseId") int courseId,
            @RequestBody CourseRatingRequest request) {
        String username = authService.getCurrentUsername();
        int userId = courseRatingService.getUserIdByUsername(username);
        if (request.getValue() < 1 || request.getValue() > 5) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            CourseRating rating = courseRatingService.rateCourse(courseId, userId, request.getValue());
            CourseRatingResponse response = courseRatingMapper.toResponse(rating);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        }
    }

    @Operation(summary = "Удалить оценку курсу (USER)")
    @Secured("ROLE_USER")
    @DeleteMapping("/course/{courseId}")
    public ResponseEntity<Void> deleteRating(
            @PathVariable("courseId") int courseId) {
        String username = authService.getCurrentUsername();
        int userId = courseRatingService.getUserIdByUsername(username);
        boolean deleted = courseRatingService.deleteRating(courseId, userId);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Получить среднюю оценку курса")
    @GetMapping("/course/{courseId}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable("courseId") int courseId) {
        Double averageRating = courseRatingService.getAverageRating(courseId);
        return new ResponseEntity<>(averageRating, HttpStatus.OK);
    }
}

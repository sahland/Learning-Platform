package com.knitwit.api.v1.controller;

import com.knitwit.api.v1.dto.request.CourseRatingRequest;
import com.knitwit.api.v1.dto.response.CourseRatingResponse;
import com.knitwit.api.v1.dto.mapper.CourseRatingMapper;
import com.knitwit.model.CourseRating;
import com.knitwit.service.CourseRatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ratings")
public class CourseRatingController {

    private final CourseRatingService courseRatingService;
    private final CourseRatingMapper courseRatingMapper;

    @Operation(summary = "Добавить оценку курсу")
    //user
    @PostMapping("/course/{courseId}")
    public ResponseEntity<CourseRatingResponse> rateCourse(
            @PathVariable("courseId") int courseId,
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CourseRatingRequest request) {
        String username = jwt.getClaim("preferred_username");
        int userId = courseRatingService.getUserIdByUsername(username);
        if (request.getValue() < 1 || request.getValue() > 5) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            CourseRating rating = courseRatingService.rateCourse(courseId, userId, request.getValue());
            CourseRatingResponse response = courseRatingMapper.toResponse(rating);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Удалить оценку курсу")
    //user
    @DeleteMapping("/course/{courseId}")
    public ResponseEntity<Void> deleteRating(
            @PathVariable("courseId") int courseId,
            @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaim("preferred_username");
        int userId = courseRatingService.getUserIdByUsername(username);
        boolean deleted = courseRatingService.deleteRating(courseId, userId);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Получить среднюю оценку курса")
    //user
    @GetMapping("/course/{courseId}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable("courseId") int courseId) {
        Double averageRating = courseRatingService.getAverageRating(courseId);
        return new ResponseEntity<>(averageRating, HttpStatus.OK);
    }
}

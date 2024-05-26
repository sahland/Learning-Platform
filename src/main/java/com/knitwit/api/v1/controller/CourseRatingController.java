package com.knitwit.api.v1.controller;

import com.knitwit.model.CourseRating;
import com.knitwit.service.CourseRatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ratings")
@SecurityRequirement(name = "Keycloak")
public class CourseRatingController {

    private final CourseRatingService courseRatingService;

    @Operation(summary = "Добавить оценку курсу")
    //user
    @PostMapping("/course/{courseId}/user/{userId}")
    public ResponseEntity<CourseRating> rateCourse(
            @PathVariable("courseId") int courseId,
            @PathVariable("userId") int userId,
            @RequestBody Map<String, Integer> requestBody) {
        int value = requestBody.get("value");
        if (value < 1 || value > 5) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            CourseRating rating = courseRatingService.rateCourse(courseId, userId, value);
            return new ResponseEntity<>(rating, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Удалить оценку курсу")
    //user
    @DeleteMapping("/course/{courseId}/user/{userId}")
    public ResponseEntity<Void> deleteRating(
            @PathVariable("courseId") int courseId,
            @PathVariable("userId") int userId) {
        boolean deleted = courseRatingService.deleteRating(courseId, userId);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Получить оценки пользователя")
    //admin
    @GetMapping("/user/{userId}/ratings")
    public ResponseEntity<List<CourseRating>> getUserRatings(@PathVariable("userId") int userId) {
        List<CourseRating> ratings = courseRatingService.getUserRatings(userId);
        return new ResponseEntity<>(ratings, HttpStatus.OK);
    }

    @Operation(summary = "Получить оценки курса")
    //admin
    @GetMapping("/course/{courseId}/ratings")
    public ResponseEntity<List<CourseRating>> getCourseRatings(@PathVariable("courseId") int courseId) {
        List<CourseRating> ratings = courseRatingService.getCourseRatings(courseId);
        return new ResponseEntity<>(ratings, HttpStatus.OK);
    }

    @Operation(summary = "Получить среднюю оценку курса")
    //user
    @GetMapping("/course/{courseId}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable("courseId") int courseId) {
        Double averageRating = courseRatingService.getAverageRating(courseId);
        return new ResponseEntity<>(averageRating, HttpStatus.OK);
    }
}

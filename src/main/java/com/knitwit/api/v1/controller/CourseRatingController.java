package com.knitwit.api.v1.controller;

import com.knitwit.enums.RatingValue;
import com.knitwit.model.CourseRating;
import com.knitwit.service.CourseRatingService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ratings")
public class CourseRatingController {

    @Autowired
    private CourseRatingService courseRatingService;

    @Operation(summary = "Создать новый рейтинг курса")
    @PostMapping
    public ResponseEntity<CourseRating> createCourseRating(@RequestBody CourseRating courseRating) {
        CourseRating createdRating = courseRatingService.createCourseRating(courseRating);
        return ResponseEntity.ok(createdRating);
    }

    @Operation(summary = "Удалить оценку курса ID")
    @DeleteMapping("/{ratingId}")
    public ResponseEntity<Void> deleteCourseRating(@PathVariable int ratingId) {
        courseRatingService.deleteCourseRating(ratingId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Обновить рейтинг курса по ID")
    @PutMapping("/{ratingId}")
    public ResponseEntity<CourseRating> updateCourseRating(@PathVariable int ratingId, @RequestBody CourseRating updatedCourseRating) {
        CourseRating rating = courseRatingService.updateCourseRating(ratingId, updatedCourseRating);
        return ResponseEntity.ok(rating);
    }

    @Operation(summary = "Получите оценку курса по ID")
    @GetMapping("/{ratingId}")
    public ResponseEntity<CourseRating> getCourseRatingById(@PathVariable int ratingId) {
        CourseRating rating = courseRatingService.getCourseRatingById(ratingId);
        return ResponseEntity.ok(rating);
    }

    @Operation(summary = "Получить количество оценок курса")
    @GetMapping("/countByCourse/{courseId}")
    public ResponseEntity<Integer> countRatingsByCourse(@PathVariable int courseId) {
        int count = courseRatingService.countByCourse(courseId);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Получить количество определённых оценок у курса")
    @GetMapping("/countByCourseAndValue/{courseId}/{value}")
    public ResponseEntity<Integer> countRatingsByCourseAndValue(@PathVariable int courseId, @PathVariable RatingValue value) {
        int count = courseRatingService.countByCourseAndValue(courseId, value);
        return ResponseEntity.ok(count);
    }
}

package com.knitwit.api.v1.controller;

import com.knitwit.service.LearningProgressService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/progress")
public class LearningProgressController {

    private final LearningProgressService learningProgressService;

    public LearningProgressController(LearningProgressService learningProgressService) {
        this.learningProgressService = learningProgressService;
    }

    @Operation(summary = "Отметить раздел как завершенный для пользователя")
    @PostMapping("/{userId}/section/{sectionId}/complete")
    public ResponseEntity<Void> markSectionAsCompleted(@PathVariable int userId, @PathVariable int sectionId) {
        learningProgressService.markSectionAsCompleted(userId, sectionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Отметить раздел как незавершенный для пользователя")
    @PostMapping("/{userId}/section/{sectionId}/incomplete")
    public ResponseEntity<Void> markSectionAsIncomplete(@PathVariable int userId, @PathVariable int sectionId) {
        learningProgressService.markSectionAsIncomplete(userId, sectionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить процент завершенных разделов для пользователя в курсе")
    @GetMapping("/{userId}/course/{courseId}/completion")
    public ResponseEntity<Integer> getCourseCompletionPercentage(@PathVariable int userId, @PathVariable int courseId) {
        double completionPercentage = learningProgressService.getCompletionPercentageForUserAndCourse(userId, courseId);
        return ResponseEntity.ok((int) completionPercentage);
    }
}

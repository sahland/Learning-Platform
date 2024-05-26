package com.knitwit.api.v1.controller;

import com.knitwit.service.LearningProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/progress")
@SecurityRequirement(name = "Keycloak")
public class LearningProgressController {

    private final LearningProgressService learningProgressService;

    @Operation(summary = "Отметить раздел как завершенный для пользователя")
    @PostMapping("/{userId}/section/{sectionId}/complete")
    //user
    public ResponseEntity<Void> markSectionAsCompleted(@PathVariable int userId, @PathVariable int sectionId) {
        learningProgressService.markSectionAsCompleted(userId, sectionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Отметить раздел как незавершенный для пользователя")
    @PostMapping("/{userId}/section/{sectionId}/incomplete")
    //user
    public ResponseEntity<Void> markSectionAsIncomplete(@PathVariable int userId, @PathVariable int sectionId) {
        learningProgressService.markSectionAsIncomplete(userId, sectionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить процент завершенных разделов для пользователя в курсе")
    @GetMapping("/{userId}/course/{courseId}/completion")
    //user
    public ResponseEntity<Integer> getCourseCompletionPercentage(@PathVariable int userId, @PathVariable int courseId) {
        double completionPercentage = learningProgressService.getCompletionPercentageForUserAndCourse(userId, courseId);
        return ResponseEntity.ok((int) completionPercentage);
    }
}

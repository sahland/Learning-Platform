package com.knitwit.api.v1.controller;

import com.knitwit.model.LearningProgress;
import com.knitwit.service.LearningProgressService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/progress")
public class LearningProgressController {

    @Autowired
    private LearningProgressService learningProgressService;

    @Operation(summary = "Получение списка записей о прогрессе для конкретного пользователя и курса")
    @GetMapping("/{userId}/{courseId}")
    public ResponseEntity<List<LearningProgress>> getUserLearningProgress(@PathVariable int userId, @PathVariable int courseId) {
        List<LearningProgress> progressList = learningProgressService.getUserLearningProgressByUserIdAndCourseId(userId, courseId);
        return ResponseEntity.ok(progressList);
    }

    @Operation(summary = "Сохранение или обновление записи о прогрессе")
    @PostMapping
    public ResponseEntity<LearningProgress> saveLearningProgress(@RequestBody LearningProgress learningProgress) {
        LearningProgress savedProgress = learningProgressService.saveLearningProgress(learningProgress);
        return ResponseEntity.ok(savedProgress);
    }
}

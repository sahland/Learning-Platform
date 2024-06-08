package com.knitwit.api.v1.controller;

import com.knitwit.api.v1.dto.request.LearningProgressRequest;
import com.knitwit.api.v1.dto.response.LearningProgressResponse;
import com.knitwit.api.v1.dto.mapper.LearningProgressMapper;
import com.knitwit.model.LearningProgress;
import com.knitwit.model.User;
import com.knitwit.repository.UserRepository;
import com.knitwit.service.LearningProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/progress")
public class LearningProgressController {

    private final LearningProgressService learningProgressService;
    private final LearningProgressMapper learningProgressMapper;
    private final UserRepository userRepository;

    private User getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaim("preferred_username");
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден по логину: " + username));
    }

    @Operation(summary = "Отметить раздел как завершенный для пользователя")
    @PostMapping("/section/{sectionId}/complete")
    public ResponseEntity<LearningProgressResponse> markSectionAsCompleted(@AuthenticationPrincipal Jwt jwt, @PathVariable int sectionId) {
        User user = getCurrentUser(jwt);
        LearningProgressRequest request = new LearningProgressRequest();
        request.setSectionId(sectionId);
        request.setCompleted(true);

        LearningProgress progress = learningProgressService.markSectionAsCompleted(user, request);
        LearningProgressResponse response = learningProgressMapper.toResponse(progress);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Отметить раздел как незавершенный для пользователя")
    @PostMapping("/section/{sectionId}/incomplete")
    public ResponseEntity<LearningProgressResponse> markSectionAsIncomplete(@AuthenticationPrincipal Jwt jwt, @PathVariable int sectionId) {
        User user = getCurrentUser(jwt);
        LearningProgressRequest request = new LearningProgressRequest();
        request.setSectionId(sectionId);
        request.setCompleted(false);

        LearningProgress progress = learningProgressService.markSectionAsIncomplete(user, request);
        LearningProgressResponse response = learningProgressMapper.toResponse(progress);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получить процент завершенных разделов для пользователя в курсе")
    @GetMapping("/course/{courseId}/completion")
    public ResponseEntity<Integer> getCourseCompletionPercentage(@AuthenticationPrincipal Jwt jwt, @PathVariable int courseId) {
        User user = getCurrentUser(jwt);
        int completionPercentage = learningProgressService.getCompletionPercentageForUserAndCourse(user.getUserId(), courseId);
        return ResponseEntity.ok(completionPercentage);
    }
}

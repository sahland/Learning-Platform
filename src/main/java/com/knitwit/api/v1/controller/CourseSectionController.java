package com.knitwit.api.v1.controller;

import com.knitwit.api.v1.dto.request.CourseSectionRequest;
import com.knitwit.api.v1.dto.response.CourseSectionResponse;
import com.knitwit.mapper.CourseSectionMapper;
import com.knitwit.model.CourseSection;
import com.knitwit.service.CourseSectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sections")
@SecurityRequirement(name = "Keycloak")
public class CourseSectionController {

    private final CourseSectionService courseSectionService;
    private final CourseSectionMapper courseSectionMapper;

    @Operation(summary = "Получить раздел курса по ID")
    @GetMapping("/{sectionId}")
    public ResponseEntity<CourseSectionResponse> getSectionById(@PathVariable int sectionId) {
        CourseSection section = courseSectionService.getSectionById(sectionId);
        if (section != null) {
            CourseSectionResponse response = courseSectionMapper.toResponse(section);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Создать новый раздел курса")
    @PostMapping
    public ResponseEntity<CourseSectionResponse> createSection(@RequestBody CourseSectionRequest request) {
        CourseSection section = courseSectionService.createSection(request);
        CourseSectionResponse response = courseSectionMapper.toResponse(section);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

package com.knitwit.api.v1.controller;

import com.knitwit.api.v1.dto.request.CourseSectionRequest;
import com.knitwit.api.v1.dto.response.CourseSectionResponse;
import com.knitwit.api.v1.dto.mapper.CourseSectionMapper;
import com.knitwit.model.CourseSection;
import com.knitwit.service.CourseSectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sections")
public class CourseSectionController {

    private final CourseSectionService courseSectionService;
    private final CourseSectionMapper courseSectionMapper;

    @Operation(summary = "Получить раздел курса по ID (ADMIN)")
    @GetMapping("/{sectionId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<CourseSectionResponse> getSectionById(@PathVariable int sectionId) {
        CourseSection section = courseSectionService.getSectionById(sectionId);
        if (section != null) {
            CourseSectionResponse response = courseSectionMapper.toResponse(section);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

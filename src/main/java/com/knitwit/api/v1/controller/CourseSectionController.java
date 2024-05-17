package com.knitwit.api.v1.controller;

import com.knitwit.model.CourseSection;
import com.knitwit.service.CourseSectionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sections")
public class CourseSectionController {

    @Autowired
    private CourseSectionService courseSectionService;

    @Operation(summary = "Получить раздел курса по ID")
    @GetMapping("/{sectionId}")
    public ResponseEntity<CourseSection> getSectionById(@PathVariable int sectionId) {
        CourseSection section = courseSectionService.getSectionById(sectionId);
        if (section != null) {
            return new ResponseEntity<>(section, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

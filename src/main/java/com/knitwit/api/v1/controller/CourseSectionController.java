package com.knitwit.api.v1.controller;

import com.knitwit.model.CourseSection;
import com.knitwit.service.CourseSectionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sections")
public class CourseSectionController {

    @Autowired
    private CourseSectionService courseSectionService;

    @Operation(summary = "Создание раздела курса")
    @PostMapping
    public ResponseEntity<CourseSection> createCourseSection(@RequestBody CourseSection courseSection) {
        CourseSection createdSection = courseSectionService.createCourseSection(courseSection);
        return ResponseEntity.ok(createdSection);
    }

    @Operation(summary = "Удаление раздела курса")
    @DeleteMapping("/{sectionId}")
    public ResponseEntity<Void> deleteCourseSection(@PathVariable int sectionId) {
        courseSectionService.deleteCourseSection(sectionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Редактирование раздела курса")
    @PutMapping("/{sectionId}")
    public ResponseEntity<CourseSection> updateCourseSection(@PathVariable int sectionId, @RequestBody CourseSection updatedCourseSection) {
        CourseSection updatedSection = courseSectionService.updateCourseSection(sectionId, updatedCourseSection);
        return ResponseEntity.ok(updatedSection);
    }

    @Operation(summary = "Получение списка всех разделов курса")
    @GetMapping
    public ResponseEntity<List<CourseSection>> getAllCourseSections() {
        List<CourseSection> sections = courseSectionService.getAllCourseSections();
        return ResponseEntity.ok(sections);
    }

    @Operation(summary = "Получение раздела курса по его ID")
    @GetMapping("/{sectionId}")
    public ResponseEntity<CourseSection> getCourseSectionById(@PathVariable int sectionId) {
        CourseSection section = courseSectionService.getCourseSectionById(sectionId);
        return ResponseEntity.ok(section);
    }
}

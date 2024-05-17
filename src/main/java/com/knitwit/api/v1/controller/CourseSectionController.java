package com.knitwit.api.v1.controller;

import com.knitwit.model.CourseSection;
import com.knitwit.service.CourseSectionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @PostMapping("/image/upload")
    public ResponseEntity<List<String>> uploadSectionImages(
            @PathVariable int courseId,
            @PathVariable int sectionId,
            @RequestParam("files") List<MultipartFile> files) {
        List<String> imageUrls = courseSectionService.uploadSectionImages(courseId, sectionId, files);
        return ResponseEntity.ok(imageUrls);
    }

    @GetMapping("/image/{imageName}")
    public ResponseEntity <Resource> getSectionImage(
            @PathVariable int courseId,
            @PathVariable int sectionId,
            @PathVariable String imageName) {
        Resource imageResource = courseSectionService.getSectionImage(courseId, sectionId, imageName);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageResource);
    }

}

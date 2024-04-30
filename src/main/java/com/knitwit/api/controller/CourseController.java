package com.knitwit.api.controller;

import com.knitwit.model.Course;
import com.knitwit.model.Tag;
import com.knitwit.model.User;
import com.knitwit.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @Operation(summary = "Create a new course")
    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        Course createdCourse = courseService.addCourse(course);
        return ResponseEntity.ok(createdCourse);
    }

    @Operation(summary = "Delete a course by ID")
    @DeleteMapping("/{courseId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Course deleted"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<Void> deleteCourse(@PathVariable int courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update a course by ID")
    @PutMapping("/{courseId}")
    public ResponseEntity<Course> updateCourse(@PathVariable int courseId, @RequestBody Course updatedCourse) {
        Course course = courseService.updateCourse(courseId, updatedCourse);
        return ResponseEntity.ok(course);
    }

    @Operation(summary = "Get a course by ID")
    @GetMapping("/{courseId}")
    public ResponseEntity<Course> getCourseById(@PathVariable int courseId) {
        Course course = courseService.getCourseById(courseId);
        return ResponseEntity.ok(course);
    }

    @Operation(summary = "Get all courses")
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Get courses by multiple IDs")
    @GetMapping("/ids")
    public ResponseEntity<List<Course>> getCoursesByIds(@RequestParam List<Integer> courseIds) {
        List<Course> courses = courseService.getCoursesByIds(courseIds);
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Get total count of courses")
    @GetMapping("/count")
    public ResponseEntity<Long> countAllCourses() {
        long count = courseService.countAllCourses();
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Get courses created by a user")
    @GetMapping("/createdByUser/{userId}")
    public ResponseEntity<List<Course>> getCoursesCreatedByUser(@PathVariable int userId) {
        List<Course> courses = courseService.getCoursesCreatedByUser(userId);
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Search courses by title containing a keyword")
    @GetMapping("/search")
    public ResponseEntity<List<Course>> searchCoursesByTitle(@RequestParam String keyword) {
        List<Course> courses = courseService.searchCoursesByTitle(keyword);
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Delete tag from course")
    @DeleteMapping("/{courseId}/tags/{tagId}")
    public ResponseEntity<Void> deleteTagFromCourse(@PathVariable int courseId, @PathVariable int tagId) {
        courseService.deleteTagFromCourse(courseId, tagId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get courses by tag ID")
    @GetMapping("/tags/{tagId}")
    public ResponseEntity<Set<Course>> getAllCoursesForTag(@PathVariable int tagId) {
        Set<Course> courses = courseService.getAllCoursesForTag(tagId);
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Get tags by course ID")
    @GetMapping("/{courseId}/tags")
    public ResponseEntity<Set<Tag>> getTagsByCourseId(@PathVariable int courseId) {
        Set<Tag> tags = courseService.getTagsByCourseId(courseId);
        return ResponseEntity.ok(tags);
    }

    @Operation(summary = "Subscribe user to course")
    @PostMapping("/{courseId}/subscribe/{userId}")
    public ResponseEntity<Void> subscribeToCourse(@PathVariable User user, @PathVariable Course course) {
        courseService.subscribeToCourse(user, course);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Unsubscribe user from course")
    @DeleteMapping("/{courseId}/unsubscribe/{userId}")
    public ResponseEntity<Void> unsubscribeFromCourse(@PathVariable User user, @PathVariable Course course) {
        courseService.unsubscribeFromCourse(user, course);
        return ResponseEntity.noContent().build();
    }
}

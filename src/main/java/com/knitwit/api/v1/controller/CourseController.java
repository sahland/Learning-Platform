package com.knitwit.api.v1.controller;

import com.knitwit.model.Course;
import com.knitwit.model.CourseSection;
import com.knitwit.model.Tag;
import com.knitwit.service.CourseService;
import com.knitwit.service.CourseSectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseService courseService;
    private final CourseSectionService courseSectionService;

    @Autowired
    public CourseController(CourseService courseService, CourseSectionService courseSectionService) {
        this.courseService = courseService;
        this.courseSectionService = courseSectionService;
    }

    @PostMapping
    public ResponseEntity<Course> createCourseWithSection(@RequestBody CourseWithSectionRequest request) {
        Course createdCourse = courseService.createCourseWithSection(request.getCourse(), request.getSection());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
    }

    @PostMapping("/{courseId}/sections")
    public ResponseEntity<CourseSection> addSectionToCourse(@PathVariable int courseId, @RequestBody CourseSection section) {
        CourseSection addedSection = courseService.addSectionToCourse(courseId, section);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedSection);
    }

    @Operation(summary = "Удалить курс по ID")
    @DeleteMapping("/{courseId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Курс удален."),
            @ApiResponse(responseCode = "404", description = "Курс не найден")
    })
    public ResponseEntity<Void> deleteCourse(@PathVariable int courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Добавить тег к курсу")
    @PostMapping("/{courseId}/tags/{tagId}")
    public ResponseEntity<Void> addTagToCourse(@PathVariable int courseId, @PathVariable int tagId) {
        courseService.addTagToCourse(courseId, tagId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Обновить курс по ID")
    @PutMapping("/{courseId}")
    public ResponseEntity<Course> updateCourse(@PathVariable int courseId, @RequestBody Course updatedCourse, @RequestParam(required = false) boolean publish) {
        Course course = courseService.updateCourse(courseId, updatedCourse, publish);
        return ResponseEntity.ok(course);
    }

    @Operation(summary = "Получить курс по ID")
    @GetMapping("/{courseId}")
    public ResponseEntity<Course> getCourseById(@PathVariable int courseId) {
        Course course = courseService.getCourseById(courseId);
        if (course != null) {
            return new ResponseEntity<>(course, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Получить все курсы")
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Приобретайте курсы несколькими ID")
    @GetMapping("/ids")
    public ResponseEntity<List<Course>> getCoursesByIds(@RequestParam List<Integer> courseIds) {
        List<Course> courses = courseService.getCoursesByIds(courseIds);
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Получить общее количество курсов")
    @GetMapping("/count")
    public ResponseEntity<Long> countAllCourses() {
        long count = courseService.countAllCourses();
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Получить курсы, созданные пользователем")
    @GetMapping("/createdByUser/{userId}")
    public ResponseEntity<List<Course>> getCoursesCreatedByUser(@PathVariable int userId) {
        List<Course> courses = courseService.getCoursesCreatedByUser(userId);
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Поиск курсов по названию, содержащему ключевое слово")
    @GetMapping("/search")
    public ResponseEntity<List<Course>> searchCoursesByTitle(@RequestParam String keyword) {
        List<Course> courses = courseService.searchCoursesByTitle(keyword);
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Удалить тег из курса")
    @DeleteMapping("/{courseId}/tags/{tagId}")
    public ResponseEntity<Void> deleteTagFromCourse(@PathVariable int courseId, @PathVariable int tagId) {
        courseService.deleteTagFromCourse(courseId, tagId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить курсы по ID тега")
    @GetMapping("/tags/{tagId}")
    public ResponseEntity<Set<Course>> getAllCoursesForTag(@PathVariable int tagId) {
        Set<Course> courses = courseService.getAllCoursesForTag(tagId);
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Получить теги по ID курса")
    @GetMapping("/{courseId}/tags")
    public ResponseEntity<Set<Tag>> getTagsByCourseId(@PathVariable int courseId) {
        Set<Tag> tags = courseService.getTagsByCourseId(courseId);
        return ResponseEntity.ok(tags);
    }

    @Operation(summary = "Подписать пользователя на курс")
    @PostMapping("/{courseId}/subscribe/{userId}")
    public ResponseEntity<Void> subscribeToCourse(@PathVariable int userId, @PathVariable int courseId) {
        courseService.subscribeToCourse(userId, courseId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Отписать пользователя от курса")
    @DeleteMapping("/{courseId}/unsubscribe/{userId}")
    public ResponseEntity<Void> unsubscribeFromCourse(@PathVariable int userId, @PathVariable int courseId) {
        courseService.unsubscribeFromCourse(userId, courseId);
        return ResponseEntity.noContent().build();
    }
}

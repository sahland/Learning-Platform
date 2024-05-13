package com.knitwit.api.v1.controller;

import com.knitwit.api.v1.request.CourseWithSectionsAndTagsRequest;
import com.knitwit.model.Course;
import com.knitwit.model.CourseSection;
import com.knitwit.model.MediaFile;
import com.knitwit.model.Tag;
import com.knitwit.service.CourseService;
import com.knitwit.service.CourseSectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Course> createCourse(@RequestBody CourseWithSectionsAndTagsRequest request) {
        Course createdCourse = courseService.createCourseWithSections(request.getCourse(), request.getSections(), request.getTags());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
    }

    @Operation(summary = "Получить все курсы")
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
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

    @Operation(summary = "Обновить курс по ID")
    @PutMapping("/{courseId}")
    public ResponseEntity<Course> updateCourse(@PathVariable int courseId, @RequestBody Course updatedCourse, @RequestParam(required = false) boolean publish) {
        Course course = courseService.updateCourse(courseId, updatedCourse, publish);
        return ResponseEntity.ok(course);
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

    @Operation(summary = "Получить все секции курса по его ID")
    @GetMapping("/{courseId}/sections")
    public ResponseEntity<List<CourseSection>> getAllSectionsByCourseId(@PathVariable int courseId) {
        List<CourseSection> sections = courseService.getAllSectionsByCourseId(courseId);
        return ResponseEntity.ok(sections);
    }

    @Operation(summary = "Добавить секцию к курсу")
    @PostMapping("/{courseId}/sections")
    public ResponseEntity<List<CourseSection>> addSectionsToCourse(@PathVariable int courseId, @RequestBody List<CourseSection> sections) {
        List<CourseSection> addedSections = courseService.addSectionsToCourse(courseId, sections);

        return ResponseEntity.status(HttpStatus.CREATED).body(addedSections);
    }

    @Operation(summary = "Удалить секцию из курса")
    @DeleteMapping("/{courseId}/sections/{sectionId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Секция удалена из курса."),
            @ApiResponse(responseCode = "404", description = "Курс или секция не найдены")
    })
    public ResponseEntity<Void> deleteSectionFromCourse(@PathVariable int courseId, @PathVariable int sectionId) {
        courseService.deleteSectionFromCourse(courseId, sectionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Обновить секцию курса")
    @PutMapping("/{courseId}/sections/{sectionId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Секция курса обновлена."),
            @ApiResponse(responseCode = "404", description = "Курс или секция не найдены")
    })
    public ResponseEntity<Void> updateSection(@PathVariable int courseId, @PathVariable int sectionId, @RequestBody CourseSection updatedSection) {
        courseService.updateSection(courseId, sectionId, updatedSection);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Подтвердить курс по ID")
    @PostMapping("/{courseId}/confirm")
    public ResponseEntity<Void> confirmCourse(@PathVariable int courseId) {
        courseService.confirmCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Отклонить курс по ID")
    @PostMapping("/{courseId}/reject")
    public ResponseEntity<Void> rejectCourse(@PathVariable int courseId) {
        courseService.rejectCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить все отклоненные курсы")
    @GetMapping("/rejected")
    public ResponseEntity<List<Course>> getAllRejectedCourses() {
        List<Course> courses = courseService.getAllRejectedCourses();
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Получить все курсы в процессе")
    @GetMapping("/processing")
    public ResponseEntity<List<Course>> getAllCoursesInProcessing() {
        List<Course> courses = courseService.getAllCoursesInProcessing();
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Получить все подтвержденные курсы")
    @GetMapping("/published")
    public ResponseEntity<List<Course>> getAllPublishedCourses() {
        List<Course> courses = courseService.getAllPublishedCourses();
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Получить все отклоненные курсы с пагинацией")
    @GetMapping("/rejected/pagination")
    public ResponseEntity<Page<Course>> getAllRejectedCoursesWithPagination(@RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> courses = courseService.getAllRejectedCoursesWithPagination(pageable);
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Получить все курсы в процессе с пагинацией")
    @GetMapping("/processing/pagination")
    public ResponseEntity<Page<Course>> getAllCoursesInProcessingWithPagination(@RequestParam(defaultValue = "0") int page,
                                                                                @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> courses = courseService.getAllCoursesInProcessingWithPagination(pageable);
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Получить все подтвержденные курсы с пагинацией")
    @GetMapping("/published/pagination")
    public ResponseEntity<Page<Course>> getAllPublishedCoursesWithPagination(@RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> courses = courseService.getAllPublishedCoursesWithPagination(pageable);
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Добавить теги к курсу")
    @PostMapping("/{courseId}/tags")
    public ResponseEntity<Void> addTagsToCourse(@PathVariable int courseId, @RequestBody List<Integer> tagIds) {
        courseService.addTagsToCourse(courseId, tagIds);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Удалить теги из курса")
    @DeleteMapping("/{courseId}/tags")
    public ResponseEntity<Void> deleteTagsFromCourse(@PathVariable int courseId, @RequestBody List<Integer> tagIds) {
        courseService.deleteTagsFromCourse(courseId, tagIds);
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
    @Operation(summary = "Добавить аватар курса")
    @PostMapping("/{courseId}/avatar")
    public ResponseEntity<Void> addAvatarToCourse(@PathVariable int courseId, @RequestBody MediaFile avatarFile) {
        courseService.addAvatarToCourse(courseId, avatarFile);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Удалить аватар курса")
    @DeleteMapping("/{courseId}/avatar")
    public ResponseEntity<Void> removeAvatarFromCourse(@PathVariable int courseId) {
        courseService.removeAvatarFromCourse(courseId);
        return ResponseEntity.noContent().build();
    }

}

package com.knitwit.api.v1.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knitwit.api.v1.dto.mapper.CourseMapper;
import com.knitwit.api.v1.dto.request.CourseRequest;
import com.knitwit.api.v1.dto.response.CourseResponse;
import com.knitwit.model.Course;
import com.knitwit.model.CourseSection;
import com.knitwit.model.Tag;
import com.knitwit.model.User;
import com.knitwit.repository.CourseRepository;
import com.knitwit.repository.UserRepository;
import com.knitwit.service.AuthService;
import com.knitwit.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseService courseService;
    private final CourseMapper courseMapper;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Operation(summary = "Создать курс")
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    //user
    public ResponseEntity<CourseResponse> createCourse(
            @RequestPart("text") String courseJson,
            @RequestPart("file") MultipartFile avatar) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        CourseRequest request = objectMapper.readValue(courseJson, CourseRequest.class);
        String username = authService.getCurrentUsername(); // Получаем текущего пользователя
        Course createdCourse = courseService.createCourse(request.getCourse(), request.getSections(), request.getTags(), username, avatar);
        CourseResponse courseResponse = courseMapper.toResponse(createdCourse);
        return ResponseEntity.status(HttpStatus.OK).body(courseResponse);
    }

    @Operation(summary = "Редактировать курс")
    @PutMapping(path = "/{courseId}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<CourseResponse> editCourse(
            @PathVariable int courseId,
            @RequestPart("text") String courseJson,
            @RequestPart(value = "file", required = false) MultipartFile avatar,
            @AuthenticationPrincipal Jwt jwt) throws IOException {
        String username = jwt.getClaim("preferred_username");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        CourseRequest request = objectMapper.readValue(courseJson, CourseRequest.class);
        Course courseToEdit = courseService.getCourseForEditing(courseId);
        request.getCourse().setTitle(request.getCourse().getTitle());
        Course updatedCourse = courseService.editCourse(courseId, request.getCourse(), request.getSections(), request.getTags(), username, avatar);
        CourseResponse courseResponse = courseMapper.toResponse(updatedCourse);
        return ResponseEntity.status(HttpStatus.OK).body(courseResponse);
    }

    @Operation(summary = "Получить все курсы")
    @GetMapping
    //admin
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        List<CourseResponse> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Получить курс по ID")
    @GetMapping("/{courseId}")
    //user
    public ResponseEntity<Course> getCourseById(@PathVariable int courseId) {
        Course course = courseService.getCourseById(courseId);
        return ResponseEntity.ok(course);
    }

    @Operation(summary = "Удалить курс по ID")
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable int courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.ok().build();
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
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<CourseSection>> getAllSectionsByCourseId(@PathVariable int courseId) {
        List<CourseSection> sections = courseService.getAllSectionsByCourseId(courseId);
        return ResponseEntity.ok(sections);
    }

    @Operation(summary = "Подтвердить курс по ID")
    @PostMapping("/{courseId}/confirm")
    public ResponseEntity<Void> confirmCourse(@PathVariable int courseId) {
        courseService.confirmCourse(courseId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Отклонить курс по ID")
    @PostMapping("/{courseId}/reject")
    public ResponseEntity<Void> rejectCourse(@PathVariable int courseId) {
        courseService.rejectCourse(courseId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить все отклоненные курсы")
    @GetMapping("/rejected")
    public ResponseEntity<List<Course>> getAllRejectedCourses() {
        List<Course> courses = courseService.getAllRejectedCourses();
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Получить все курсы в процессе")
    @GetMapping("/processing")
    @PreAuthorize("hasRole('admin')")
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
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Page<Course>> getAllRejectedCoursesWithPagination(@RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> courses = courseService.getAllRejectedCoursesWithPagination(pageable);
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Получить все курсы в процессе с пагинацией")
    @GetMapping("/processing/pagination")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Page<Course>> getAllCoursesInProcessingWithPagination(@RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> courses = courseService.getAllCoursesInProcessingWithPagination(pageable);
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Получить все подтвержденные курсы с пагинацией")
    @GetMapping("/published/pagination")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Page<Course>> getAllPublishedCoursesWithPagination(@RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> courses = courseService.getAllPublishedCoursesWithPagination(pageable);
        return ResponseEntity.ok(courses);
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

    @Operation(summary = "Подписаться на курс")
    @PostMapping("/{courseId}/subscribe")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<Void> subscribeToCourse(@PathVariable int courseId, @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaim("preferred_username");
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
        courseService.subscribeToCourse(user.getUserId(), courseId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Отписаться от курса")
    @DeleteMapping("/{courseId}/unsubscribe")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<Void> unsubscribeFromCourse(@PathVariable int courseId, @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaim("preferred_username");
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
        courseService.unsubscribeFromCourse(user.getUserId(), courseId);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Добавить аватар курса")
    @PostMapping("/{courseId}/avatar")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<String> uploadCourseAvatar(@PathVariable int courseId, @RequestParam("file") MultipartFile file) {
        String avatarUrl = courseService.uploadCourseAvatar(courseId, file);
        return ResponseEntity.ok(avatarUrl);
    }
}
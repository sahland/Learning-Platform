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
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseService courseService;
    private final CourseMapper courseMapper;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Operation(summary = "Создать курс (USER)")
    @Secured("ROLE_USER")
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<CourseResponse> createCourse(
            @RequestPart("text") String courseJson,
            @RequestPart("file") MultipartFile avatar) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        CourseRequest request = objectMapper.readValue(courseJson, CourseRequest.class);
        String username = authService.getCurrentUsername();
        Course createdCourse = courseService.createCourse(request.getCourse(), request.getSections(), request.getTags(), username, avatar);
        CourseResponse courseResponse = courseMapper.toResponse(createdCourse);
        return ResponseEntity.status(HttpStatus.OK).body(courseResponse);
    }

    @Operation(summary = "Редактировать курс (USER)")
    @Secured("ROLE_USER")
    @PutMapping(path = "/{courseId}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<CourseResponse> editCourse(
            @PathVariable int courseId,
            @RequestPart("text") String courseJson,
            @RequestPart(value = "file", required = false) MultipartFile avatar) throws IOException {
        String username = authService.getCurrentUsername();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        CourseRequest request = objectMapper.readValue(courseJson, CourseRequest.class);
        Course courseToEdit = courseService.getCourseForEditing(courseId);
        request.getCourse().setTitle(request.getCourse().getTitle());
        Course updatedCourse = courseService.editCourse(courseId, request.getCourse(), request.getSections(), request.getTags(), username, avatar);
        CourseResponse courseResponse = courseMapper.toResponse(updatedCourse);
        return ResponseEntity.status(HttpStatus.OK).body(courseResponse);
    }

    @Operation(summary = "Получить все курсы (ADMIN)")
    @GetMapping(path = "/all")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        List<CourseResponse> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Получить курс по ID (USER)" )
    @Secured("ROLE_USER")
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable int courseId) {
        Course course = courseService.getCourseById(courseId);
        CourseResponse courseResponse = courseMapper.toResponse(course);
        return ResponseEntity.ok(courseResponse);
    }

    @Operation(summary = "Удалить курс по ID (ADMIN)")
    @DeleteMapping("/{courseId}")
    @Secured("ROLE_ADMIN")
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

    @Operation(summary = "Получить курсы, созданные пользователем (USER)")
    @Secured("ROLE_USER")
    @GetMapping("/createdByUser/{userId}")
    public ResponseEntity<List<CourseResponse>> getCoursesCreatedByUser(@PathVariable int userId) {
        List<CourseResponse> courses = courseService.getCoursesCreatedByUser(userId).stream()
                .map(courseMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Поиск курсов по названию, содержащему ключевое слово")
    @GetMapping("/search")
    public ResponseEntity<List<CourseResponse>> searchCoursesByTitle(@RequestParam String keyword) {
        List<CourseResponse> courses = courseService.searchCoursesByTitle(keyword).stream()
                .map(courseMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Получить все секции курса по его ID (USER)")
    @Secured("ROLE_USER")
    @GetMapping("/{courseId}/sections")
    public ResponseEntity<List<CourseSection>> getAllSectionsByCourseId(@PathVariable int courseId) {
        List<CourseSection> sections = courseService.getAllSectionsByCourseId(courseId);
        return ResponseEntity.ok(sections);
    }

    @Operation(summary = "Подтвердить курс по ID (ADMIN)")
    @PostMapping("/{courseId}/confirm")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> confirmCourse(@PathVariable int courseId) {
        courseService.confirmCourse(courseId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Отклонить курс по ID (ADMIN)")
    @PostMapping("/{courseId}/reject")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> rejectCourse(@PathVariable int courseId) {
        courseService.rejectCourse(courseId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить все отклоненные курсы (ADMIN)")
    @GetMapping("/rejected")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<List<CourseResponse>> getAllRejectedCourses() {
        List<CourseResponse> courses = courseService.getAllRejectedCourses().stream()
                .map(courseMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Получить все курсы в процессе (ADMIN)")
    @GetMapping("/processing")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<List<CourseResponse>> getAllCoursesInProcessing() {
        List<CourseResponse> courses = courseService.getAllCoursesInProcessing().stream()
                .map(courseMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Получить все подтвержденные курсы")
    @GetMapping("/published")
    public ResponseEntity<List<CourseResponse>> getAllPublishedCourses() {
        List<CourseResponse> courses = courseService.getAllPublishedCourses().stream()
                .map(courseMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Получить все отклоненные курсы с пагинацией (ADMIN)")
    @GetMapping("/rejected/pagination")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Page<CourseResponse>> getAllRejectedCoursesWithPagination(@RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CourseResponse> courses = courseService.getAllRejectedCoursesWithPagination(pageable)
                .map(courseMapper::toResponse);
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Получить все курсы в процессе с пагинацией (ADMIN)")
    @GetMapping("/processing/pagination")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Page<CourseResponse>> getAllCoursesInProcessingWithPagination(@RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CourseResponse> courses = courseService.getAllCoursesInProcessingWithPagination(pageable)
                .map(courseMapper::toResponse);
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Получить все подтвержденные курсы с пагинацией (ADMIN)")
    @GetMapping("/published/pagination")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Page<CourseResponse>> getAllPublishedCoursesWithPagination(@RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CourseResponse> courses = courseService.getAllPublishedCoursesWithPagination(pageable)
                .map(courseMapper::toResponse);
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Получить курсы по ID тега")
    @GetMapping("/tags/{tagId}")
    public ResponseEntity<Set<CourseResponse>> getAllCoursesForTag(@PathVariable int tagId) {
        Set<CourseResponse> courses = courseService.getAllCoursesForTag(tagId).stream()
                .map(courseMapper::toResponse)
                .collect(Collectors.toSet());
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Получить теги по ID курса")
    @GetMapping("/{courseId}/tags")
    public ResponseEntity<Set<Tag>> getTagsByCourseId(@PathVariable int courseId) {
        Set<Tag> tags = courseService.getTagsByCourseId(courseId);
        return ResponseEntity.ok(tags);
    }

    @Operation(summary = "Подписаться на курс (USER)")
    @PostMapping("/{courseId}/subscribe")
    @Secured("ROLE_USER")
    public ResponseEntity<Void> subscribeToCourse(@PathVariable int courseId) {
        String username = authService.getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
        courseService.subscribeToCourse(user.getUserId(), courseId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Отписаться от курса (USER)")
    @DeleteMapping("/{courseId}/unsubscribe")
    @Secured("ROLE_USER")
    public ResponseEntity<Void> unsubscribeFromCourse(@PathVariable int courseId) {
        String username = authService.getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
        courseService.unsubscribeFromCourse(user.getUserId(), courseId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Добавить аватар курса (USER)")
    @PostMapping("/{courseId}/avatar")
    @Secured("ROLE_USER")
    public ResponseEntity<String> uploadCourseAvatar(@PathVariable int courseId, @RequestParam("file") MultipartFile file) {
        String avatarUrl = courseService.uploadCourseAvatar(courseId, file);
        return ResponseEntity.ok(avatarUrl);
    }
}
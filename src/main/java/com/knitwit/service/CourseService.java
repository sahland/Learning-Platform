package com.knitwit.service;

import com.knitwit.api.v1.dto.mapper.CourseMapper;
import com.knitwit.api.v1.dto.response.CourseResponse;
import com.knitwit.enums.CourseStatus;
import com.knitwit.model.*;
import com.knitwit.repository.CourseRepository;
import com.knitwit.repository.CourseSectionRepository;
import com.knitwit.repository.TagRepository;
import com.knitwit.repository.UserRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.io.Files.getFileExtension;

@Schema(description = "Сервис для работы с курсами")
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final MinioService minioService;
    private final CourseMapper courseMapper;

    @Transactional
    public Course createCourse(Course course, List<CourseSection> sections, List<Tag> tags, String username, MultipartFile avatarFile) {
        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с указанным логином не найден: " + username));
        if (sections == null || sections.isEmpty()) {
            throw new IllegalArgumentException("Курс должен содержать как минимум одну секцию.");
        }
        course.setCreator(creator);
        setSectionNumbersAndCourse(sections, course);
        course.setStatus(CourseStatus.IN_PROCESSING);
        course.setPublishedDate(LocalDate.now());
        Course savedCourse = courseRepository.save(course);
        Set<Tag> savedTags = new HashSet<>();
        for (Tag tag : tags) {
            List<Tag> existingTags = tagRepository.findByTagName(tag.getTagName());
            if (!existingTags.isEmpty()) {
                savedTags.addAll(existingTags);
            } else {
                throw new IllegalArgumentException("Тег с названием " + tag.getTagName() + " не найден.");
            }
        }
        savedCourse.setTags(savedTags);
        if (avatarFile != null && !avatarFile.isEmpty()) {
            uploadCourseAvatar(savedCourse.getCourseId(), avatarFile);
        }
        return savedCourse;
    }

    @Transactional
    public Course editCourse(int courseId, Course updatedCourse, List<CourseSection> updatedSections, List<Tag> updatedTags, String username, MultipartFile avatarFile) {
        User editor = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с указанным логином не найден: " + username));
        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Курс с указанным идентификатором не найден: " + courseId));
        if (updatedSections == null || updatedSections.isEmpty()) {
            throw new IllegalArgumentException("Курс должен содержать как минимум одну секцию.");
        }
        existingCourse.setTitle(updatedCourse.getTitle());
        existingCourse.setStatus(CourseStatus.IN_PROCESSING);
        updateSections(existingCourse, updatedSections);
        Set<Tag> savedTags = new HashSet<>();
        for (Tag tag : updatedTags) {
            List<Tag> existingTags = tagRepository.findByTagName(tag.getTagName());
            if (!existingTags.isEmpty()) {
                savedTags.addAll(existingTags);
            } else {
                throw new IllegalArgumentException("Тег с названием " + tag.getTagName() + " не найден.");
            }
        }
        existingCourse.setTags(savedTags);
        if (avatarFile != null && !avatarFile.isEmpty()) {
            uploadCourseAvatar(existingCourse.getCourseId(), avatarFile);
        }
        return courseRepository.save(existingCourse);
    }

    public Course getCourseForEditing(int courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Курс с указанным идентификатором не найден: " + courseId));
    }

    public List<CourseResponse> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream()
                .map(courseMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Course getConfirmedCourseById(int courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Курс с указанным ID не найден: " + courseId));
        if (!"PUBLISHED".equals(course.getStatus())) {
            throw new IllegalArgumentException("Курс с указанным ID не подтвержден: " + courseId);
        }
        return course;
    }

    public Course getCourseById(int courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Курс с указанным ID не найден: " + courseId));
    }

    @Transactional
    public void deleteCourse(int courseId) {
        Optional<Course> optionalCourse = courseRepository.findById(courseId);
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            course.getTags().clear();
            courseRepository.deleteById(courseId);
        } else {
            throw new IllegalArgumentException("Курс с указанным ID не найден: " + courseId);
        }
    }

    @Transactional
    public long countAllCourses() {
        return courseRepository.count();
    }

    public List<Course> getCoursesCreatedByUser(int userId) {
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с указанным ID не найден: " + userId));
        return courseRepository.findByCreatorAndStatusPublished(creator);
    }

    @Transactional
    public List<Course> searchCoursesByTitle(String keyword) {
        return courseRepository.findByTitleContainingAndStatusPublished(keyword);
    }

    private void setSectionNumbersAndCourse(List<CourseSection> sections, Course course) {
        int sectionNumber = 1;
        for (CourseSection section : sections) {
            section.setCourse(course);
            section.setSectionNumber(sectionNumber++);
            course.getSections().add(section);
        }
    }

    private void updateSections(Course course, List<CourseSection> updatedSections) {
        courseSectionRepository.deleteAllByCourseCourseId(course.getCourseId());
        course.getSections().clear();
        int sectionNumber = 1;
        for (CourseSection section : updatedSections) {
            section.setCourse(course);
            section.setSectionNumber(sectionNumber++);
            courseSectionRepository.save(section);
        }
        course.getSections().addAll(updatedSections);
    }

    public List<CourseSection> getAllSectionsByCourseId(int courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Курс с указанным ID не найден: " + courseId));
        if (!"PUBLISHED".equals(course.getStatus())) {
            throw new IllegalArgumentException("Курс с указанным ID не опубликован: " + courseId);
        }
        return course.getSections();
    }

    @Transactional
    public List<CourseSection> addSectionsToCourse(int courseId, List<CourseSection> sections) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Курс с указанным ID не найден: " + courseId));

        int lastSectionNumber = course.getSections().stream()
                .mapToInt(CourseSection::getSectionNumber)
                .max()
                .orElse(0);
        for (CourseSection section : sections) {
            section.setCourse(course);
            section.setSectionNumber(++lastSectionNumber);
            courseSectionRepository.save(section);
            course.getSections().add(section);
        }
        courseRepository.save(course);
        return sections;
    }

    @Transactional
    public void confirmCourse(int courseId) {
        Course course = getCourseById(courseId);
        course.setStatus(CourseStatus.PUBLISHED);
        courseRepository.save(course);
    }

    @Transactional
    public void rejectCourse(int courseId) {
        Course course = getCourseById(courseId);
        course.setStatus(CourseStatus.CANCELED);
        courseRepository.save(course);
    }

    public List<Course> getAllCoursesByStatus(CourseStatus status) {
        return courseRepository.findAllByStatus(status);
    }

    public Page<Course> getAllCoursesByStatusWithPagination(CourseStatus status, Pageable pageable) {
        return courseRepository.findAllByStatus(status, pageable);
    }

    public List<Course> getAllRejectedCourses() {
        return getAllCoursesByStatus(CourseStatus.CANCELED);
    }

    public List<Course> getAllCoursesInProcessing() {
        return getAllCoursesByStatus(CourseStatus.IN_PROCESSING);
    }

    public List<Course> getAllPublishedCourses() {
        return getAllCoursesByStatus(CourseStatus.PUBLISHED);
    }

    public Page<Course> getAllRejectedCoursesWithPagination(Pageable pageable) {
        return getAllCoursesByStatusWithPagination(CourseStatus.CANCELED, pageable);
    }

    public Page<Course> getAllCoursesInProcessingWithPagination(Pageable pageable) {
        return getAllCoursesByStatusWithPagination(CourseStatus.IN_PROCESSING, pageable);
    }

    public Page<Course> getAllPublishedCoursesWithPagination(Pageable pageable) {
        return getAllCoursesByStatusWithPagination(CourseStatus.PUBLISHED, pageable);
    }

    public Set<Course> getAllCoursesForTag(int tagId) {
        return courseRepository.findByTagIdAndStatusPublished(tagId);
    }

    public Set<Tag> getTagsByCourseId(int courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Курс не найден по ID: " + courseId));
        return course.getTags();
    }

    @Transactional
    public void subscribeToCourse(int userId, int courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден по ID: " + userId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Курс не найден по ID: " + courseId));

        if (!user.getCourses().contains(course)) {
            user.getCourses().add(course);
            course.getSubscribers().add(user);
            userRepository.save(user);
            courseRepository.save(course);
        }
    }

    @Transactional
    public void unsubscribeFromCourse(int userId, int courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден по ID: " + userId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Курс не найден по ID: " + courseId));

        if (user.getCourses().contains(course)) {
            user.getCourses().remove(course);
            course.getSubscribers().remove(user);
            userRepository.save(user);
            courseRepository.save(course);
        }
    }

    @Transactional
    public String uploadCourseAvatar(int courseId, MultipartFile file) {
        try {
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
                throw new IllegalArgumentException("Недопустимый тип файла. Разрешены только файлы JPEG и PNG.");
            }
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String objectName = "course_avatars/course_" + courseId + "_avatar." + fileExtension;
            Course course = getCourseById(courseId);
            String previousAvatarKey = course.getCourseAvatarKey();
            if (previousAvatarKey != null) {
                minioService.deleteFile(previousAvatarKey);
            }
            InputStream inputStream = file.getInputStream();
            minioService.uploadFile(objectName, inputStream, contentType);
            course.setCourseAvatarKey(objectName);
            courseRepository.save(course);

            return objectName;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить аватар курса", e);
        }
    }
}
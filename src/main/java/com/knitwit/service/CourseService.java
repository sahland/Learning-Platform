package com.knitwit.service;

import com.knitwit.enums.CourseStatus;
import com.knitwit.model.*;
import com.knitwit.repository.CourseRepository;
import com.knitwit.repository.CourseSectionRepository;
import com.knitwit.repository.TagRepository;
import com.knitwit.repository.UserRepository;
import io.swagger.v3.oas.annotations.media.Schema;
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

@Schema(description = "Сервис для работы с курсами")
@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final PlatformTransactionManager transactionManager;
    private final MinioService minioService;

    public CourseService(CourseRepository courseRepository, TagRepository tagRepository, UserRepository userRepository, CourseSectionRepository courseSectionRepository, PlatformTransactionManager transactionManager, MinioService minioService) {
        this.courseRepository = courseRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
        this.courseSectionRepository = courseSectionRepository;
        this.transactionManager = transactionManager;
        this.minioService = minioService;
    }

    @Transactional
    public Course createCourseWithSections(Course course, List<CourseSection> sections, List<Tag> tags, MultipartFile avatarFile) {
        if (sections == null || sections.isEmpty()) {
            throw new IllegalArgumentException("Курс должен содержать как минимум одну секцию.");
        }
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            setSectionNumbersAndCourse(sections, course);
            course.setStatus(CourseStatus.IN_PROCESSING);
            course.setPublishedDate(LocalDate.now());
            Course savedCourse = courseRepository.save(course);
            Set<Tag> savedTags = new HashSet<>();
            for (Tag tag : tags) {
                Tag existingTag = tagRepository.findByTagName(tag.getTagName());
                if (existingTag != null) {
                    savedTags.add(existingTag);
                } else {
                    throw new IllegalArgumentException("Тег с названием " + tag.getTagName() + " не найден.");
                }
            }
            savedCourse.setTags(savedTags);
            if (avatarFile != null && !avatarFile.isEmpty()) {
                uploadCourseAvatar(savedCourse.getCourseId(), avatarFile);
            }
            transactionManager.commit(status);
            return savedCourse;
        } catch (Exception ex) {
            transactionManager.rollback(status);
            throw ex;
        }
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(int courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Курс с указанным ID не найден: " + courseId));
    }

    @Transactional
    public Course updateCourse(int courseId, Course updatedCourse, boolean publish) {
        Optional<Course> optionalCourse = courseRepository.findById(courseId);
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            updatedCourse.setCourseId(courseId);
            if (publish) {
                updatedCourse.setStatus(CourseStatus.PUBLISHED);
            } else {
                updatedCourse.setStatus(course.getStatus());
            }
            course.setTitle(updatedCourse.getTitle());
            course.setPublishedDate(updatedCourse.getPublishedDate());
            updateSections(course, updatedCourse.getSections());

            return courseRepository.save(course);
        } else {
            throw new IllegalArgumentException("Курс с указанным ID не найден: " + courseId);
        }
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
        return courseRepository.findByCreatorUserId(userId);
    }

    @Transactional
    public List<Course> searchCoursesByTitle(String keyword) {
        return courseRepository.findByTitleContaining(keyword);
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
            CourseSection newSection = new CourseSection();
            newSection.setContent(section.getContent());
            newSection.setSectionNumber(sectionNumber++);
            newSection.setCourse(course);
            course.getSections().add(newSection);
        }
    }

    public List<CourseSection> getAllSectionsByCourseId(int courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Курс с указанным ID не найден: " + courseId));
        List<CourseSection> sections = course.getSections();
        return sections;
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
    public void deleteSectionFromCourse(int courseId, int sectionId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Курс с указанным ID не найден: " + courseId));

        CourseSection sectionToDelete = courseSectionRepository.findById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("Секция с указанным ID не найдена: " + sectionId));
        course.getSections().remove(sectionToDelete);
        courseSectionRepository.delete(sectionToDelete);
        List<CourseSection> sections = course.getSections();
        for (int i = 0; i < sections.size(); i++) {
            CourseSection section = sections.get(i);
            section.setSectionNumber(i + 1);
            courseSectionRepository.save(section);
        }
    }

    @Transactional
    public void updateSection(int courseId, int sectionId, CourseSection updatedSection) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Курс с указанным ID не найден: " + courseId));
        CourseSection section = courseSectionRepository.findById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("Секция с указанным ID не найдена: " + sectionId));
        if (section.getCourse().getCourseId() != courseId) {
            throw new IllegalArgumentException("Секция не принадлежит указанному курсу.");
        }
        section.setContent(updatedSection.getContent());
        courseSectionRepository.save(section);
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

    @Transactional
    public void addTagsToCourse(int courseId, List<Integer> tagIds) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Курс не найден по ID: " + courseId));
        List<Tag> tags = tagRepository.findAllById(tagIds);
        course.getTags().addAll(tags);
        courseRepository.save(course);
    }

    @Transactional
    public void deleteTagsFromCourse(int courseId, List<Integer> tagIds) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Курс не найден по ID: " + courseId));
        List<Tag> tags = tagRepository.findAllById(tagIds);
        course.getTags().removeAll(tags);
        courseRepository.save(course);
    }

    public Set<Course> getAllCoursesForTag(int tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("Тег не найден по ID: " + tagId));
        return tag.getCourses();
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

        if (user.getCourses().contains(course)) {
            throw new IllegalArgumentException("Пользователь уже подписан на данный курс.");
        }

        user.getCourses().add(course);
        course.getSubscribers().add(user);
    }

    @Transactional
    public void unsubscribeFromCourse(int userId, int courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден по ID: " + userId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Курс не найден по ID: " + courseId));

        if (!user.getCourses().contains(course)) {
            throw new IllegalArgumentException("Пользователь не подписан на данный курс.");
        }

        user.getCourses().remove(course);
        course.getSubscribers().remove(user);
    }

    public int getSectionCountByCourseId(int courseId) {
        return courseSectionRepository.countByCourseCourseId(courseId);
    }

    @Transactional
    public String uploadCourseAvatar(int courseId, MultipartFile file) {
        try {
            String objectName = "course_avatars/course_" + courseId + "_avatar.jpg";
            Course course = getCourseById(courseId);
            String previousAvatarKey = course.getCourseAvatarKey();
            if (previousAvatarKey != null) {
                minioService.deleteFile(previousAvatarKey);
            }
            InputStream inputStream = file.getInputStream();
            minioService.uploadFile(objectName, inputStream);
            course.setCourseAvatarKey(objectName);
            courseRepository.save(course);

            return objectName;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить аватар курса", e);
        }
    }

    public Resource getCourseAvatar(int courseId) {
        Course course = getCourseById(courseId);
        if (course == null || course.getCourseAvatarKey() == null) {
            throw new RuntimeException("Ключ аватара курса имеет значение null для курса с ID:" + courseId);
        }
        String objectName = course.getCourseAvatarKey();
        return minioService.getFileResource(objectName);
    }
}

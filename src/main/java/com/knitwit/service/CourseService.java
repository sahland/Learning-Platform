package com.knitwit.service;

import com.knitwit.enums.CourseStatus;
import com.knitwit.model.Course;
import com.knitwit.model.CourseSection;
import com.knitwit.model.Tag;
import com.knitwit.model.User;
import com.knitwit.repository.CourseRepository;
import com.knitwit.repository.CourseSectionRepository;
import com.knitwit.repository.TagRepository;
import com.knitwit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseSectionRepository courseSectionRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Transactional
    public Course createCourseWithSections(Course course, List<CourseSection> sections) {
        if (sections == null || sections.isEmpty()) {
            throw new IllegalArgumentException("Курс должен содержать как минимум одну секцию.");
        }
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            setSectionNumbersAndCourse(sections, course);
            course.setStatus(CourseStatus.IN_PROCESSING);
            Course savedCourse = courseRepository.save(course);
            transactionManager.commit(status);
            return savedCourse;
        } catch (Exception ex) {
            transactionManager.rollback(status);
            throw ex;
        }
    }

    @Transactional
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Transactional
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
            updateSections(course, updatedCourse.getSections()); // Обновление секций курса

            return courseRepository.save(course);
        } else {
            throw new IllegalArgumentException("Course not found with id: " + courseId);
        }
    }

    @Transactional
    public void deleteCourse(int courseId) {
        Optional<Course> optionalCourse = courseRepository.findById(courseId);
        if (optionalCourse.isPresent()) {
            courseRepository.deleteById(courseId);
        } else {
            throw new IllegalArgumentException("Курс с указанным ID не найден: " + courseId);
        }
    }

    @Transactional
    public long countAllCourses() {
        return courseRepository.count();
    }

    @Transactional
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

    @Transactional
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

    @Transactional
    public List<Course> getAllCoursesByStatus(CourseStatus status) {
        return courseRepository.findAllByStatus(status);
    }

    @Transactional
    public Page<Course> getAllCoursesByStatusWithPagination(CourseStatus status, Pageable pageable) {
        return courseRepository.findAllByStatus(status, pageable);
    }

    @Transactional
    public List<Course> getAllRejectedCourses() {
        return getAllCoursesByStatus(CourseStatus.CANCELED);
    }

    @Transactional
    public List<Course> getAllCoursesInProcessing() {
        return getAllCoursesByStatus(CourseStatus.IN_PROCESSING);
    }

    @Transactional
    public List<Course> getAllPublishedCourses() {
        return getAllCoursesByStatus(CourseStatus.PUBLISHED);
    }

    @Transactional
    public Page<Course> getAllRejectedCoursesWithPagination(Pageable pageable) {
        return getAllCoursesByStatusWithPagination(CourseStatus.CANCELED, pageable);
    }

    @Transactional
    public Page<Course> getAllCoursesInProcessingWithPagination(Pageable pageable) {
        return getAllCoursesByStatusWithPagination(CourseStatus.IN_PROCESSING, pageable);
    }

    @Transactional
    public Page<Course> getAllPublishedCoursesWithPagination(Pageable pageable) {
        return getAllCoursesByStatusWithPagination(CourseStatus.PUBLISHED, pageable);
    }

    @Transactional
    public void addTagToCourse(int courseId, int tagId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found with id: " + tagId));
        course.getTags().add(tag);
        courseRepository.save(course);
    }

    @Transactional
    public void deleteTagFromCourse(int courseId, int tagId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found with id: " + tagId));
        course.getTags().remove(tag);
        courseRepository.save(course);
    }

    @Transactional
    public Set<Course> getAllCoursesForTag(int tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found with id: " + tagId));
        return tag.getCourses();
    }

    @Transactional
    public Set<Tag> getTagsByCourseId(int courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));
        return course.getTags();
    }

    @Transactional
    public void subscribeToCourse(int userId, int courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));

        if (user.getCourses().contains(course)) {
            throw new IllegalArgumentException("Пользователь уже подписан на данный курс.");
        }

        user.getCourses().add(course);
        course.getSubscribers().add(user);
    }

    @Transactional
    public void unsubscribeFromCourse(int userId, int courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));

        if (!user.getCourses().contains(course)) {
            throw new IllegalArgumentException("Пользователь не подписан на данный курс.");
        }

        user.getCourses().remove(course);
        course.getSubscribers().remove(user);
    }
}

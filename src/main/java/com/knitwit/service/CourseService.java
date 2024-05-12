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

    public Course createCourseWithSection(Course course, CourseSection section) {
        if (section == null) {
            throw new IllegalArgumentException("Курс должен содержать как минимум одну секцию.");
        }

        // Создаем транзакцию
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            section.setCourse(course); // Устанавливаем связь с курсом
            course.getSections().add(section); // Добавляем секцию к курсу
            Course savedCourse = courseRepository.save(course); // Сохраняем курс с секцией
            transactionManager.commit(status); // Фиксируем транзакцию
            return savedCourse;
        } catch (Exception ex) {
            transactionManager.rollback(status); // Откатываем транзакцию в случае ошибки
            throw ex; // Перебрасываем исключение
        }
    }

    public CourseSection addSectionToCourse(int courseId, CourseSection section) {
        Course course = courseRepository.findById(courseId).orElse(null);
        section.setCourse(course);
        course.getSections().add(section);
        courseRepository.save(course);
        return section;
    }

    @Transactional
    public void deleteCourse(int courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new IllegalArgumentException("Курс с указанным ID не найден: " + courseId);
        }
        courseRepository.deleteById(courseId);
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
                updatedCourse.setStatus(course.getStatus()); // сохраняем текущий статус
            }
            return courseRepository.save(updatedCourse);
        } else {
            throw new IllegalArgumentException("Course not found with id: " + courseId);
        }
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> getCoursesByIds(List<Integer> courseIds) {
        return courseRepository.findAllByCourseIdIn(courseIds);
    }

    public long countAllCourses() {
        return courseRepository.count();
    }

    public List<Course> getCoursesCreatedByUser(int userId) {
        return courseRepository.findByCreatorUserId(userId);
    }

    public List<Course> searchCoursesByTitle(String keyword) {
        return courseRepository.findByTitleContaining(keyword);
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

    public Set<Course> getAllCoursesForTag(int tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found with id: " + tagId));
        return tag.getCourses();
    }

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

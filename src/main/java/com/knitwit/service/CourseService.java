package com.knitwit.service;

import com.knitwit.model.Course;
import com.knitwit.model.Tag;
import com.knitwit.model.User;
import com.knitwit.repository.CourseRepository;
import com.knitwit.repository.TagRepository;
import com.knitwit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository, TagRepository tagRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
    }

    //создание курса
    @Transactional
    public Course addCourse(Course course) {
        return  courseRepository.save(course);
    }

    //удаление курса
    @Transactional
    public void deleteCourse(int courseId) {
        courseRepository.deleteById(courseId);
    }

    //редактирование курса
    @Transactional
    public Course updateCourse(int courseId, Course updatedCourse) {
        Optional<Course> optionalCourse = courseRepository.findById(courseId);
        if (optionalCourse.isPresent()) {
            updatedCourse.setCourseId(courseId);
            return courseRepository.save(updatedCourse);
        } else {
            throw new IllegalArgumentException("Course not found with id: " + courseId);
        }
    }

    //получение курса по id
    public Course getCourseById(int courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));
    }

    //получение всех курсов
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    //получение курсов по нескольким id
    public List<Course> getCoursesByIds(List<Integer> courseIds) {
        return courseRepository.findAllByCourseIdIn(courseIds);
    }

    //получение колличества курсов
    public long countAllCourses(){
        return courseRepository.count();
    }

    //получение курсов по пользователею
    public List<Course> getCoursesCreatedByUser(int userId) {
        return courseRepository.findByCreatorUserId(userId);
    }

    //получение курсов по названию
    public List<Course> searchCoursesByTitle(String keyword) {
        return courseRepository.findByTitleContaining(keyword);
    }


    //добавление тега к курсу
    @Transactional
    public void addTagToCourse(int courseId, int tagId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found with id: " + tagId));
        course.getTags().add(tag);
        courseRepository.save(course);
    }

    //удаление тега из курса
    @Transactional
    public void deleteTagFromCourse(int courseId, int tagId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found with id: " + tagId));
        course.getTags().remove(tag);
        courseRepository.save(course);
    }

    //получение курсов по тегу
    public Set<Course> getAllCoursesForTag(int tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found with id: " + tagId));
        return tag.getCourses();
    }

    //получение тегов курса
    public Set<Tag> getTagsByCourseId(int courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));
        return course.getTags();
    }

    //подписка пользователя на курс
    @Transactional
    public void subscribeToCourse(int userId, int courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));
        user.getCourses().add(course);
        course.getSubscribers().add(user);
    }

    //отписка пользователя от курса
    @Transactional
    public void unsubscribeFromCourse(int userId, int courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));
        user.getCourses().remove(course);
        course.getSubscribers().remove(user);
    }
}

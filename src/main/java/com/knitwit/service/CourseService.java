package com.knitwit.service;

import com.knitwit.model.Course;
import com.knitwit.model.Tag;
import com.knitwit.repository.CourseRepository;
import com.knitwit.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final TagRepository tagRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository, TagRepository tagRepository) {
        this.courseRepository = courseRepository;
        this.tagRepository = tagRepository;
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

    //получение курсов по нескольким id
    public Page<Course> getCoursesByIds(List<Integer> courseIds, Pageable pageable) {
        return (Page<Course>) courseRepository.findAllByIdWithPagination(courseIds, pageable);
    }

    //получение колличества курсов
    public long countAllCourse(){
        return courseRepository.count();
    }

    //получение курсов по пользователею
    public Page<Course> getCourseCreatedByUser(int userId, Pageable pageable) {
        return courseRepository.findByCreatorUserId(userId, pageable);
    }

    //получение курсов по названию
    public Page<Course> searchCoursesByTitle(String keyword, Pageable pageable) {
        return courseRepository.findByTitleContaining(keyword, pageable);
    }

    //получение всех курсов
    public Page<Course> getAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable);
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
}

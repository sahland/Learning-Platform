package com.knitwit.service;

import com.knitwit.model.Course;
import com.knitwit.model.CourseSection;
import com.knitwit.repository.CourseRepository;
import com.knitwit.repository.CourseSectionRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Schema(description = "Сервис для работы с разделами курсов")
@Service
public class CourseSectionService {

    @Autowired
    private CourseSectionRepository courseSectionRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private MinioService minioService;
    @Autowired
    private CourseService courseService;

    public CourseSection getSectionById(int sectionId) {
        return courseSectionRepository.findById(sectionId).orElse(null);
    }

    public Optional<CourseSection> findById(int sectionId) {
        return courseSectionRepository.findById(sectionId);
    }

    @Transactional
    public List<String> uploadSectionImages(int courseId, int sectionId, List<MultipartFile> files) {
        try {
            Course course = courseService.getCourseById(courseId);
            CourseSection section = getSectionById(sectionId);

            List<String> imageUrls = new ArrayList<>();

            for (MultipartFile file : files) {
                String objectName = "course_sections/section_" + sectionId + "_image_" + UUID.randomUUID().toString() + ".jpg";
                InputStream inputStream = file.getInputStream();
                minioService.uploadFile(objectName, inputStream);

                String staticUrl = "http://localhost:9000/knitwit/" + objectName;
                section.getSectionImageKeys().add(objectName);
                imageUrls.add(staticUrl);
            }

            courseSectionRepository.save(section);
            return imageUrls;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload section images", e);
        }
    }


    @Transactional
    public Resource getSectionImage(int courseId, int sectionId, String imageName) {
        // Получаем курс
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));

        // Проверяем, существует ли секция с указанным идентификатором в данном курсе
        CourseSection section = null;
        for (CourseSection s : course.getSections()) {
            if (s.getSectionId() == sectionId) {
                section = s;
                break;
            }
        }
        if (section == null) {
            throw new IllegalArgumentException("Course section not found with id: " + sectionId);
        }

        // Проверяем, содержит ли секция указанное изображение
        List <String> sectionImages = section.getSectionImageKeys();
        if (!sectionImages.contains(imageName)) {
            throw new IllegalArgumentException("Image not found in section");
        }

        // Формируем путь к изображению
        String objectName = "course_sections/course_" + courseId + "/section_" + sectionId + "/images/" + imageName;

        // Получаем ресурс изображения из MinIO
        return minioService.getFileResource(objectName);
    }
}
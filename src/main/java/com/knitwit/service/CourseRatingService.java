package com.knitwit.service;

import com.knitwit.model.CourseRating;
import com.knitwit.repository.CourseRatingRepository;
import com.knitwit.repository.CourseRepository;
import com.knitwit.repository.UserRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Schema(description = "Сервис для работы с рейтингами курсов")
@Service
public class CourseRatingService {
    @Autowired
    private CourseRatingRepository courseRatingRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    UserRepository userRepository;

    @Schema(description = "Оценить курс")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Рейтинг успешно добавлен"),
            @ApiResponse(responseCode = "400", description = "Некорректное значение рейтинга")
    })
    @Transactional
    public CourseRating rateCourse(@Schema(description = "ID курса", example = "1")int courseId,
                                   @Schema(description = "ID пользователя", example = "4")int userId,
                                   @Schema(description = "Оценка курса (от 1 до 5)", example = "5")int value) {
        if (value < 1 || value > 5) {
            throw new IllegalArgumentException("Rating value must be between 1 and 5.");
        }
        CourseRating existingRating = courseRatingRepository.findCourseRatingByCourseCourseIdAndUserUserId(courseId, userId);
        if (existingRating != null) {
            existingRating.setValue(value);
            return courseRatingRepository.save(existingRating);
        } else {
            CourseRating newRating = new CourseRating();
            newRating.setCourse(courseRepository.getById(courseId));
            newRating.setUser(userRepository.getById(userId));
            newRating.setValue(value);
            return courseRatingRepository.save(newRating);
        }
    }

    @Schema(description = "Удалить оценку курса")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Оценка успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Оценка не найдена")
    })
    @Transactional
    public boolean deleteRating(@Schema(description = "ID курса", example = "3") int courseId,
                                @Schema(description = "ID пользователя", example = "15") int userId) {
        CourseRating rating = courseRatingRepository.findCourseRatingByCourseCourseIdAndUserUserId(courseId, userId);
        if (rating != null) {
            courseRatingRepository.delete(rating);
            return true;
        }
        return false;
    }

    @Schema(description = "Получить оценки пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Оценки пользователя успешно получены"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @Transactional
    public List<CourseRating> getUserRatings(@Schema(description = "ID пользователя", example = "16") int userId) {
        return courseRatingRepository.findByUserUserId(userId);
    }

    @Schema(description = "Получить оценки за курс")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Оценки за курс успешно получены"),
            @ApiResponse(responseCode = "404", description = "Курс не найден")
    })
    @Transactional
    public List<CourseRating> getCourseRatings(@Schema(description = "ID курса", example = "43") int courseId) {
        return courseRatingRepository.findByCourseCourseId(courseId);
    }

    @Schema(description = "Получить среднюю оценку за курс")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Средняя Оценка за курс успешно получена"),
            @ApiResponse(responseCode = "404", description = "Курс не найден")
    })
    @Transactional
    public Double getAverageRating(@Schema(description = "ID курса", example = "7") int courseId) {
        List<CourseRating> ratings = courseRatingRepository.findByCourseCourseId(courseId);
        if (ratings.isEmpty()) {
            return null;
        }
        double sum = ratings.stream().mapToDouble(CourseRating::getValue).sum();
        double average = sum / ratings.size();
        return Math.round(average * 10.0) / 10.0;
    }
}

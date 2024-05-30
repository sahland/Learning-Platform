package com.knitwit.api.v1.dto.mapper;

import com.knitwit.api.v1.dto.response.CourseRatingResponse;
import com.knitwit.model.CourseRating;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CourseRatingMapper {

    public CourseRatingResponse toResponse(CourseRating rating) {
        CourseRatingResponse response = new CourseRatingResponse();
        response.setRatingId(rating.getRatingId());
        response.setUserId(rating.getUser().getUserId());
        response.setCourseId(rating.getCourse().getCourseId());
        response.setValue(rating.getValue());
        return response;
    }

    public List<CourseRatingResponse> toResponseList(List<CourseRating> ratings) {
        return ratings.stream().map(this::toResponse).collect(Collectors.toList());
    }
}

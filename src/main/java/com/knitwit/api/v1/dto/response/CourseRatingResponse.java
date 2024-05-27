package com.knitwit.api.v1.dto.response;

import lombok.Data;

@Data
public class CourseRatingResponse {
    private int ratingId;
    private int userId;
    private int courseId;
    private int value;
}

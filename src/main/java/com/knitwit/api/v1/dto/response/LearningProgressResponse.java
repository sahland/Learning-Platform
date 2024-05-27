package com.knitwit.api.v1.dto.response;

import lombok.Data;

@Data
public class LearningProgressResponse {
    private int progressId;
    private int userId;
    private int sectionId;
    private boolean completed;
}

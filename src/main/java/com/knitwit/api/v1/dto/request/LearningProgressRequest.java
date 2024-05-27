package com.knitwit.api.v1.dto.request;

import lombok.Data;

@Data
public class LearningProgressRequest {
    private int sectionId;
    private boolean completed;
}

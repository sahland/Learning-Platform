package com.knitwit.enums;

import lombok.Getter;

@Getter
public enum RatingValue {
    ONE_STAR("1"),
    TWO_STARS("2"),
    THREE_STARS("3"),
    FOUR_STARS("4"),
    FIVE_STARS("5");

    private final String value;

    RatingValue(String value) {
        this.value = value;
    }

}

package com.textilflow.platform.configuration.domain.model.valueobjects;

import java.util.Arrays;

/**
 * Language enumeration with string conversion
 */
public enum Language {
    ES("es"),
    EN("en");

    private final String value;

    Language(String value) {
        this.value = value;
    }

    public static Language fromString(String value) {
        return Arrays.stream(values())
                .filter(lang -> lang.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid language: " + value));
    }

    public String getValue() {
        return value;
    }
}

package com.textilflow.platform.configuration.domain.model.valueobjects;

import java.util.Arrays;

/**
 * View mode enumeration with string conversion
 */
public enum ViewMode {
    LIGHT("light"),
    DARK("dark"),
    AUTO("auto");

    private final String value;

    ViewMode(String value) {
        this.value = value;
    }

    public static ViewMode fromString(String value) {
        return Arrays.stream(values())
                .filter(mode -> mode.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid view mode: " + value));
    }

    public String getValue() {
        return value;
    }
}

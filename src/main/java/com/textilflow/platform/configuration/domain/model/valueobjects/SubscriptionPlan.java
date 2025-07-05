package com.textilflow.platform.configuration.domain.model.valueobjects;

import java.util.Arrays;

/**
 * Subscription plan enumeration with string conversion
 */
public enum SubscriptionPlan {
    BASIC("basic"),
    CORPORATE("corporate");

    private final String value;

    SubscriptionPlan(String value) {
        this.value = value;
    }

    public static SubscriptionPlan fromString(String value) {
        return Arrays.stream(values())
                .filter(plan -> plan.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid subscription plan: " + value));
    }

    public String getValue() {
        return value;
    }
}

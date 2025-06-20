package com.textilflow.platform.batches.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record CheckId(Long checkId) {

    public CheckId {
        if (checkId == null) {
            throw new IllegalArgumentException("CheckId cannot be null");
        }
    }
}

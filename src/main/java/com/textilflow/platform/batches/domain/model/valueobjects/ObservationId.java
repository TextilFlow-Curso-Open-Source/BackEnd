package com.textilflow.platform.batches.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record ObservationId(Long observationId) {

    public ObservationId {
        if (observationId == null) {
            throw new IllegalArgumentException("ObservationId cannot be null");
        }
    }
}

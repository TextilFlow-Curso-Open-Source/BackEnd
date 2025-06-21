package com.textilflow.platform.profiles.domain.model.commands;

public record UpdateObservationCommand(
        Long observationId,
        String reason,
        String imageUrl,
        String status
) {}

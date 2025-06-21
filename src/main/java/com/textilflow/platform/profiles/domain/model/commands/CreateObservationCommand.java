package com.textilflow.platform.profiles.domain.model.commands;

public record CreateObservationCommand(
        Long batchId,
        String batchCode,
        Long businessmanId,
        Long supplierId,
        String reason,
        String imageUrl,
        String status
) {}
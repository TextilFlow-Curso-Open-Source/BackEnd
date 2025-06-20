package com.textilflow.platform.batches.domain.model.events;

import java.time.LocalDate;

public record BatchUpdatedEvent(
        Long batchId,
        LocalDate productionDate,
        Boolean qualityStatus,
        LocalDate creationDate,
        String productName,
        Float quantity,
        Integer storageCondition,
        String unitOfMeasure
) {}

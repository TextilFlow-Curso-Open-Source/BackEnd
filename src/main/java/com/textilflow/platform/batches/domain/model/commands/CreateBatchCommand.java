package com.textilflow.platform.batches.domain.model.commands;

import java.time.LocalDate;

public record CreateBatchCommand(LocalDate productionDate, Boolean qualityStatus, LocalDate creationDate, String productName, Float quantity, Integer storageCondition, String unitOfMeasure) {

    public CreateBatchCommand {
        if (productionDate == null) {
            throw new IllegalArgumentException("Production date cannot be null");
        }
        if (productionDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Production date cannot be in the future");
        }
        if (qualityStatus == null) {
            throw new IllegalArgumentException("Quality status cannot be null");
        }
        if (creationDate == null) {
            throw new IllegalArgumentException("Creation date cannot be null");
        }
        if (creationDate.isBefore(productionDate)) {
            throw new IllegalArgumentException("Creation date cannot be before production date");
        }
        if (productName == null || productName.isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity cannot be null or less than or equal to zero");
        }
        if (storageCondition == null || storageCondition < 0 || storageCondition > 100) {
            throw new IllegalArgumentException("Storage condition cannot be null or less than zero or greater than 100");
        }
        if (unitOfMeasure == null || unitOfMeasure.isEmpty()) {
            throw new IllegalArgumentException("Unit of measure cannot be null or empty");
        }
    }
}

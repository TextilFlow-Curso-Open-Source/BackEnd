package com.textilflow.platform.batches.domain.model.aggregates;

import com.textilflow.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.textilflow.platform.batches.domain.model.commands.CreateBatchCommand;
import io.jsonwebtoken.lang.Strings;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Entity
public class Batch extends AuditableAbstractAggregateRoot<Batch> {

    private LocalDate productionDate;
    private Boolean qualityStatus;
    private LocalDate creationDate;
    private String productName;
    private Float quantity;
    private Integer storageCondition;
    private String unitOfMeasure;




    public Batch() {
        this.productionDate = LocalDate.now();
        this.qualityStatus = true;
        this.creationDate = LocalDate.now();
        this.productName = Strings.EMPTY;
        this.quantity = 0.0f;
        this.storageCondition = 0;
        this.unitOfMeasure = Strings.EMPTY;
    }

    public Batch updateInformation(LocalDate productionDate, Boolean qualityStatus, LocalDate creationDate, 
                                  String productName, Float quantity, Integer storageCondition, String unitOfMeasure) {
        this.productionDate = productionDate;
        this.qualityStatus = qualityStatus;
        this.creationDate = creationDate;
        this.productName = productName;
        this.quantity = quantity;
        this.storageCondition = storageCondition;
        this.unitOfMeasure = unitOfMeasure;
        return this;
    }

    public Batch(CreateBatchCommand command) {
        this.productionDate = command.productionDate();
        this.qualityStatus = command.qualityStatus();
        this.creationDate = command.creationDate();
        this.productName = command.productName();
        this.quantity = command.quantity();
        this.storageCondition = command.storageCondition();
        this.unitOfMeasure = command.unitOfMeasure();
    }


}

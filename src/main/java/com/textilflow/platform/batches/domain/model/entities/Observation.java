package com.textilflow.platform.batches.domain.model.entities;

import com.textilflow.platform.batches.domain.model.aggregates.Batch;
import com.textilflow.platform.shared.domain.model.entities.AuditableModel;
import com.textilflow.platform.batches.domain.model.valueobjects.ObservationId;
import io.jsonwebtoken.lang.Strings;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Entity
public class Observation extends AuditableModel {
    @ManyToOne
    @JoinColumn(name = "batch_id")
    @NotNull
    private Batch batch;

    @NotNull
    @Embedded
    @Column(name = "observation_id")
    private ObservationId observationId;

    private String observation_te;
    private LocalDate observation_d;
    private String severity;
    private Boolean resolved;
    private LocalDate resolution_date;


    public Observation(Batch batch, ObservationId observationId, String observation_te,
                       LocalDate observation_d, String severity, Boolean resolved, LocalDate resolution_date) {
        this.batch = batch;
        this.observationId = observationId;
        this.observation_te = observation_te;
        this.observation_d = observation_d;
        this.severity = severity;
        this.resolved = resolved;
        this.resolution_date = resolution_date;
    }

    public Observation() {
        this.observationId = new ObservationId(0L);
        this.observation_te = Strings.EMPTY;
        this.observation_d = LocalDate.now();
        this.severity = Strings.EMPTY;
        this.resolved = false;
        this.resolution_date = LocalDate.now();
    }



}

package com.textilflow.platform.batches.domain.model.entities;


import com.textilflow.platform.batches.domain.model.aggregates.Batch;
import com.textilflow.platform.batches.domain.model.valueobjects.CheckId;
import com.textilflow.platform.shared.domain.model.entities.AuditableModel;
import io.jsonwebtoken.lang.Strings;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Entity
public class BatchQualityCheck extends AuditableModel {

    @ManyToOne
    @JoinColumn(name = "batch_id")
    @NotNull
    private Batch batch;

    @NotNull
    @Embedded
    @Column(name = "check_id")
    private CheckId checkId;

    private LocalDate check_date;
    private String check_type;
    private String results;
    private Boolean passed;
    private Integer comments;

    public BatchQualityCheck(Batch batch, CheckId checkId, LocalDate check_date, String check_type, String results, Boolean passed, Integer comments) {
        this.batch = batch;
        this.checkId = checkId;
        this.check_date = check_date;
        this.check_type = check_type;
        this.results = results;
        this.passed = passed;
        this.comments = comments;
    }

    public BatchQualityCheck() {
        this.checkId = new CheckId(0L);
        this.check_date = LocalDate.now();
        this.check_type = Strings.EMPTY;
        this.results = Strings.EMPTY;
        this.passed = false;
        this.comments = 0;
    }


}

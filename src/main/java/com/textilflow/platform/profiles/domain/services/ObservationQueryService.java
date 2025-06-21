package com.textilflow.platform.profiles.domain.services;

import com.textilflow.platform.profiles.domain.model.aggregates.Observation;
import com.textilflow.platform.profiles.domain.model.queries.GetObservationByIdQuery;
import com.textilflow.platform.profiles.domain.model.queries.GetObservationsByBatchIdQuery;
import com.textilflow.platform.profiles.domain.model.queries.GetObservationsByBusinessmanIdQuery;
import com.textilflow.platform.profiles.domain.model.queries.GetObservationsBySupplierIdQuery;

import java.util.List;
import java.util.Optional;

public interface ObservationQueryService {
    Optional<Observation> handle(GetObservationByIdQuery query);
    List<Observation> handle(GetObservationsByBatchIdQuery query);
    List<Observation> handle(GetObservationsByBusinessmanIdQuery query);
    List<Observation> handle(GetObservationsBySupplierIdQuery query);
}

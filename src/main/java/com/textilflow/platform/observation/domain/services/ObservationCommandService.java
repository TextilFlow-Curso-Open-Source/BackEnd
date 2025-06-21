package com.textilflow.platform.observation.domain.services;

import com.textilflow.platform.observation.domain.model.aggregates.Observation;
import com.textilflow.platform.observation.domain.model.commands.CreateObservationCommand;
import com.textilflow.platform.observation.domain.model.commands.UpdateObservationCommand;
import com.textilflow.platform.observation.domain.model.commands.DeleteObservationCommand;

import java.util.Optional;

public interface ObservationCommandService {
    Optional<Observation> handle(CreateObservationCommand command);
    Optional<Observation> handle(UpdateObservationCommand command);
    void handle(DeleteObservationCommand command);
}
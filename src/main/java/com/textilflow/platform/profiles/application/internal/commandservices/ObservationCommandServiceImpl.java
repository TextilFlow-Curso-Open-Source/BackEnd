package com.textilflow.platform.profiles.application.internal.commandservices;

import com.textilflow.platform.profiles.domain.model.aggregates.Observation;
import com.textilflow.platform.profiles.domain.model.commands.CreateObservationCommand;
import com.textilflow.platform.profiles.domain.model.commands.UpdateObservationCommand;
import com.textilflow.platform.profiles.domain.model.commands.DeleteObservationCommand;
import com.textilflow.platform.profiles.domain.model.valueobjects.BatchCode;
import com.textilflow.platform.profiles.domain.model.valueobjects.ImageUrl;
import com.textilflow.platform.profiles.domain.model.valueobjects.ObservationStatus;
import com.textilflow.platform.profiles.domain.services.ObservationCommandService;
import com.textilflow.platform.profiles.infrastructure.persistence.repositories.ObservationRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ObservationCommandServiceImpl implements ObservationCommandService {

    private final ObservationRepository observationRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ObservationCommandServiceImpl(ObservationRepository observationRepository,
                                         ApplicationEventPublisher eventPublisher) {
        this.observationRepository = observationRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<Observation> handle(CreateObservationCommand command) {
        try {
            var observation = new Observation(
                    command.batchId(),
                    new BatchCode(command.batchCode()),
                    command.businessmanId(),
                    command.supplierId(),
                    command.reason(),
                    new ImageUrl(command.imageUrl()),
                    ObservationStatus.valueOf(command.status().toUpperCase())
            );

            var savedObservation = observationRepository.save(observation);
            return Optional.of(savedObservation);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Observation> handle(UpdateObservationCommand command) {
        return observationRepository.findById(command.observationId())
                .map(observation -> {
                    observation.updateInformation(command);
                    return observationRepository.save(observation);
                });
    }

    @Override
    public void handle(DeleteObservationCommand command) {
        observationRepository.deleteById(command.observationId());
    }
}

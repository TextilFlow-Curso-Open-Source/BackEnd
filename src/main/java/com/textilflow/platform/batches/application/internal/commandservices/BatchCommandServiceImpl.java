package com.textilflow.platform.batches.application.internal.commandservices;

import com.textilflow.platform.batches.domain.model.aggregates.Batch;
import com.textilflow.platform.batches.domain.model.commands.CreateBatchCommand;
import com.textilflow.platform.batches.domain.model.commands.DeleteBatchCommand;
import com.textilflow.platform.batches.domain.model.commands.UpdateBatchCommand;
import com.textilflow.platform.batches.domain.model.events.BatchCreatedEvent;
import com.textilflow.platform.batches.domain.model.events.BatchUpdatedEvent;
import com.textilflow.platform.batches.domain.services.BatchCommandService;
import com.textilflow.platform.batches.infraestructure.persistence.repositories.BatchRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Batch command service implementation
 */
@Service
public class BatchCommandServiceImpl implements BatchCommandService {

    private final BatchRepository batchRepository;
    private final ApplicationEventPublisher eventPublisher;

    public BatchCommandServiceImpl(BatchRepository batchRepository,
                                   ApplicationEventPublisher eventPublisher) {
        this.batchRepository = batchRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Long handle(CreateBatchCommand command) {
        // Check if batch already exists with same production date
        if (batchRepository.existsByProductionDate(command.productionDate())) {
            throw new RuntimeException("Batch with production date already exists");
        }

        var batch = new Batch(command);
        var savedBatch = batchRepository.save(batch);

        // Publish domain event
        var event = new BatchCreatedEvent(
        );
        eventPublisher.publishEvent(event);

        return savedBatch.getId();
    }

    @Override
    public Optional<Batch> handle(UpdateBatchCommand command) {
        var batch = batchRepository.findById(command.batchId());
        if (batch.isEmpty()) {
            throw new RuntimeException("Batch not found");
        }

        // Check if another batch exists with same production date
        if (batchRepository.existsByProductionDateAndIdIsNot(command.productionDate(), command.batchId())) {
            throw new RuntimeException("Another batch with this production date already exists");
        }

        // Update batch information
        batch.get().updateInformation(
                command.productionDate(),
                command.qualityStatus(),
                command.creationDate(),
                command.productName(),
                command.quantity(),
                command.storageCondition(),
                command.unitOfMeasure()

        );

        var updatedBatch = batchRepository.save(batch.get());

        // Publish domain event
        var event = new BatchUpdatedEvent(
                updatedBatch.getId(),
                command.productionDate(),
                command.qualityStatus(),
                command.creationDate(),
                command.productName(),
                command.quantity(),
                command.storageCondition(),
                command.unitOfMeasure()
        );
        eventPublisher.publishEvent(event);

        return Optional.of(updatedBatch);
    }

    @Override
    public void handle(DeleteBatchCommand command) {
        if (!batchRepository.existsById(command.batchId())) {
            throw new IllegalArgumentException("Batch with id %s is not found".formatted(command.batchId()));
        }
        try {
            batchRepository.deleteById(command.batchId());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while deleting batch: %s".formatted(e.getMessage()));
        }
    }



}

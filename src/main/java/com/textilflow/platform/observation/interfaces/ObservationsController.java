package com.textilflow.platform.observation.interfaces;

import com.textilflow.platform.observation.domain.model.commands.CreateObservationCommand;
import com.textilflow.platform.observation.domain.model.commands.UpdateObservationCommand;
import com.textilflow.platform.observation.domain.model.commands.DeleteObservationCommand;
import com.textilflow.platform.observation.domain.model.queries.GetObservationByIdQuery;
import com.textilflow.platform.observation.domain.model.queries.GetObservationsByBatchIdQuery;
import com.textilflow.platform.observation.domain.model.queries.GetObservationsByBusinessmanIdQuery;
import com.textilflow.platform.observation.domain.model.queries.GetObservationsBySupplierIdQuery;
import com.textilflow.platform.observation.domain.services.ObservationCommandService;
import com.textilflow.platform.observation.domain.services.ObservationQueryService;
import com.textilflow.platform.observation.interfaces.resources.CreateObservationResource;
import com.textilflow.platform.observation.interfaces.resources.ObservationResource;
import com.textilflow.platform.observation.interfaces.resources.UpdateObservationResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/observations")
@Tag(name = "Observations", description = "Observation Management Endpoints")
public class ObservationsController {

    private final ObservationCommandService observationCommandService;
    private final ObservationQueryService observationQueryService;

    public ObservationsController(ObservationCommandService observationCommandService,
                                  ObservationQueryService observationQueryService) {
        this.observationCommandService = observationCommandService;
        this.observationQueryService = observationQueryService;
    }

    @PostMapping
    public ResponseEntity<ObservationResource> createObservation(@RequestBody CreateObservationResource resource) {
        var command = new CreateObservationCommand(
                resource.batchId(),
                resource.batchCode(),
                resource.businessmanId(),
                resource.supplierId(),
                resource.reason(),
                resource.imageUrl(),
                resource.status()
        );

        var observation = observationCommandService.handle(command);
        if (observation.isPresent()) {
            var observationResource = ObservationResource.fromEntity(observation.get());
            return new ResponseEntity<>(observationResource, HttpStatus.CREATED);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{observationId}")
    public ResponseEntity<ObservationResource> getObservationById(@PathVariable Long observationId) {
        var query = new GetObservationByIdQuery(observationId);
        var observation = observationQueryService.handle(query);

        if (observation.isPresent()) {
            var observationResource = ObservationResource.fromEntity(observation.get());
            return ResponseEntity.ok(observationResource);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/batch/{batchId}")
    public ResponseEntity<List<ObservationResource>> getObservationsByBatch(@PathVariable Long batchId) {
        var query = new GetObservationsByBatchIdQuery(batchId);
        var observations = observationQueryService.handle(query);
        var resources = observations.stream()
                .map(ObservationResource::fromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/businessman/{businessmanId}")
    public ResponseEntity<List<ObservationResource>> getObservationsByBusinessman(@PathVariable Long businessmanId) {
        var query = new GetObservationsByBusinessmanIdQuery(businessmanId);
        var observations = observationQueryService.handle(query);
        var resources = observations.stream()
                .map(ObservationResource::fromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<ObservationResource>> getObservationsBySupplier(@PathVariable Long supplierId) {
        var query = new GetObservationsBySupplierIdQuery(supplierId);
        var observations = observationQueryService.handle(query);
        var resources = observations.stream()
                .map(ObservationResource::fromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @PutMapping("/{observationId}")
    public ResponseEntity<ObservationResource> updateObservation(
            @PathVariable Long observationId,
            @RequestBody UpdateObservationResource resource) {

        var command = new UpdateObservationCommand(
                observationId,
                resource.reason(),
                resource.imageUrl(),
                resource.status()
        );

        var observation = observationCommandService.handle(command);
        if (observation.isPresent()) {
            var observationResource = ObservationResource.fromEntity(observation.get());
            return ResponseEntity.ok(observationResource);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{observationId}")
    public ResponseEntity<Void> deleteObservation(@PathVariable Long observationId) {
        var command = new DeleteObservationCommand(observationId);
        observationCommandService.handle(command);
        return ResponseEntity.noContent().build();
    }
}

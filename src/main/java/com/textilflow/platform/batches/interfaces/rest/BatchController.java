package com.textilflow.platform.batches.interfaces.rest;

import com.textilflow.platform.batches.domain.model.commands.CreateBatchCommand;
import com.textilflow.platform.batches.domain.model.commands.DeleteBatchCommand;
import com.textilflow.platform.batches.domain.model.commands.UpdateBatchCommand;
import com.textilflow.platform.batches.domain.model.queries.GetAllBatchesQuery;
import com.textilflow.platform.batches.domain.model.queries.GetBatchByIdQuery;
import com.textilflow.platform.batches.domain.services.BatchCommandService;
import com.textilflow.platform.batches.domain.services.BatchQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/batches", produces = "application/json")
@Tag(name = "Batches", description = "Batch Management Endpoints")
public class BatchController {
    private final BatchCommandService batchCommandService;
    private final BatchQueryService batchQueryService;

    public BatchController(BatchCommandService batchCommandService, BatchQueryService batchQueryService) {
        this.batchCommandService = batchCommandService;
        this.batchQueryService = batchQueryService;
    }

    @PostMapping
    @Operation(summary = "Create a new batch", description = "Creates a new production batch with specified details")
    public ResponseEntity<?> createBatch(@RequestBody CreateBatchCommand command) {
        var batchId = batchCommandService.handle(command);
        return ResponseEntity.ok(batchId);
    }

    @GetMapping
    @Operation(summary = "Get all batches", description = "Retrieves a list of all production batches")
    public ResponseEntity<?> getAllBatches() {
        var batches = batchQueryService.handle(new GetAllBatchesQuery());
        return ResponseEntity.ok(batches);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get batch by ID", description = "Retrieves a specific batch by its unique identifier")
    public ResponseEntity<?> getBatchById(@PathVariable Long id) {
        var batch = batchQueryService.handle(new GetBatchByIdQuery(id));
        return ResponseEntity.ok(batch);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update batch", description = "Updates an existing batch with new information")
    public ResponseEntity<?> updateBatch(@PathVariable Long id, @RequestBody UpdateBatchCommand command) {
        var updateCommand = new UpdateBatchCommand(id, command.productionDate(), command.qualityStatus(), command.creationDate(), command.productName(),command.quantity(),command.storageCondition(),command.unitOfMeasure());
        var updatedBatch = batchCommandService.handle(updateCommand);
        return ResponseEntity.ok(updatedBatch);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete batch", description = "Removes a batch from the system permanently")
    public ResponseEntity<?> deleteBatch(@PathVariable Long id) {
        batchCommandService.handle(new DeleteBatchCommand(id));
        return ResponseEntity.ok("Batch deleted successfully");
    }
}
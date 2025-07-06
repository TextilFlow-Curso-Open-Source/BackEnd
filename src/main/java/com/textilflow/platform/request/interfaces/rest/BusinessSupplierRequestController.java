package com.textilflow.platform.request.interfaces.rest;

import com.textilflow.platform.request.domain.model.commands.DeleteRequestCommand;
import com.textilflow.platform.request.domain.model.queries.*;
import com.textilflow.platform.request.domain.services.RequestCommandService;
import com.textilflow.platform.request.domain.services.RequestQueryService;
import com.textilflow.platform.request.interfaces.rest.resources.*;
import com.textilflow.platform.request.interfaces.rest.transform.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/business-supplier-requests")
@Tag(name = "Business Supplier Requests", description = "Business Supplier Request Management Endpoints")
public class BusinessSupplierRequestController {

    private final RequestCommandService requestCommandService;
    private final RequestQueryService requestQueryService;

    public BusinessSupplierRequestController(RequestCommandService requestCommandService,
                                             RequestQueryService requestQueryService) {
        this.requestCommandService = requestCommandService;
        this.requestQueryService = requestQueryService;
    }

    @PostMapping
    public ResponseEntity<BusinessSupplierRequestResource> createRequest(@RequestBody CreateBusinessSupplierRequestResource resource) {
        var command = CreateBusinessSupplierRequestCommandFromResourceAssembler.toCommandFromResource(resource);
        var requestId = requestCommandService.handle(command);

        if (requestId == null) {
            return ResponseEntity.badRequest().build();
        }

        var query = new GetRequestByIdQuery(requestId);
        var request = requestQueryService.handle(query);

        if (request.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var requestResource = BusinessSupplierRequestResourceFromEntityAssembler.toResourceFromEntity(request.get());
        return new ResponseEntity<>(requestResource, HttpStatus.CREATED);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<BusinessSupplierRequestResource> getRequestById(@PathVariable Long requestId) {
        var query = new GetRequestByIdQuery(requestId);
        var request = requestQueryService.handle(query);

        if (request.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var requestResource = BusinessSupplierRequestResourceFromEntityAssembler.toResourceFromEntity(request.get());
        return ResponseEntity.ok(requestResource);
    }

    @GetMapping
    public ResponseEntity<List<BusinessSupplierRequestResource>> getAllRequests() {
        var query = new GetAllRequestsQuery();
        var requests = requestQueryService.handle(query);

        var requestResources = requests.stream()
                .map(BusinessSupplierRequestResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(requestResources);
    }

    @GetMapping("/businessman/{businessmanId}")
    public ResponseEntity<List<BusinessSupplierRequestResource>> getRequestsByBusinessmanId(@PathVariable Long businessmanId) {
        var query = new GetRequestsByBusinessmanIdQuery(businessmanId);
        var requests = requestQueryService.handle(query);

        var requestResources = requests.stream()
                .map(BusinessSupplierRequestResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(requestResources);
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<BusinessSupplierRequestResource>> getRequestsBySupplierId(@PathVariable Long supplierId) {
        var query = new GetRequestsBySupplierIdQuery(supplierId);
        var requests = requestQueryService.handle(query);

        var requestResources = requests.stream()
                .map(BusinessSupplierRequestResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(requestResources);
    }

    @PutMapping("/{requestId}/status")
    public ResponseEntity<BusinessSupplierRequestResource> updateRequestStatus(
            @PathVariable Long requestId,
            @RequestBody UpdateRequestStatusResource resource) {

        var command = UpdateRequestStatusCommandFromResourceAssembler.toCommandFromResource(requestId, resource);
        var updatedRequest = requestCommandService.handle(command);

        if (updatedRequest.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var requestResource = BusinessSupplierRequestResourceFromEntityAssembler.toResourceFromEntity(updatedRequest.get());
        return ResponseEntity.ok(requestResource);
    }

    @PutMapping("/{requestId}/details")
    public ResponseEntity<BusinessSupplierRequestResource> updateRequestDetails(
            @PathVariable Long requestId,
            @RequestBody UpdateRequestDetailsResource resource) {

        var command = UpdateRequestDetailsCommandFromResourceAssembler.toCommandFromResource(requestId, resource);
        var updatedRequest = requestCommandService.handle(command);

        if (updatedRequest.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var requestResource = BusinessSupplierRequestResourceFromEntityAssembler.toResourceFromEntity(updatedRequest.get());
        return ResponseEntity.ok(requestResource);
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long requestId) {
        var command = new DeleteRequestCommand(requestId);
        requestCommandService.handle(command);
        return ResponseEntity.noContent().build();
    }
}

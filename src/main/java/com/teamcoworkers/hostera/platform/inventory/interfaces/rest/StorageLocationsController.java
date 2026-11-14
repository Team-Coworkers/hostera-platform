package com.teamcoworkers.hostera.platform.inventory.interfaces.rest;

import com.teamcoworkers.hostera.platform.inventory.application.commands.DeleteStorageLocationCommand;
import com.teamcoworkers.hostera.platform.inventory.application.commandservices.StorageLocationCommandService;
import com.teamcoworkers.hostera.platform.inventory.application.queries.GetAllStorageLocationsQuery;
import com.teamcoworkers.hostera.platform.inventory.application.queries.GetStorageLocationByIdQuery;
import com.teamcoworkers.hostera.platform.inventory.application.queryservices.StorageLocationQueryService;
import com.teamcoworkers.hostera.platform.inventory.interfaces.rest.resources.StorageLocationResource;
import com.teamcoworkers.hostera.platform.inventory.interfaces.rest.resources.SaveStorageLocationResource;
import com.teamcoworkers.hostera.platform.inventory.interfaces.rest.transform.StorageLocationResourceFromEntityAssembler;
import com.teamcoworkers.hostera.platform.shared.interfaces.rest.resources.MessageResource;
import com.teamcoworkers.hostera.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/storage-locations", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Storage Locations", description = "Places of a property where supplies are kept")
public class StorageLocationsController {
    private final StorageLocationCommandService commandService;
    private final StorageLocationQueryService queryService;

    public StorageLocationsController(StorageLocationCommandService commandService, StorageLocationQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "List the storage locations", description = "Returns every storage location, optionally filtered by propertyId, type.")
    @ApiResponse(responseCode = "200", description = "Storage locations retrieved")
    public ResponseEntity<List<StorageLocationResource>> getAll(
            @RequestParam(required = false) @Parameter(description = "Property that owns the storage locations", example = "1") Long propertyId,
            @RequestParam(required = false) @Parameter(description = "Type of the storage location: warehouse, closet, front-desk or service-area", example = "warehouse") String type) {
        var items = queryService.handle(new GetAllStorageLocationsQuery(propertyId, type)).stream()
                .map(StorageLocationResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{storageLocationId}")
    @Operation(summary = "Get a storage location by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Storage location found"),
            @ApiResponse(responseCode = "404", description = "Storage location not found")})
    public ResponseEntity<StorageLocationResource> getById(@PathVariable Long storageLocationId) {
        return queryService.handle(new GetStorageLocationByIdQuery(storageLocationId))
                .map(StorageLocationResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a storage location", description = "The code is unique in the property. The name, the area and the responsible team are required.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Storage location created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "A referenced resource does not exist"),
            @ApiResponse(responseCode = "409", description = "Conflict with an existing storage location"),
            @ApiResponse(responseCode = "422", description = "Business rule violation")})
    public ResponseEntity<?> create(@Valid @RequestBody SaveStorageLocationResource resource) {
        var result = commandService.handle(StorageLocationResourceFromEntityAssembler.toCreateCommand(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, StorageLocationResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{storageLocationId}")
    @Operation(summary = "Update a storage location", description = "Replaces the data of the storage location with the same rules as the creation.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Storage location updated"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Storage location not found"),
            @ApiResponse(responseCode = "409", description = "Conflict with an existing storage location"),
            @ApiResponse(responseCode = "422", description = "Business rule violation")})
    public ResponseEntity<?> update(@PathVariable Long storageLocationId, @Valid @RequestBody SaveStorageLocationResource resource) {
        var result = commandService.handle(StorageLocationResourceFromEntityAssembler.toUpdateCommand(storageLocationId, resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, StorageLocationResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @DeleteMapping("/{storageLocationId}")
    @Operation(summary = "Delete a storage location", description = "A storage location that is the primary location of an item, or that still holds stock, cannot be deleted.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Storage location deleted"),
            @ApiResponse(responseCode = "404", description = "Storage location not found"),
            @ApiResponse(responseCode = "422", description = "Business rule violation")})
    public ResponseEntity<?> delete(@PathVariable Long storageLocationId) {
        var result = commandService.handle(new DeleteStorageLocationCommand(storageLocationId))
                .map(_ -> new MessageResource("Storage location with given id successfully deleted"));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, message -> message, HttpStatus.OK);
    }
}

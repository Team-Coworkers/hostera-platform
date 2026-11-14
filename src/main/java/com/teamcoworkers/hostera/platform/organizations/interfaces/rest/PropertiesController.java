package com.teamcoworkers.hostera.platform.organizations.interfaces.rest;

import com.teamcoworkers.hostera.platform.organizations.application.commands.DeletePropertyCommand;
import com.teamcoworkers.hostera.platform.organizations.application.commandservices.PropertyCommandService;
import com.teamcoworkers.hostera.platform.organizations.application.queries.GetAllPropertiesQuery;
import com.teamcoworkers.hostera.platform.organizations.application.queries.GetPropertyByIdQuery;
import com.teamcoworkers.hostera.platform.organizations.application.queryservices.PropertyQueryService;
import com.teamcoworkers.hostera.platform.organizations.interfaces.rest.resources.PropertyResource;
import com.teamcoworkers.hostera.platform.organizations.interfaces.rest.resources.SavePropertyResource;
import com.teamcoworkers.hostera.platform.organizations.interfaces.rest.transform.PropertyResourceFromEntityAssembler;
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
@RequestMapping(value = "/api/v1/properties", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Properties", description = "Hotel properties of an organization")
public class PropertiesController {
    private final PropertyCommandService commandService;
    private final PropertyQueryService queryService;

    public PropertiesController(PropertyCommandService commandService, PropertyQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "List Propertys", description = "Returns every Property, optionally filtered by organizationId.")
    @ApiResponse(responseCode = "200", description = "Propertys retrieved")
    public ResponseEntity<List<PropertyResource>> getAll(
            @RequestParam(required = false) @Parameter(description = "Organization that owns the properties", example = "1") Long organizationId) {
        var items = queryService.handle(new GetAllPropertiesQuery(organizationId)).stream()
                .map(PropertyResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{propertyId}")
    @Operation(summary = "Get a Property by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Property found"),
            @ApiResponse(responseCode = "404", description = "Property not found")})
    public ResponseEntity<PropertyResource> getById(@PathVariable Long propertyId) {
        return queryService.handle(new GetPropertyByIdQuery(propertyId))
                .map(PropertyResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a Property")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Property created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "409", description = "Conflict with an existing Property")})
    public ResponseEntity<?> create(@Valid @RequestBody SavePropertyResource resource) {
        var result = commandService.handle(PropertyResourceFromEntityAssembler.toCreateCommand(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, PropertyResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{propertyId}")
    @Operation(summary = "Update a Property")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Property updated"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Property not found")})
    public ResponseEntity<?> update(@PathVariable Long propertyId, @Valid @RequestBody SavePropertyResource resource) {
        var result = commandService.handle(PropertyResourceFromEntityAssembler.toUpdateCommand(propertyId, resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, PropertyResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @DeleteMapping("/{propertyId}")
    @Operation(summary = "Delete a Property")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Property deleted"),
            @ApiResponse(responseCode = "404", description = "Property not found")})
    public ResponseEntity<?> delete(@PathVariable Long propertyId) {
        var result = commandService.handle(new DeletePropertyCommand(propertyId))
                .map(_ -> new MessageResource("Property with given id successfully deleted"));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, message -> message, HttpStatus.OK);
    }
}

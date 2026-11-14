package com.teamcoworkers.hostera.platform.rooms.interfaces.rest;

import com.teamcoworkers.hostera.platform.rooms.application.commands.DeleteStatusPeriodCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commandservices.StatusPeriodCommandService;
import com.teamcoworkers.hostera.platform.rooms.application.queries.GetAllStatusPeriodsQuery;
import com.teamcoworkers.hostera.platform.rooms.application.queries.GetStatusPeriodByIdQuery;
import com.teamcoworkers.hostera.platform.rooms.application.queryservices.StatusPeriodQueryService;
import com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources.StatusPeriodResource;
import com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources.SaveStatusPeriodResource;
import com.teamcoworkers.hostera.platform.rooms.interfaces.rest.transform.StatusPeriodResourceFromEntityAssembler;
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
@RequestMapping(value = "/api/v1/status-periods", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Status Periods", description = "Ranges of days in which a room is blocked, out of service or waiting for cleaning")
public class StatusPeriodsController {
    private final StatusPeriodCommandService commandService;
    private final StatusPeriodQueryService queryService;

    public StatusPeriodsController(StatusPeriodCommandService commandService, StatusPeriodQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "List the status periods", description = "Returns every status period, optionally filtered by propertyId, roomId, status.")
    @ApiResponse(responseCode = "200", description = "Status periods retrieved")
    public ResponseEntity<List<StatusPeriodResource>> getAll(
            @RequestParam(required = false) @Parameter(description = "Property that owns the status periods", example = "1") Long propertyId,
            @RequestParam(required = false) @Parameter(description = "Room of the status periods", example = "12") Long roomId,
            @RequestParam(required = false) @Parameter(description = "Operational status: blocked, out-of-service or needs-cleaning", example = "blocked") String status) {
        var items = queryService.handle(new GetAllStatusPeriodsQuery(propertyId, roomId, status)).stream()
                .map(StatusPeriodResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{statusPeriodId}")
    @Operation(summary = "Get a status period by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status period found"),
            @ApiResponse(responseCode = "404", description = "Status period not found")})
    public ResponseEntity<StatusPeriodResource> getById(@PathVariable Long statusPeriodId) {
        return queryService.handle(new GetStatusPeriodByIdQuery(statusPeriodId))
                .map(StatusPeriodResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a status period", description = "The end date is on or after the start date, the reason is required and the room belongs to the property.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Status period created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "A referenced resource does not exist"),
            @ApiResponse(responseCode = "409", description = "Conflict with an existing status period"),
            @ApiResponse(responseCode = "422", description = "Business rule violation")})
    public ResponseEntity<?> create(@Valid @RequestBody SaveStatusPeriodResource resource) {
        var result = commandService.handle(StatusPeriodResourceFromEntityAssembler.toCreateCommand(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, StatusPeriodResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{statusPeriodId}")
    @Operation(summary = "Update a status period", description = "Replaces the data of the status period with the same rules as the creation.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status period updated"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Status period not found"),
            @ApiResponse(responseCode = "409", description = "Conflict with an existing status period"),
            @ApiResponse(responseCode = "422", description = "Business rule violation")})
    public ResponseEntity<?> update(@PathVariable Long statusPeriodId, @Valid @RequestBody SaveStatusPeriodResource resource) {
        var result = commandService.handle(StatusPeriodResourceFromEntityAssembler.toUpdateCommand(statusPeriodId, resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, StatusPeriodResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @DeleteMapping("/{statusPeriodId}")
    @Operation(summary = "Delete a status period", description = "After the deletion, the room is available again on those days.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status period deleted"),
            @ApiResponse(responseCode = "404", description = "Status period not found"),
            @ApiResponse(responseCode = "422", description = "Business rule violation")})
    public ResponseEntity<?> delete(@PathVariable Long statusPeriodId) {
        var result = commandService.handle(new DeleteStatusPeriodCommand(statusPeriodId))
                .map(_ -> new MessageResource("Status period with given id successfully deleted"));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, message -> message, HttpStatus.OK);
    }
}

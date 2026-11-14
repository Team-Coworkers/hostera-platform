package com.teamcoworkers.hostera.platform.rooms.interfaces.rest;

import com.teamcoworkers.hostera.platform.rooms.application.commands.DeleteDailyRateCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commandservices.DailyRateCommandService;
import com.teamcoworkers.hostera.platform.rooms.application.queries.GetAllDailyRatesQuery;
import com.teamcoworkers.hostera.platform.rooms.application.queries.GetDailyRateByIdQuery;
import com.teamcoworkers.hostera.platform.rooms.application.queryservices.DailyRateQueryService;
import com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources.DailyRateResource;
import com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources.SaveDailyRateResource;
import com.teamcoworkers.hostera.platform.rooms.interfaces.rest.transform.DailyRateResourceFromEntityAssembler;
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
@RequestMapping(value = "/api/v1/daily-rates", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Daily Rates", description = "Price of one night of a room type under a rate plan")
public class DailyRatesController {
    private final DailyRateCommandService commandService;
    private final DailyRateQueryService queryService;

    public DailyRatesController(DailyRateCommandService commandService, DailyRateQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "List the daily rates", description = "Returns every daily rate, optionally filtered by propertyId, ratePlanId, roomTypeId, date.")
    @ApiResponse(responseCode = "200", description = "Daily rates retrieved")
    public ResponseEntity<List<DailyRateResource>> getAll(
            @RequestParam(required = false) @Parameter(description = "Property that owns the daily rates", example = "1") Long propertyId,
            @RequestParam(required = false) @Parameter(description = "Rate plan of the daily rates", example = "1") Long ratePlanId,
            @RequestParam(required = false) @Parameter(description = "Room type of the daily rates", example = "1") Long roomTypeId,
            @RequestParam(required = false) @Parameter(description = "Night that the daily rates price, as yyyy-MM-dd", example = "2026-10-01") String date) {
        var items = queryService.handle(new GetAllDailyRatesQuery(propertyId, ratePlanId, roomTypeId, date)).stream()
                .map(DailyRateResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{dailyRateId}")
    @Operation(summary = "Get a daily rate by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Daily rate found"),
            @ApiResponse(responseCode = "404", description = "Daily rate not found")})
    public ResponseEntity<DailyRateResource> getById(@PathVariable Long dailyRateId) {
        return queryService.handle(new GetDailyRateByIdQuery(dailyRateId))
                .map(DailyRateResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a daily rate", description = "The amount is greater than zero. The rate plan and the room type belong to the property, and a rate plan has only one price per room type and date.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Daily rate created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "A referenced resource does not exist"),
            @ApiResponse(responseCode = "409", description = "Conflict with an existing daily rate"),
            @ApiResponse(responseCode = "422", description = "Business rule violation")})
    public ResponseEntity<?> create(@Valid @RequestBody SaveDailyRateResource resource) {
        var result = commandService.handle(DailyRateResourceFromEntityAssembler.toCreateCommand(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, DailyRateResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{dailyRateId}")
    @Operation(summary = "Update a daily rate", description = "Replaces the data of the daily rate with the same rules as the creation.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Daily rate updated"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Daily rate not found"),
            @ApiResponse(responseCode = "409", description = "Conflict with an existing daily rate"),
            @ApiResponse(responseCode = "422", description = "Business rule violation")})
    public ResponseEntity<?> update(@PathVariable Long dailyRateId, @Valid @RequestBody SaveDailyRateResource resource) {
        var result = commandService.handle(DailyRateResourceFromEntityAssembler.toUpdateCommand(dailyRateId, resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, DailyRateResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @DeleteMapping("/{dailyRateId}")
    @Operation(summary = "Delete a daily rate", description = "After the deletion, the base nightly rate of the room type applies to that night.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Daily rate deleted"),
            @ApiResponse(responseCode = "404", description = "Daily rate not found"),
            @ApiResponse(responseCode = "422", description = "Business rule violation")})
    public ResponseEntity<?> delete(@PathVariable Long dailyRateId) {
        var result = commandService.handle(new DeleteDailyRateCommand(dailyRateId))
                .map(_ -> new MessageResource("Daily rate with given id successfully deleted"));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, message -> message, HttpStatus.OK);
    }
}

package com.teamcoworkers.hostera.platform.rooms.interfaces.rest;

import com.teamcoworkers.hostera.platform.rooms.application.commands.DeleteRatePlanCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commandservices.RatePlanCommandService;
import com.teamcoworkers.hostera.platform.rooms.application.queries.GetAllRatePlansQuery;
import com.teamcoworkers.hostera.platform.rooms.application.queries.GetRatePlanByIdQuery;
import com.teamcoworkers.hostera.platform.rooms.application.queryservices.RatePlanQueryService;
import com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources.RatePlanResource;
import com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources.SaveRatePlanResource;
import com.teamcoworkers.hostera.platform.rooms.interfaces.rest.transform.RatePlanResourceFromEntityAssembler;
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
@RequestMapping(value = "/api/v1/rate-plans", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Rate Plans", description = "Commercial conditions under which a property sells its room types")
public class RatePlansController {
    private final RatePlanCommandService commandService;
    private final RatePlanQueryService queryService;

    public RatePlansController(RatePlanCommandService commandService, RatePlanQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "List the rate plans", description = "Returns every rate plan, optionally filtered by propertyId, status.")
    @ApiResponse(responseCode = "200", description = "Rate plans retrieved")
    public ResponseEntity<List<RatePlanResource>> getAll(
            @RequestParam(required = false) @Parameter(description = "Property that owns the rate plans", example = "1") Long propertyId,
            @RequestParam(required = false) @Parameter(description = "Status of the rate plan: active or inactive", example = "active") String status) {
        var items = queryService.handle(new GetAllRatePlansQuery(propertyId, status)).stream()
                .map(RatePlanResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{ratePlanId}")
    @Operation(summary = "Get a rate plan by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rate plan found"),
            @ApiResponse(responseCode = "404", description = "Rate plan not found")})
    public ResponseEntity<RatePlanResource> getById(@PathVariable Long ratePlanId) {
        return queryService.handle(new GetRatePlanByIdQuery(ratePlanId))
                .map(RatePlanResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a rate plan", description = "The name is unique in the property. A refundable plan needs a cancellation policy. An empty roomTypeIds list means that the plan applies to every room type.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rate plan created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "A referenced resource does not exist"),
            @ApiResponse(responseCode = "409", description = "Conflict with an existing rate plan"),
            @ApiResponse(responseCode = "422", description = "Business rule violation")})
    public ResponseEntity<?> create(@Valid @RequestBody SaveRatePlanResource resource) {
        var result = commandService.handle(RatePlanResourceFromEntityAssembler.toCreateCommand(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, RatePlanResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{ratePlanId}")
    @Operation(summary = "Update a rate plan", description = "Replaces the data of the rate plan with the same rules as the creation.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rate plan updated"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Rate plan not found"),
            @ApiResponse(responseCode = "409", description = "Conflict with an existing rate plan"),
            @ApiResponse(responseCode = "422", description = "Business rule violation")})
    public ResponseEntity<?> update(@PathVariable Long ratePlanId, @Valid @RequestBody SaveRatePlanResource resource) {
        var result = commandService.handle(RatePlanResourceFromEntityAssembler.toUpdateCommand(ratePlanId, resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, RatePlanResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @DeleteMapping("/{ratePlanId}")
    @Operation(summary = "Delete a rate plan", description = "A rate plan that still has daily rates cannot be deleted.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rate plan deleted"),
            @ApiResponse(responseCode = "404", description = "Rate plan not found"),
            @ApiResponse(responseCode = "422", description = "Business rule violation")})
    public ResponseEntity<?> delete(@PathVariable Long ratePlanId) {
        var result = commandService.handle(new DeleteRatePlanCommand(ratePlanId))
                .map(_ -> new MessageResource("Rate plan with given id successfully deleted"));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, message -> message, HttpStatus.OK);
    }
}

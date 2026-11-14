package com.teamcoworkers.hostera.platform.accesscontrol.interfaces.rest;

import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.DeleteAccessEventCommand;
import com.teamcoworkers.hostera.platform.accesscontrol.application.commandservices.AccessEventCommandService;
import com.teamcoworkers.hostera.platform.accesscontrol.application.queries.GetAllAccessEventsQuery;
import com.teamcoworkers.hostera.platform.accesscontrol.application.queries.GetAccessEventByIdQuery;
import com.teamcoworkers.hostera.platform.accesscontrol.application.queryservices.AccessEventQueryService;
import com.teamcoworkers.hostera.platform.accesscontrol.interfaces.rest.resources.AccessEventResource;
import com.teamcoworkers.hostera.platform.accesscontrol.interfaces.rest.resources.SaveAccessEventResource;
import com.teamcoworkers.hostera.platform.accesscontrol.interfaces.rest.transform.AccessEventResourceFromEntityAssembler;
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
@RequestMapping(value = "/api/v1/access-events", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Access Events", description = "Attempts to open the readers of a property")
public class AccessEventsController {

    private final AccessEventCommandService commandService;
    private final AccessEventQueryService queryService;

    public AccessEventsController(AccessEventCommandService commandService, AccessEventQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "List Access events", description = "Returns every Access event, optionally filtered by the given parameters.")
    @ApiResponse(responseCode = "200", description = "Access events retrieved")
    public ResponseEntity<List<AccessEventResource>> getAll(
            @RequestParam(required = false) @Parameter(description = "Property of the attempts", example = "1") Long propertyId,
            @RequestParam(required = false) @Parameter(description = "Room of the attempts", example = "16") Long roomId,
            @RequestParam(required = false) @Parameter(description = "Booking whose key cards were used", example = "3") Long bookingId,
            @RequestParam(required = false) @Parameter(description = "Credential used", example = "7") Long credentialId) {
        var items = queryService.handle(new GetAllAccessEventsQuery(propertyId, roomId, bookingId, credentialId)).stream()
                .map(AccessEventResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{accessEventId}")
    @Operation(summary = "Get a Access event by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Access event found"),
            @ApiResponse(responseCode = "404", description = "Access event not found")})
    public ResponseEntity<AccessEventResource> getById(@PathVariable Long accessEventId) {
        return queryService.handle(new GetAccessEventByIdQuery(accessEventId))
                .map(AccessEventResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a Access event", description = "The result is decided by the credential: a non-active credential or another room is recorded as denied.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Access attempt recorded"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Credential not found")})
    public ResponseEntity<?> create(@Valid @RequestBody SaveAccessEventResource resource) {
        var result = commandService.handle(AccessEventResourceFromEntityAssembler.toCreateCommand(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, AccessEventResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{accessEventId}")
    @Operation(summary = "Update a Access event", description = "Replaces the attempt and evaluates it again.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Access attempt updated"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Access event or credential not found")})
    public ResponseEntity<?> update(@PathVariable Long accessEventId, @Valid @RequestBody SaveAccessEventResource resource) {
        var result = commandService.handle(AccessEventResourceFromEntityAssembler.toUpdateCommand(accessEventId, resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, AccessEventResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @DeleteMapping("/{accessEventId}")
    @Operation(summary = "Delete a Access event")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Access event deleted"),
            @ApiResponse(responseCode = "404", description = "Access event not found")})
    public ResponseEntity<?> delete(@PathVariable Long accessEventId) {
        var result = commandService.handle(new DeleteAccessEventCommand(accessEventId))
                .map(_ -> new MessageResource("Access event with given id successfully deleted"));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, message -> message, HttpStatus.OK);
    }
}

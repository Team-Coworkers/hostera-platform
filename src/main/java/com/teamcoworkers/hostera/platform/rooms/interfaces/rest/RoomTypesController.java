package com.teamcoworkers.hostera.platform.rooms.interfaces.rest;

import com.teamcoworkers.hostera.platform.rooms.application.commands.DeleteRoomTypeCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commandservices.RoomTypeCommandService;
import com.teamcoworkers.hostera.platform.rooms.application.queries.GetAllRoomTypesQuery;
import com.teamcoworkers.hostera.platform.rooms.application.queries.GetRoomTypeByIdQuery;
import com.teamcoworkers.hostera.platform.rooms.application.queryservices.RoomTypeQueryService;
import com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources.RoomTypeResource;
import com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources.SaveRoomTypeResource;
import com.teamcoworkers.hostera.platform.rooms.interfaces.rest.transform.RoomTypeResourceFromEntityAssembler;
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
@RequestMapping(value = "/api/v1/room-types", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Room Types", description = "Room categories of a property, with their capacity and base nightly rate")
public class RoomTypesController {
    private final RoomTypeCommandService commandService;
    private final RoomTypeQueryService queryService;

    public RoomTypesController(RoomTypeCommandService commandService, RoomTypeQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "List the room types", description = "Returns every room type, optionally filtered by propertyId, status.")
    @ApiResponse(responseCode = "200", description = "Room types retrieved")
    public ResponseEntity<List<RoomTypeResource>> getAll(
            @RequestParam(required = false) @Parameter(description = "Property that owns the room types", example = "1") Long propertyId,
            @RequestParam(required = false) @Parameter(description = "Status of the room type: active or inactive", example = "active") String status) {
        var items = queryService.handle(new GetAllRoomTypesQuery(propertyId, status)).stream()
                .map(RoomTypeResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{roomTypeId}")
    @Operation(summary = "Get a room type by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Room type found"),
            @ApiResponse(responseCode = "404", description = "Room type not found")})
    public ResponseEntity<RoomTypeResource> getById(@PathVariable Long roomTypeId) {
        return queryService.handle(new GetRoomTypeByIdQuery(roomTypeId))
                .map(RoomTypeResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a room type", description = "The name is unique in the property, the capacity goes from 1 to 12 guests and the base nightly rate is greater than zero.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Room type created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "A referenced resource does not exist"),
            @ApiResponse(responseCode = "409", description = "Conflict with an existing room type"),
            @ApiResponse(responseCode = "422", description = "Business rule violation")})
    public ResponseEntity<?> create(@Valid @RequestBody SaveRoomTypeResource resource) {
        var result = commandService.handle(RoomTypeResourceFromEntityAssembler.toCreateCommand(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, RoomTypeResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{roomTypeId}")
    @Operation(summary = "Update a room type", description = "Replaces the data of the room type with the same rules as the creation.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Room type updated"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Room type not found"),
            @ApiResponse(responseCode = "409", description = "Conflict with an existing room type"),
            @ApiResponse(responseCode = "422", description = "Business rule violation")})
    public ResponseEntity<?> update(@PathVariable Long roomTypeId, @Valid @RequestBody SaveRoomTypeResource resource) {
        var result = commandService.handle(RoomTypeResourceFromEntityAssembler.toUpdateCommand(roomTypeId, resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, RoomTypeResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @DeleteMapping("/{roomTypeId}")
    @Operation(summary = "Delete a room type", description = "A room type that still has rooms cannot be deleted.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Room type deleted"),
            @ApiResponse(responseCode = "404", description = "Room type not found"),
            @ApiResponse(responseCode = "422", description = "Business rule violation")})
    public ResponseEntity<?> delete(@PathVariable Long roomTypeId) {
        var result = commandService.handle(new DeleteRoomTypeCommand(roomTypeId))
                .map(_ -> new MessageResource("Room type with given id successfully deleted"));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, message -> message, HttpStatus.OK);
    }
}

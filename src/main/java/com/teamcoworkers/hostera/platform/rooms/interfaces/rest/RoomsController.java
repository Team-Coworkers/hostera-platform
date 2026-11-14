package com.teamcoworkers.hostera.platform.rooms.interfaces.rest;

import com.teamcoworkers.hostera.platform.rooms.application.commands.DeleteRoomCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commandservices.RoomCommandService;
import com.teamcoworkers.hostera.platform.rooms.application.queries.GetAllRoomsQuery;
import com.teamcoworkers.hostera.platform.rooms.application.queries.GetRoomByIdQuery;
import com.teamcoworkers.hostera.platform.rooms.application.queryservices.RoomQueryService;
import com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources.RoomResource;
import com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources.SaveRoomResource;
import com.teamcoworkers.hostera.platform.rooms.interfaces.rest.transform.RoomResourceFromEntityAssembler;
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
@RequestMapping(value = "/api/v1/rooms", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Rooms", description = "Rooms of a property, with their housekeeping and occupancy status")
public class RoomsController {
    private final RoomCommandService commandService;
    private final RoomQueryService queryService;

    public RoomsController(RoomCommandService commandService, RoomQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "List the rooms", description = "Returns every room, optionally filtered by propertyId, roomTypeId, status.")
    @ApiResponse(responseCode = "200", description = "Rooms retrieved")
    public ResponseEntity<List<RoomResource>> getAll(
            @RequestParam(required = false) @Parameter(description = "Property that owns the rooms", example = "1") Long propertyId,
            @RequestParam(required = false) @Parameter(description = "Room type of the rooms", example = "1") Long roomTypeId,
            @RequestParam(required = false) @Parameter(description = "Status of the rooms: available, ready, occupied, needs-cleaning or blocked", example = "ready") String status) {
        var items = queryService.handle(new GetAllRoomsQuery(propertyId, roomTypeId, status)).stream()
                .map(RoomResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{roomId}")
    @Operation(summary = "Get a room by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Room found"),
            @ApiResponse(responseCode = "404", description = "Room not found")})
    public ResponseEntity<RoomResource> getById(@PathVariable Long roomId) {
        return queryService.handle(new GetRoomByIdQuery(roomId))
                .map(RoomResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a room", description = "The number is unique in the property and the room type belongs to the same property. A new room starts as available unless a status is sent.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Room created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "A referenced resource does not exist"),
            @ApiResponse(responseCode = "409", description = "Conflict with an existing room"),
            @ApiResponse(responseCode = "422", description = "Business rule violation")})
    public ResponseEntity<?> create(@Valid @RequestBody SaveRoomResource resource) {
        var result = commandService.handle(RoomResourceFromEntityAssembler.toCreateCommand(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, RoomResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{roomId}")
    @Operation(summary = "Update a room", description = "Replaces the data of the room. When a status is sent it must be a valid transition: an occupied room can only change to needs-cleaning, and a room that needs cleaning can only change to ready or blocked.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Room updated"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Room not found"),
            @ApiResponse(responseCode = "409", description = "Conflict with an existing room"),
            @ApiResponse(responseCode = "422", description = "Business rule violation")})
    public ResponseEntity<?> update(@PathVariable Long roomId, @Valid @RequestBody SaveRoomResource resource) {
        var result = commandService.handle(RoomResourceFromEntityAssembler.toUpdateCommand(roomId, resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, RoomResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @DeleteMapping("/{roomId}")
    @Operation(summary = "Delete a room", description = "An occupied room cannot be deleted.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Room deleted"),
            @ApiResponse(responseCode = "404", description = "Room not found"),
            @ApiResponse(responseCode = "422", description = "Business rule violation")})
    public ResponseEntity<?> delete(@PathVariable Long roomId) {
        var result = commandService.handle(new DeleteRoomCommand(roomId))
                .map(_ -> new MessageResource("Room with given id successfully deleted"));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, message -> message, HttpStatus.OK);
    }
}

package com.teamcoworkers.hostera.platform.bookings.interfaces.rest;

import com.teamcoworkers.hostera.platform.bookings.application.commands.DeleteBookingCommand;
import com.teamcoworkers.hostera.platform.bookings.application.commandservices.BookingCommandService;
import com.teamcoworkers.hostera.platform.bookings.application.queries.GetAllBookingsQuery;
import com.teamcoworkers.hostera.platform.bookings.application.queries.GetBookingByIdQuery;
import com.teamcoworkers.hostera.platform.bookings.application.queryservices.BookingQueryService;
import com.teamcoworkers.hostera.platform.bookings.interfaces.rest.resources.BookingResource;
import com.teamcoworkers.hostera.platform.bookings.interfaces.rest.resources.SaveBookingResource;
import com.teamcoworkers.hostera.platform.bookings.interfaces.rest.transform.BookingResourceFromEntityAssembler;
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
@RequestMapping(value = "/api/v1/bookings", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Bookings", description = "Guest stays at each property")
public class BookingsController {

    private final BookingCommandService commandService;
    private final BookingQueryService queryService;

    public BookingsController(BookingCommandService commandService, BookingQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "List Bookings",
            description = "Returns every Booking, optionally filtered by propertyId, status and roomId. "
                    + "_sort, _order and _limit keep the JSON Server parameters the frontend uses.")
    @ApiResponse(responseCode = "200", description = "Bookings retrieved")
    public ResponseEntity<List<BookingResource>> getAll(
            @RequestParam(required = false) @Parameter(description = "Property of the bookings", example = "1") Long propertyId,
            @RequestParam(required = false) @Parameter(description = "Booking status", example = "confirmed") String status,
            @RequestParam(required = false) @Parameter(description = "Room held by the bookings", example = "12") Long roomId,
            @RequestParam(name = "_sort", required = false) @Parameter(description = "Field to sort by: code, checkInDate, createdAt or id", example = "code") String sort,
            @RequestParam(name = "_order", required = false) @Parameter(description = "asc or desc", example = "desc") String order,
            @RequestParam(name = "_limit", required = false) @Parameter(description = "Maximum number of bookings", example = "1") Integer limit) {
        var items = queryService.handle(new GetAllBookingsQuery(propertyId, status, roomId, sort, order, limit)).stream()
                .map(BookingResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{bookingId}")
    @Operation(summary = "Get a Booking by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking found"),
            @ApiResponse(responseCode = "404", description = "Booking not found")})
    public ResponseEntity<BookingResource> getById(@PathVariable Long bookingId) {
        return queryService.handle(new GetBookingByIdQuery(bookingId))
                .map(BookingResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a Booking",
            description = "Rejects a booking whose room is already held by another active booking for overlapping nights.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Booking created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Property not found"),
            @ApiResponse(responseCode = "409", description = "The room is already booked for overlapping nights")})
    public ResponseEntity<?> create(@Valid @RequestBody SaveBookingResource resource) {
        var result = commandService.handle(BookingResourceFromEntityAssembler.toCreateCommand(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, BookingResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{bookingId}")
    @Operation(summary = "Update a Booking",
            description = "Replaces the booking. Checking out or cancelling an active booking revokes its key cards.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking updated"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Booking or property not found"),
            @ApiResponse(responseCode = "409", description = "The room is already booked for overlapping nights")})
    public ResponseEntity<?> update(@PathVariable Long bookingId, @Valid @RequestBody SaveBookingResource resource) {
        var result = commandService.handle(BookingResourceFromEntityAssembler.toUpdateCommand(bookingId, resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, BookingResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @DeleteMapping("/{bookingId}")
    @Operation(summary = "Delete a Booking")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking deleted"),
            @ApiResponse(responseCode = "404", description = "Booking not found")})
    public ResponseEntity<?> delete(@PathVariable Long bookingId) {
        var result = commandService.handle(new DeleteBookingCommand(bookingId))
                .map(_ -> new MessageResource("Booking with given id successfully deleted"));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, message -> message, HttpStatus.OK);
    }
}

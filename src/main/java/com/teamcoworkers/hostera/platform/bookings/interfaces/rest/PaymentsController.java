package com.teamcoworkers.hostera.platform.bookings.interfaces.rest;

import com.teamcoworkers.hostera.platform.bookings.application.commands.DeletePaymentCommand;
import com.teamcoworkers.hostera.platform.bookings.application.commandservices.PaymentCommandService;
import com.teamcoworkers.hostera.platform.bookings.application.queries.GetAllPaymentsQuery;
import com.teamcoworkers.hostera.platform.bookings.application.queries.GetPaymentByIdQuery;
import com.teamcoworkers.hostera.platform.bookings.application.queryservices.PaymentQueryService;
import com.teamcoworkers.hostera.platform.bookings.interfaces.rest.resources.PaymentResource;
import com.teamcoworkers.hostera.platform.bookings.interfaces.rest.resources.SavePaymentResource;
import com.teamcoworkers.hostera.platform.bookings.interfaces.rest.transform.PaymentResourceFromEntityAssembler;
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
@RequestMapping(value = "/api/v1/payments", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Payments", description = "Money collected for each booking")
public class PaymentsController {

    private final PaymentCommandService commandService;
    private final PaymentQueryService queryService;

    public PaymentsController(PaymentCommandService commandService, PaymentQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "List Payments", description = "Returns every Payment, optionally filtered by propertyId and bookingId.")
    @ApiResponse(responseCode = "200", description = "Payments retrieved")
    public ResponseEntity<List<PaymentResource>> getAll(
            @RequestParam(required = false) @Parameter(description = "Property of the payments", example = "1") Long propertyId,
            @RequestParam(required = false) @Parameter(description = "Booking the payments belong to", example = "2") Long bookingId) {
        var items = queryService.handle(new GetAllPaymentsQuery(propertyId, bookingId)).stream()
                .map(PaymentResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get a Payment by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment found"),
            @ApiResponse(responseCode = "404", description = "Payment not found")})
    public ResponseEntity<PaymentResource> getById(@PathVariable Long paymentId) {
        return queryService.handle(new GetPaymentByIdQuery(paymentId))
                .map(PaymentResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Record a Payment")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment recorded"),
            @ApiResponse(responseCode = "400", description = "Invalid data or the booking belongs to another property"),
            @ApiResponse(responseCode = "404", description = "Property or booking not found")})
    public ResponseEntity<?> create(@Valid @RequestBody SavePaymentResource resource) {
        var result = commandService.handle(PaymentResourceFromEntityAssembler.toCreateCommand(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, PaymentResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{paymentId}")
    @Operation(summary = "Update a Payment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment updated"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Payment, property or booking not found")})
    public ResponseEntity<?> update(@PathVariable Long paymentId, @Valid @RequestBody SavePaymentResource resource) {
        var result = commandService.handle(PaymentResourceFromEntityAssembler.toUpdateCommand(paymentId, resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, PaymentResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @DeleteMapping("/{paymentId}")
    @Operation(summary = "Delete a Payment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment deleted"),
            @ApiResponse(responseCode = "404", description = "Payment not found")})
    public ResponseEntity<?> delete(@PathVariable Long paymentId) {
        var result = commandService.handle(new DeletePaymentCommand(paymentId))
                .map(_ -> new MessageResource("Payment with given id successfully deleted"));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, message -> message, HttpStatus.OK);
    }
}

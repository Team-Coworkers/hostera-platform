package com.teamcoworkers.hostera.platform.accesscontrol.interfaces.rest;

import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.DeleteCredentialCommand;
import com.teamcoworkers.hostera.platform.accesscontrol.application.commandservices.CredentialCommandService;
import com.teamcoworkers.hostera.platform.accesscontrol.application.queries.GetAllCredentialsQuery;
import com.teamcoworkers.hostera.platform.accesscontrol.application.queries.GetCredentialByIdQuery;
import com.teamcoworkers.hostera.platform.accesscontrol.application.queryservices.CredentialQueryService;
import com.teamcoworkers.hostera.platform.accesscontrol.interfaces.rest.resources.CredentialResource;
import com.teamcoworkers.hostera.platform.accesscontrol.interfaces.rest.resources.SaveCredentialResource;
import com.teamcoworkers.hostera.platform.accesscontrol.interfaces.rest.transform.CredentialResourceFromEntityAssembler;
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
@RequestMapping(value = "/api/v1/credentials", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Credentials", description = "Guest key cards and staff credentials")
public class CredentialsController {

    private final CredentialCommandService commandService;
    private final CredentialQueryService queryService;

    public CredentialsController(CredentialCommandService commandService, CredentialQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "List Credentials", description = "Returns every Credential, optionally filtered by the given parameters.")
    @ApiResponse(responseCode = "200", description = "Credentials retrieved")
    public ResponseEntity<List<CredentialResource>> getAll(
            @RequestParam(required = false) @Parameter(description = "Property of the credentials", example = "1") Long propertyId,
            @RequestParam(required = false) @Parameter(description = "Room a guest key card opens", example = "6") Long roomId,
            @RequestParam(required = false) @Parameter(description = "Booking of the guest key cards", example = "3") Long bookingId) {
        var items = queryService.handle(new GetAllCredentialsQuery(propertyId, roomId, bookingId)).stream()
                .map(CredentialResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{credentialId}")
    @Operation(summary = "Get a Credential by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credential found"),
            @ApiResponse(responseCode = "404", description = "Credential not found")})
    public ResponseEntity<CredentialResource> getById(@PathVariable Long credentialId) {
        return queryService.handle(new GetCredentialByIdQuery(credentialId))
                .map(CredentialResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a Credential", description = "A guest key card is only issued for the room of a checked-in booking and never for a blocked room.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Credential issued"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Property or booking not found"),
            @ApiResponse(responseCode = "422", description = "The room is not assigned to a checked-in booking of that guest, or it is blocked")})
    public ResponseEntity<?> create(@Valid @RequestBody SaveCredentialResource resource) {
        var result = commandService.handle(CredentialResourceFromEntityAssembler.toCreateCommand(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, CredentialResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{credentialId}")
    @Operation(summary = "Update a Credential", description = "Replaces the credential. A revoked credential stays revoked.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credential updated"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Credential or property not found")})
    public ResponseEntity<?> update(@PathVariable Long credentialId, @Valid @RequestBody SaveCredentialResource resource) {
        var result = commandService.handle(CredentialResourceFromEntityAssembler.toUpdateCommand(credentialId, resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, CredentialResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @DeleteMapping("/{credentialId}")
    @Operation(summary = "Delete a Credential")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credential deleted"),
            @ApiResponse(responseCode = "404", description = "Credential not found")})
    public ResponseEntity<?> delete(@PathVariable Long credentialId) {
        var result = commandService.handle(new DeleteCredentialCommand(credentialId))
                .map(_ -> new MessageResource("Credential with given id successfully deleted"));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, message -> message, HttpStatus.OK);
    }
}

package com.teamcoworkers.hostera.platform.organizations.interfaces.rest;

import com.teamcoworkers.hostera.platform.organizations.application.commands.DeleteStaffMemberCommand;
import com.teamcoworkers.hostera.platform.organizations.application.commandservices.StaffMemberCommandService;
import com.teamcoworkers.hostera.platform.organizations.application.queries.GetAllStaffMembersQuery;
import com.teamcoworkers.hostera.platform.organizations.application.queries.GetStaffMemberByIdQuery;
import com.teamcoworkers.hostera.platform.organizations.application.queryservices.StaffMemberQueryService;
import com.teamcoworkers.hostera.platform.organizations.interfaces.rest.resources.StaffMemberResource;
import com.teamcoworkers.hostera.platform.organizations.interfaces.rest.resources.SaveStaffMemberResource;
import com.teamcoworkers.hostera.platform.organizations.interfaces.rest.transform.StaffMemberResourceFromEntityAssembler;
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
@RequestMapping(value = "/api/v1/staff-members", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Staff Members", description = "Staff of each property")
public class StaffMembersController {
    private final StaffMemberCommandService commandService;
    private final StaffMemberQueryService queryService;

    public StaffMembersController(StaffMemberCommandService commandService, StaffMemberQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "List Staff members", description = "Returns every Staff member, optionally filtered by propertyId.")
    @ApiResponse(responseCode = "200", description = "Staff members retrieved")
    public ResponseEntity<List<StaffMemberResource>> getAll(
            @RequestParam(required = false) @Parameter(description = "Property where the staff members work", example = "1") Long propertyId) {
        var items = queryService.handle(new GetAllStaffMembersQuery(propertyId)).stream()
                .map(StaffMemberResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{staffMemberId}")
    @Operation(summary = "Get a Staff member by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Staff member found"),
            @ApiResponse(responseCode = "404", description = "Staff member not found")})
    public ResponseEntity<StaffMemberResource> getById(@PathVariable Long staffMemberId) {
        return queryService.handle(new GetStaffMemberByIdQuery(staffMemberId))
                .map(StaffMemberResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a Staff member")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Staff member created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "409", description = "Conflict with an existing Staff member")})
    public ResponseEntity<?> create(@Valid @RequestBody SaveStaffMemberResource resource) {
        var result = commandService.handle(StaffMemberResourceFromEntityAssembler.toCreateCommand(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, StaffMemberResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{staffMemberId}")
    @Operation(summary = "Update a Staff member")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Staff member updated"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Staff member not found")})
    public ResponseEntity<?> update(@PathVariable Long staffMemberId, @Valid @RequestBody SaveStaffMemberResource resource) {
        var result = commandService.handle(StaffMemberResourceFromEntityAssembler.toUpdateCommand(staffMemberId, resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, StaffMemberResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @DeleteMapping("/{staffMemberId}")
    @Operation(summary = "Delete a Staff member")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Staff member deleted"),
            @ApiResponse(responseCode = "404", description = "Staff member not found")})
    public ResponseEntity<?> delete(@PathVariable Long staffMemberId) {
        var result = commandService.handle(new DeleteStaffMemberCommand(staffMemberId))
                .map(_ -> new MessageResource("Staff member with given id successfully deleted"));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, message -> message, HttpStatus.OK);
    }
}

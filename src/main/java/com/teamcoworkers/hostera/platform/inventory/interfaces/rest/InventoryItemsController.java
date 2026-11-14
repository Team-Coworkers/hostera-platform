package com.teamcoworkers.hostera.platform.inventory.interfaces.rest;

import com.teamcoworkers.hostera.platform.inventory.application.commands.DeleteInventoryItemCommand;
import com.teamcoworkers.hostera.platform.inventory.application.commandservices.InventoryItemCommandService;
import com.teamcoworkers.hostera.platform.inventory.application.queries.GetAllInventoryItemsQuery;
import com.teamcoworkers.hostera.platform.inventory.application.queries.GetInventoryItemByIdQuery;
import com.teamcoworkers.hostera.platform.inventory.application.queryservices.InventoryItemQueryService;
import com.teamcoworkers.hostera.platform.inventory.interfaces.rest.resources.InventoryItemResource;
import com.teamcoworkers.hostera.platform.inventory.interfaces.rest.resources.SaveInventoryItemResource;
import com.teamcoworkers.hostera.platform.inventory.interfaces.rest.transform.InventoryItemResourceFromEntityAssembler;
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
@RequestMapping(value = "/api/v1/inventory-items", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Inventory Items", description = "Supplies of a property, with their stock per storage location and their stock adjustments")
public class InventoryItemsController {
    private final InventoryItemCommandService commandService;
    private final InventoryItemQueryService queryService;

    public InventoryItemsController(InventoryItemCommandService commandService, InventoryItemQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "List the inventory items", description = "Returns every inventory item, optionally filtered by propertyId, category.")
    @ApiResponse(responseCode = "200", description = "Inventory items retrieved")
    public ResponseEntity<List<InventoryItemResource>> getAll(
            @RequestParam(required = false) @Parameter(description = "Property that owns the inventory items", example = "1") Long propertyId,
            @RequestParam(required = false) @Parameter(description = "Category of the inventory items, for example linen", example = "linen") String category) {
        var items = queryService.handle(new GetAllInventoryItemsQuery(propertyId, category)).stream()
                .map(InventoryItemResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{inventoryItemId}")
    @Operation(summary = "Get a inventory item by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventory item found"),
            @ApiResponse(responseCode = "404", description = "Inventory item not found")})
    public ResponseEntity<InventoryItemResource> getById(@PathVariable Long inventoryItemId) {
        return queryService.handle(new GetInventoryItemByIdQuery(inventoryItemId))
                .map(InventoryItemResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a inventory item", description = "The code is unique in the property. The primary location and the locations of the stocks belong to the property, the quantities are never negative and an item counted in units has no decimals.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Inventory item created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "A referenced resource does not exist"),
            @ApiResponse(responseCode = "409", description = "Conflict with an existing inventory item"),
            @ApiResponse(responseCode = "422", description = "Business rule violation")})
    public ResponseEntity<?> create(@Valid @RequestBody SaveInventoryItemResource resource) {
        var result = commandService.handle(InventoryItemResourceFromEntityAssembler.toCreateCommand(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, InventoryItemResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{inventoryItemId}")
    @Operation(summary = "Update a inventory item", description = "Replaces the data, the stocks and the adjustments of the item. The adjustments that were already recorded cannot be removed: new ones are added at the end of the list. When stocks or adjustments are not sent, the item keeps the ones it has.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventory item updated"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Inventory item not found"),
            @ApiResponse(responseCode = "409", description = "Conflict with an existing inventory item"),
            @ApiResponse(responseCode = "422", description = "Business rule violation")})
    public ResponseEntity<?> update(@PathVariable Long inventoryItemId, @Valid @RequestBody SaveInventoryItemResource resource) {
        var result = commandService.handle(InventoryItemResourceFromEntityAssembler.toUpdateCommand(inventoryItemId, resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, InventoryItemResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @DeleteMapping("/{inventoryItemId}")
    @Operation(summary = "Delete a inventory item", description = "An item that still has stock cannot be deleted.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventory item deleted"),
            @ApiResponse(responseCode = "404", description = "Inventory item not found"),
            @ApiResponse(responseCode = "422", description = "Business rule violation")})
    public ResponseEntity<?> delete(@PathVariable Long inventoryItemId) {
        var result = commandService.handle(new DeleteInventoryItemCommand(inventoryItemId))
                .map(_ -> new MessageResource("Inventory item with given id successfully deleted"));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, message -> message, HttpStatus.OK);
    }
}

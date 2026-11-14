package com.teamcoworkers.hostera.platform.inventory.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "StorageLocation", description = "Place of a property where supplies are kept")
public record StorageLocationResource(
        @Schema(example = "1") Long id,
        @Schema(example = "1") Long propertyId,
        @Schema(example = "Housekeeping") String name,
        @Schema(example = "HK-01") String code,
        @Schema(example = "warehouse", allowableValues = {"warehouse", "closet", "front-desk", "service-area"}) String type,
        @Schema(example = "Ground floor") String area,
        @Schema(example = "Housekeeping") String responsibleTeam,
        @Schema(example = "Linen and guest supplies.") String description) {
}

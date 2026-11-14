package com.teamcoworkers.hostera.platform.inventory.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

@Schema(name = "SaveStorageLocationRequest", description = "Payload to create or update a storage location")
public record SaveStorageLocationResource(
        @NotNull @Positive @Schema(example = "1") Long propertyId,
        @NotBlank @Schema(example = "Housekeeping") String name,
        @NotBlank @Schema(example = "HK-01") String code,
        @NotBlank @Pattern(regexp = "warehouse|closet|front-desk|service-area") @Schema(example = "warehouse", allowableValues = {"warehouse", "closet", "front-desk", "service-area"}) String type,
        @NotBlank @Schema(example = "Ground floor") String area,
        @NotBlank @Schema(example = "Housekeeping") String responsibleTeam,
        @Schema(example = "Linen and guest supplies.") String description) {
}

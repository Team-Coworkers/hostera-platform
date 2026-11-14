package com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(name = "SaveRoomTypeRequest", description = "Payload to create or update a room type")
public record SaveRoomTypeResource(
        @NotNull @Positive @Schema(example = "1") Long propertyId,
        @NotBlank @Schema(example = "Standard") String name,
        @NotNull @Min(1) @Max(12) @Schema(example = "2") Integer capacity,
        @NotBlank @Schema(example = "1 queen") String bedConfiguration,
        @NotNull @Positive @Schema(example = "180") BigDecimal baseNightlyRate,
        @NotBlank @Pattern(regexp = "active|inactive") @Schema(example = "active", allowableValues = {"active", "inactive"}) String status) {
}

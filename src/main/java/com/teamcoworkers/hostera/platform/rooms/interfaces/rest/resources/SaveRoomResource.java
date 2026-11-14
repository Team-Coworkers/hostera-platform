package com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

@Schema(name = "SaveRoomRequest", description = "Payload to create or update a room")
public record SaveRoomResource(
        @NotNull @Positive @Schema(example = "1") Long propertyId,
        @NotBlank @Schema(example = "201") String number,
        @NotNull @Positive @Schema(example = "1") Long roomTypeId,
        @Schema(example = "2") Integer floor,
        @Pattern(regexp = "available|ready|occupied|needs-cleaning|blocked") @Schema(example = "available", description = "Optional. When it is not sent, a new room starts as available and an existing room keeps its status.", allowableValues = {"available", "ready", "occupied", "needs-cleaning", "blocked"}) String status) {
}

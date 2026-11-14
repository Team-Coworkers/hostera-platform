package com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Room", description = "Physical room of a property")
public record RoomResource(
        @Schema(example = "1") Long id,
        @Schema(example = "1") Long propertyId,
        @Schema(example = "201") String number,
        @Schema(example = "1") Long roomTypeId,
        @Schema(example = "2") Integer floor,
        @Schema(example = "available", allowableValues = {"available", "ready", "occupied", "needs-cleaning", "blocked"}) String status) {
}

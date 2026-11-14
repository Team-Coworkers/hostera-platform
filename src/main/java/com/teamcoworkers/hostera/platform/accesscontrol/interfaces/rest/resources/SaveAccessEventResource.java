package com.teamcoworkers.hostera.platform.accesscontrol.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

/** An access attempt. Its result and holder come from the credential, not from the request. */
@Schema(name = "SaveAccessEventRequest", description = "Payload to record an access attempt")
public record SaveAccessEventResource(
        @NotNull @Positive @Schema(example = "1") Long propertyId,
        @Schema(example = "2026-10-05T03:40:00Z") Instant occurredAt,
        @NotNull @Positive @Schema(example = "7") Long credentialId,
        @NotBlank @Schema(example = "room-door", allowableValues = {"main-entrance", "elevator", "room-door", "service-corridor", "storage-room"}) String accessPoint,
        @Positive @Schema(example = "16", nullable = true) Long roomId) {
}

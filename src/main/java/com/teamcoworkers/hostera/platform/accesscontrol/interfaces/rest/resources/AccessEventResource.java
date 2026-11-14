package com.teamcoworkers.hostera.platform.accesscontrol.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(name = "AccessEvent", description = "An attempt to open a reader, granted or denied")
public record AccessEventResource(
        @Schema(example = "1") Long id,
        @Schema(example = "1") Long propertyId,
        @Schema(example = "2026-10-05T03:40:00Z") Instant occurredAt,
        @Schema(example = "7") Long credentialId,
        @Schema(example = "3B08") String cardId,
        @Schema(example = "Emily Carter") String holderName,
        @Schema(example = "guest", allowableValues = {"guest", "staff"}) String holderType,
        @Schema(example = "room-door") String accessPoint,
        @Schema(example = "16", nullable = true) Long roomId,
        @Schema(example = "denied", allowableValues = {"granted", "denied"}) String result,
        @Schema(example = "revoked", nullable = true) String denialReason) {
}

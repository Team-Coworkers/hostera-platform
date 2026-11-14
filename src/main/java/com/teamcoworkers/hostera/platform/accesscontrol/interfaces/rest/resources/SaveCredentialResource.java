package com.teamcoworkers.hostera.platform.accesscontrol.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

@Schema(name = "SaveCredentialRequest", description = "Payload to issue or update a credential")
public record SaveCredentialResource(
        @NotNull @Positive @Schema(example = "1") Long propertyId,
        @NotBlank @Schema(example = "8F2A") String cardId,
        @NotBlank @Schema(example = "guest-key-card") String type,
        @NotBlank @Schema(example = "Elena Ferrer") String holderName,
        @Positive @Schema(nullable = true) Long staffMemberId,
        @Positive @Schema(example = "3", nullable = true) Long bookingId,
        @Schema(example = "BKG-1032") String bookingCode,
        @Positive @Schema(example = "6", nullable = true) Long roomId,
        @Schema(nullable = true) String scope,
        @NotNull @Schema(example = "2026-10-03T20:26:00Z") Instant validFrom,
        @Schema(example = "2026-10-07T16:00:00Z", nullable = true) Instant validUntil,
        @Schema(example = "2026-10-03T20:26:00Z") Instant issuedAt,
        @Schema(example = "Demo operator") String issuedBy,
        @Schema(nullable = true) Instant revokedAt,
        @Schema(nullable = true) String revokedBy,
        @Schema(nullable = true) String revocationReason,
        @Schema(example = "") String revocationNote) {
}

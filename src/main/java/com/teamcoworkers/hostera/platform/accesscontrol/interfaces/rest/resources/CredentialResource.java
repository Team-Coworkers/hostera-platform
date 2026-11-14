package com.teamcoworkers.hostera.platform.accesscontrol.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/** A credential with the fields of the frontend, plus its status at the moment of the response. */
@Schema(name = "Credential", description = "Guest key card or staff credential")
public record CredentialResource(
        @Schema(example = "1") Long id,
        @Schema(example = "1") Long propertyId,
        @Schema(example = "8F2A") String cardId,
        @Schema(example = "guest-key-card", allowableValues = {"guest-key-card", "staff-credential"}) String type,
        @Schema(example = "Elena Ferrer") String holderName,
        @Schema(nullable = true) Long staffMemberId,
        @Schema(example = "3", nullable = true) Long bookingId,
        @Schema(example = "BKG-1032") String bookingCode,
        @Schema(example = "6", nullable = true) Long roomId,
        @Schema(nullable = true, allowableValues = {"service-areas", "all-rooms", "all-areas"}) String scope,
        @Schema(example = "2026-10-03T20:26:00Z") Instant validFrom,
        @Schema(example = "2026-10-07T16:00:00Z", nullable = true) Instant validUntil,
        @Schema(example = "2026-10-03T20:26:00Z") Instant issuedAt,
        @Schema(example = "Demo operator") String issuedBy,
        @Schema(nullable = true) Instant revokedAt,
        @Schema(nullable = true) String revokedBy,
        @Schema(nullable = true) String revocationReason,
        @Schema(example = "") String revocationNote,
        @Schema(example = "active", allowableValues = {"active", "scheduled", "expired", "revoked", "lost"}) String status) {
}

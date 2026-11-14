package com.teamcoworkers.hostera.platform.accesscontrol.application.commands;

import java.time.Instant;

/** Credential fields as the frontend sends them. */
public record CredentialData(
        Long propertyId, String cardId, String type, String holderName, Long staffMemberId, Long bookingId,
        String bookingCode, Long roomId, String scope, Instant validFrom, Instant validUntil, Instant issuedAt,
        String issuedBy, Instant revokedAt, String revokedBy, String revocationReason, String revocationNote) {
}

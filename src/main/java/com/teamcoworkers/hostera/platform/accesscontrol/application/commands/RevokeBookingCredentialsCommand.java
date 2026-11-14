package com.teamcoworkers.hostera.platform.accesscontrol.application.commands;

import java.time.Instant;

/** Revokes every key card of a booking that was checked out or cancelled. */
public record RevokeBookingCredentialsCommand(Long bookingId, String bookingStatus, Instant revokedAt) {
}

package com.teamcoworkers.hostera.platform.accesscontrol.application.commands;

import java.time.Instant;

/** An access attempt: the server decides its result from the credential. */
public record AccessEventData(Long propertyId, Instant occurredAt, Long credentialId, String accessPoint, Long roomId) {
}

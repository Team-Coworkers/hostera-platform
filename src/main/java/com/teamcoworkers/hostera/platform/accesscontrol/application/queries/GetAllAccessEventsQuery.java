package com.teamcoworkers.hostera.platform.accesscontrol.application.queries;

public record GetAllAccessEventsQuery(Long propertyId, Long roomId, Long bookingId, Long credentialId) {
}

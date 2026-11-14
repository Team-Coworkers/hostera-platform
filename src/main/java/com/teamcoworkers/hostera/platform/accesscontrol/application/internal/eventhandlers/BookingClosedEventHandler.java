package com.teamcoworkers.hostera.platform.accesscontrol.application.internal.eventhandlers;

import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.RevokeBookingCredentialsCommand;
import com.teamcoworkers.hostera.platform.accesscontrol.application.commandservices.CredentialCommandService;
import com.teamcoworkers.hostera.platform.bookings.domain.model.events.BookingClosedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/** Revokes the key cards of a booking as soon as it is checked out or cancelled. */
@Service
public class BookingClosedEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingClosedEventHandler.class);

    private final CredentialCommandService credentialCommandService;

    public BookingClosedEventHandler(CredentialCommandService credentialCommandService) {
        this.credentialCommandService = credentialCommandService;
    }

    @EventListener
    public void on(BookingClosedEvent event) {
        int revoked = credentialCommandService.handle(
                new RevokeBookingCredentialsCommand(event.bookingId(), event.status(), event.closedAt()));
        if (revoked > 0) LOGGER.info("Revoked {} key cards of booking {} ({})", revoked, event.bookingId(), event.status());
    }
}

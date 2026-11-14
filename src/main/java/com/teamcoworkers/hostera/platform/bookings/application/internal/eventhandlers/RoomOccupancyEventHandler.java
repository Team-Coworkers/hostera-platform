package com.teamcoworkers.hostera.platform.bookings.application.internal.eventhandlers;

import com.teamcoworkers.hostera.platform.bookings.application.internal.outboundservices.acl.ExternalRoomsService;
import com.teamcoworkers.hostera.platform.bookings.domain.model.events.BookingCheckedInEvent;
import com.teamcoworkers.hostera.platform.bookings.domain.model.events.BookingClosedEvent;
import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.BookingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Keeps the room in step with the stay: the check-in makes it occupied and the check-out leaves it
 * waiting for housekeeping (needs-cleaning).
 */
@Service
public class RoomOccupancyEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomOccupancyEventHandler.class);

    private final ExternalRoomsService externalRoomsService;

    public RoomOccupancyEventHandler(ExternalRoomsService externalRoomsService) {
        this.externalRoomsService = externalRoomsService;
    }

    @EventListener
    public void on(BookingCheckedInEvent event) {
        if (!externalRoomsService.markRoomAsOccupied(event.roomId()))
            LOGGER.warn("Room {} of booking {} could not change to occupied", event.roomId(), event.bookingId());
    }

    @EventListener
    public void on(BookingClosedEvent event) {
        if (!BookingStatus.CHECKED_OUT.value().equals(event.status())) return;
        if (!externalRoomsService.markRoomAsNeedsCleaning(event.roomId()))
            LOGGER.warn("Room {} of booking {} could not change to needs-cleaning", event.roomId(), event.bookingId());
    }
}

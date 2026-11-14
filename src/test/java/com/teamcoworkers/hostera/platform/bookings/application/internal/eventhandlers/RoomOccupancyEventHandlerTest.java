package com.teamcoworkers.hostera.platform.bookings.application.internal.eventhandlers;

import com.teamcoworkers.hostera.platform.bookings.application.internal.outboundservices.acl.ExternalRoomsService;
import com.teamcoworkers.hostera.platform.bookings.domain.model.events.BookingCheckedInEvent;
import com.teamcoworkers.hostera.platform.bookings.domain.model.events.BookingClosedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomOccupancyEventHandlerTest {

    private static final Instant NOW = Instant.parse("2026-10-07T15:00:00Z");

    @Mock
    private ExternalRoomsService externalRoomsService;

    @InjectMocks
    private RoomOccupancyEventHandler handler;

    @Test
    void occupiesTheRoomWhenTheGuestChecksIn() {
        when(externalRoomsService.markRoomAsOccupied(12L)).thenReturn(true);

        handler.on(new BookingCheckedInEvent(1L, 1L, 12L, NOW));

        verify(externalRoomsService).markRoomAsOccupied(12L);
    }

    @Test
    void leavesTheRoomForHousekeepingWhenTheGuestChecksOut() {
        when(externalRoomsService.markRoomAsNeedsCleaning(12L)).thenReturn(true);

        handler.on(new BookingClosedEvent(1L, 1L, 12L, "checked-out", NOW));

        verify(externalRoomsService).markRoomAsNeedsCleaning(12L);
    }

    @Test
    void doesNotTouchTheRoomWhenABookingIsCancelled() {
        handler.on(new BookingClosedEvent(1L, 1L, 12L, "cancelled", NOW));

        verifyNoInteractions(externalRoomsService);
    }
}

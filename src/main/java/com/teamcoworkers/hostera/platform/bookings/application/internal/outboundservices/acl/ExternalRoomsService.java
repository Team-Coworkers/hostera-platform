package com.teamcoworkers.hostera.platform.bookings.application.internal.outboundservices.acl;

import com.teamcoworkers.hostera.platform.rooms.interfaces.acl.RoomsContextFacade;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/** What the bookings context asks and tells the rooms context. */
@Service("bookingsExternalRoomsService")
public class ExternalRoomsService {

    private final RoomsContextFacade roomsContextFacade;

    public ExternalRoomsService(RoomsContextFacade roomsContextFacade) {
        this.roomsContextFacade = roomsContextFacade;
    }

    /** @return whether the room is blocked or out of service today */
    public boolean isRoomBlockedToday(Long roomId) {
        return roomsContextFacade.isRoomBlockedOn(roomId, LocalDate.now());
    }

    /** @return whether the room changed to occupied */
    public boolean markRoomAsOccupied(Long roomId) {
        return roomsContextFacade.markRoomAsOccupied(roomId);
    }

    /** @return whether the room changed to needs-cleaning */
    public boolean markRoomAsNeedsCleaning(Long roomId) {
        return roomsContextFacade.markRoomAsNeedsCleaning(roomId);
    }
}

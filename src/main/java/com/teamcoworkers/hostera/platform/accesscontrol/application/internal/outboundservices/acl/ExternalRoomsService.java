package com.teamcoworkers.hostera.platform.accesscontrol.application.internal.outboundservices.acl;

import com.teamcoworkers.hostera.platform.rooms.interfaces.acl.RoomsContextFacade;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/** Asks the rooms context whether a room is blocked or out of service. */
@Service("accessControlExternalRoomsService")
public class ExternalRoomsService {

    private final RoomsContextFacade roomsContextFacade;

    public ExternalRoomsService(RoomsContextFacade roomsContextFacade) {
        this.roomsContextFacade = roomsContextFacade;
    }

    /** @return whether the room is blocked or out of service today, so no key card can open it */
    public boolean isRoomBlocked(Long propertyId, Long roomId) {
        return roomsContextFacade.isRoomBlockedOn(roomId, LocalDate.now());
    }
}

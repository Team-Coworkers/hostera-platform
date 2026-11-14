package com.teamcoworkers.hostera.platform.accesscontrol.application.internal.outboundservices.acl;

import org.springframework.stereotype.Service;

/**
 * Asks the rooms context whether a room is blocked (out of order or under maintenance). The rooms
 * context has no backend facade yet, so no room is reported as blocked; when it publishes one,
 * only this adapter changes.
 */
@Service
public class ExternalRoomsService {

    public boolean isRoomBlocked(Long propertyId, Long roomId) {
        return false;
    }
}

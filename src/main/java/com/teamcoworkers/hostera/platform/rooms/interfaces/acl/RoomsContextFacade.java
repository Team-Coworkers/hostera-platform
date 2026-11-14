package com.teamcoworkers.hostera.platform.rooms.interfaces.acl;

import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.Room;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.RoomStatus;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RatePlanRepository;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RoomRepository;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RoomTypeRepository;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.StatusPeriodRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.function.UnaryOperator;

/**
 * Anti-corruption layer facade that other bounded contexts (bookings and access control) use to
 * check rooms and to move them through their states without depending on the rooms domain model.
 */
@Service
public class RoomsContextFacade {
    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RatePlanRepository ratePlanRepository;
    private final StatusPeriodRepository statusPeriodRepository;

    public RoomsContextFacade(RoomRepository roomRepository, RoomTypeRepository roomTypeRepository,
                              RatePlanRepository ratePlanRepository, StatusPeriodRepository statusPeriodRepository) {
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.ratePlanRepository = ratePlanRepository;
        this.statusPeriodRepository = statusPeriodRepository;
    }

    /** @return whether a room with the given id exists */
    public boolean existsRoomById(Long roomId) {
        return roomId != null && roomRepository.existsById(roomId);
    }

    /** @return whether the room exists and belongs to the given property */
    public boolean isRoomOfProperty(Long roomId, Long propertyId) {
        return roomId != null && roomRepository.findById(roomId)
                .map(room -> room.getPropertyId().equals(propertyId))
                .orElse(false);
    }

    /** @return whether a room type with the given id exists */
    public boolean existsRoomTypeById(Long roomTypeId) {
        return roomTypeId != null && roomTypeRepository.existsById(roomTypeId);
    }

    /** @return whether a rate plan with the given id exists */
    public boolean existsRatePlanById(Long ratePlanId) {
        return ratePlanId != null && ratePlanRepository.existsById(ratePlanId);
    }

    /** @return the status of the room (for example, {@code ready}), or an empty string when it does not exist */
    public String fetchRoomStatus(Long roomId) {
        if (roomId == null) return "";
        return roomRepository.findById(roomId).map(room -> room.getStatus().value()).orElse("");
    }

    /** @return whether a guest can check in to the room right now (it is available or ready) */
    public boolean canRoomReceiveGuests(Long roomId) {
        return roomId != null && roomRepository.findById(roomId).map(Room::canReceiveGuests).orElse(false);
    }

    /**
     * @return whether the room is out of sale on the given day: its status is blocked, or a blocked
     * or out-of-service status period covers that day
     */
    public boolean isRoomBlockedOn(Long roomId, LocalDate date) {
        if (roomId == null) return false;
        var blockedNow = roomRepository.findById(roomId)
                .map(room -> room.getStatus() == RoomStatus.BLOCKED)
                .orElse(false);
        return blockedNow || statusPeriodRepository.findAllByRoomId(roomId).stream()
                .anyMatch(statusPeriod -> statusPeriod.preventsBookingOn(date));
    }

    /** A guest checked in. @return whether the room changed to occupied */
    public boolean markRoomAsOccupied(Long roomId) {
        return changeRoomStatus(roomId, Room::checkIn);
    }

    /** The guest checked out. @return whether the room changed to needs-cleaning */
    public boolean markRoomAsNeedsCleaning(Long roomId) {
        return changeRoomStatus(roomId, Room::checkOut);
    }

    /** Housekeeping finished. @return whether the room changed to ready */
    public boolean markRoomAsReady(Long roomId) {
        return changeRoomStatus(roomId, Room::markAsReady);
    }

    private boolean changeRoomStatus(Long roomId, UnaryOperator<Room> transition) {
        if (roomId == null) return false;
        var room = roomRepository.findById(roomId);
        if (room.isEmpty()) return false;
        try {
            roomRepository.save(transition.apply(room.get()));
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }
}

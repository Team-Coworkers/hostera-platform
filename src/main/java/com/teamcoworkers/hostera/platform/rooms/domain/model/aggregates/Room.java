package com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates;

import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.RoomStatus;
import com.teamcoworkers.hostera.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

/**
 * Room aggregate root: one physical room of a property, with its housekeeping and occupancy state.
 * <p>
 * Invariants: it belongs to a property and to a room type, its number is not blank (and is unique
 * in the property, which the application layer checks), and its status only changes through the
 * transitions that {@link RoomStatus} allows. After a check-out the room needs cleaning: it is
 * never left ready.
 * </p>
 */
@Getter
public class Room extends AbstractDomainAggregateRoot<Room> {
    private Long id;
    private Long propertyId;
    private String number;
    private Long roomTypeId;
    private Integer floor;
    private RoomStatus status;

    public Room(Long propertyId, String number, Long roomTypeId, Integer floor) {
        updateInformation(propertyId, number, roomTypeId, floor);
        this.status = RoomStatus.AVAILABLE;
    }

    public static Room reconstitute(Long id, Long propertyId, String number, Long roomTypeId, Integer floor, RoomStatus status) {
        var room = new Room(propertyId, number, roomTypeId, floor);
        room.id = id;
        room.status = status == null ? RoomStatus.AVAILABLE : status;
        return room;
    }

    public Room updateInformation(Long propertyId, String number, Long roomTypeId, Integer floor) {
        if (propertyId == null || propertyId <= 0)
            throw new IllegalArgumentException("Property id must be a positive number");
        if (number == null || number.isBlank()) throw new IllegalArgumentException("Room number is required");
        if (roomTypeId == null || roomTypeId <= 0)
            throw new IllegalArgumentException("Room type id must be a positive number");
        this.propertyId = propertyId;
        this.number = number.trim();
        this.roomTypeId = roomTypeId;
        this.floor = floor;
        return this;
    }

    /**
     * @param target the new state of the room
     * @throws IllegalStateException when the current state does not allow the change
     */
    public Room changeStatus(RoomStatus target) {
        if (target == null) throw new IllegalArgumentException("Room status is required");
        if (!status.canChangeTo(target))
            throw new IllegalStateException("A room that is %s cannot change to %s".formatted(status.value(), target.value()));
        this.status = target;
        return this;
    }

    /** A guest arrives: the room becomes occupied. */
    public Room checkIn() {
        return changeStatus(RoomStatus.OCCUPIED);
    }

    /** The guest leaves: the room needs cleaning before it can be ready again. */
    public Room checkOut() {
        return changeStatus(RoomStatus.NEEDS_CLEANING);
    }

    /** Housekeeping finished: the room is ready for the next arrival. */
    public Room markAsReady() {
        return changeStatus(RoomStatus.READY);
    }

    /** Takes the room out of sale. */
    public Room block() {
        return changeStatus(RoomStatus.BLOCKED);
    }

    /** Puts a blocked room back on sale. */
    public Room release() {
        return changeStatus(RoomStatus.AVAILABLE);
    }

    public boolean canReceiveGuests() {
        return status.canReceiveGuests();
    }
}

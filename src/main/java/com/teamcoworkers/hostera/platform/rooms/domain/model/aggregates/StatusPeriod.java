package com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates;

import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.DateRange;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.OperationalStatus;
import com.teamcoworkers.hostera.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.LocalDate;

/**
 * Status period aggregate root: a range of days in which a room is blocked, out of service or
 * waiting for cleaning, with the reason that the staff recorded.
 * <p>
 * Invariants: it belongs to a property and to a room, it has an operational status, its end date
 * is on or after its start date and its reason is not blank.
 * </p>
 */
@Getter
public class StatusPeriod extends AbstractDomainAggregateRoot<StatusPeriod> {
    private Long id;
    private Long propertyId;
    private Long roomId;
    private OperationalStatus status;
    private DateRange period;
    private String reason;

    public StatusPeriod(Long propertyId, Long roomId, OperationalStatus status, DateRange period, String reason) {
        updateInformation(propertyId, roomId, status, period, reason);
    }

    public static StatusPeriod reconstitute(Long id, Long propertyId, Long roomId, OperationalStatus status, DateRange period, String reason) {
        var statusPeriod = new StatusPeriod(propertyId, roomId, status, period, reason);
        statusPeriod.id = id;
        return statusPeriod;
    }

    public StatusPeriod updateInformation(Long propertyId, Long roomId, OperationalStatus status, DateRange period, String reason) {
        if (propertyId == null || propertyId <= 0)
            throw new IllegalArgumentException("Property id must be a positive number");
        if (roomId == null || roomId <= 0) throw new IllegalArgumentException("Room id must be a positive number");
        if (status == null) throw new IllegalArgumentException("Status is required");
        if (period == null) throw new IllegalArgumentException("Start date and end date are required");
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("Reason is required");
        this.propertyId = propertyId;
        this.roomId = roomId;
        this.status = status;
        this.period = period;
        this.reason = reason.trim();
        return this;
    }

    /** @return whether the room cannot be booked nor given to a guest on the given day */
    public boolean preventsBookingOn(LocalDate date) {
        return status.preventsBooking() && period.covers(date);
    }
}

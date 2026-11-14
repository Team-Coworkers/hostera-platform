package com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates;

import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.NightlyRate;
import com.teamcoworkers.hostera.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.LocalDate;

/**
 * Daily rate aggregate root: the price of one night of a room type under a rate plan. When a
 * night has no daily rate, the base nightly rate of the room type applies.
 * <p>
 * Invariants: it belongs to a property, a rate plan and a room type, it has a date and its
 * amount is greater than zero. A rate plan has at most one price per room type and date, which
 * the application layer checks.
 * </p>
 */
@Getter
public class DailyRate extends AbstractDomainAggregateRoot<DailyRate> {
    private Long id;
    private Long propertyId;
    private Long ratePlanId;
    private Long roomTypeId;
    private LocalDate date;
    private NightlyRate amount;

    public DailyRate(Long propertyId, Long ratePlanId, Long roomTypeId, LocalDate date, NightlyRate amount) {
        updateInformation(propertyId, ratePlanId, roomTypeId, date, amount);
    }

    public static DailyRate reconstitute(Long id, Long propertyId, Long ratePlanId, Long roomTypeId, LocalDate date, NightlyRate amount) {
        var dailyRate = new DailyRate(propertyId, ratePlanId, roomTypeId, date, amount);
        dailyRate.id = id;
        return dailyRate;
    }

    public DailyRate updateInformation(Long propertyId, Long ratePlanId, Long roomTypeId, LocalDate date, NightlyRate amount) {
        if (propertyId == null || propertyId <= 0)
            throw new IllegalArgumentException("Property id must be a positive number");
        if (ratePlanId == null || ratePlanId <= 0)
            throw new IllegalArgumentException("Rate plan id must be a positive number");
        if (roomTypeId == null || roomTypeId <= 0)
            throw new IllegalArgumentException("Room type id must be a positive number");
        if (date == null) throw new IllegalArgumentException("Date is required");
        if (amount == null) throw new IllegalArgumentException("Amount is required");
        this.propertyId = propertyId;
        this.ratePlanId = ratePlanId;
        this.roomTypeId = roomTypeId;
        this.date = date;
        this.amount = amount;
        return this;
    }

    /** @return whether this rate prices the given room type, rate plan and night */
    public boolean prices(Long roomTypeId, Long ratePlanId, LocalDate date) {
        return this.roomTypeId.equals(roomTypeId) && this.ratePlanId.equals(ratePlanId) && this.date.equals(date);
    }
}

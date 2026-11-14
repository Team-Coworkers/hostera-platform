package com.teamcoworkers.hostera.platform.bookings.domain.model.aggregates;

import com.teamcoworkers.hostera.platform.bookings.domain.model.events.BookingClosedEvent;
import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.BookingLifecycle;
import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.BookingStatus;
import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.Guest;
import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.StayPeriod;
import com.teamcoworkers.hostera.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Booking aggregate root: a guest stay at one property, from the request to the check-out.
 * <p>
 * Invariants: it belongs to a property, has a code and a valid status, covers at least one night,
 * hosts at least one guest and never has a negative total. Two active bookings cannot hold the
 * same room for nights that overlap; {@link #conflictsWith(Booking)} tells when that happens.
 * </p>
 * <p>
 * The transactional boundary is the booking itself: payments and key cards reference it by id.
 * When an active booking is checked out or cancelled it raises {@link BookingClosedEvent}.
 * </p>
 */
@Getter
public class Booking extends AbstractDomainAggregateRoot<Booking> {

    private Long id;
    private Long propertyId;
    private String code;
    private BookingStatus status;
    private Guest guest;
    private StayPeriod stay;
    private int guests;
    private Long roomTypeId;
    private Long roomId;
    private Long ratePlanId;
    private BigDecimal totalAmount;
    private String guestRequest;
    private LocalDate createdAt;
    private BookingLifecycle lifecycle;

    public Booking(Long propertyId, String code, BookingStatus status, Guest guest, StayPeriod stay, Integer guests,
                   Long roomTypeId, Long roomId, Long ratePlanId, BigDecimal totalAmount, String guestRequest,
                   LocalDate createdAt, BookingLifecycle lifecycle) {
        this.createdAt = createdAt == null ? LocalDate.now() : createdAt;
        updateInformation(propertyId, code, status, guest, stay, guests, roomTypeId, roomId, ratePlanId,
                totalAmount, guestRequest, lifecycle);
    }

    public static Booking reconstitute(Long id, Long propertyId, String code, BookingStatus status, Guest guest,
                                       StayPeriod stay, Integer guests, Long roomTypeId, Long roomId, Long ratePlanId,
                                       BigDecimal totalAmount, String guestRequest, LocalDate createdAt,
                                       BookingLifecycle lifecycle) {
        var booking = new Booking(propertyId, code, status, guest, stay, guests, roomTypeId, roomId, ratePlanId,
                totalAmount, guestRequest, createdAt, lifecycle);
        booking.id = id;
        return booking;
    }

    public Booking updateInformation(Long propertyId, String code, BookingStatus status, Guest guest, StayPeriod stay,
                                     Integer guests, Long roomTypeId, Long roomId, Long ratePlanId,
                                     BigDecimal totalAmount, String guestRequest, BookingLifecycle lifecycle) {
        if (propertyId == null || propertyId <= 0)
            throw new IllegalArgumentException("Property id must be a positive number");
        if (code == null || code.isBlank()) throw new IllegalArgumentException("Booking code is required");
        if (status == null) throw new IllegalArgumentException("Booking status is required");
        if (guest == null) throw new IllegalArgumentException("Booking guest is required");
        if (stay == null) throw new IllegalArgumentException("Booking stay period is required");
        if (guests == null || guests < 1) throw new IllegalArgumentException("A booking needs at least one guest");
        requirePositiveWhenPresent(roomTypeId, "Room type id");
        requirePositiveWhenPresent(roomId, "Room id");
        requirePositiveWhenPresent(ratePlanId, "Rate plan id");
        if (totalAmount == null || totalAmount.signum() < 0)
            throw new IllegalArgumentException("Booking total amount cannot be negative");

        var previousStatus = this.status;
        this.propertyId = propertyId;
        this.code = code.trim();
        this.status = status;
        this.guest = guest;
        this.stay = stay;
        this.guests = guests;
        this.roomTypeId = roomTypeId;
        this.roomId = roomId;
        this.ratePlanId = ratePlanId;
        this.totalAmount = totalAmount;
        this.guestRequest = guestRequest == null ? "" : guestRequest;
        this.lifecycle = lifecycle == null ? BookingLifecycle.empty() : lifecycle;

        if (id != null && previousStatus != null && previousStatus.isActive() && status.endsGuestAccess())
            registerDomainEvent(new BookingClosedEvent(id, propertyId, status.value(), closingMoment()));
        return this;
    }

    /**
     * @return whether both bookings are active and hold the same room for at least one shared night
     */
    public boolean conflictsWith(Booking other) {
        if (other == null || (id != null && id.equals(other.id))) return false;
        return status.isActive() && other.status.isActive()
                && roomId != null && roomId.equals(other.roomId)
                && stay.overlaps(other.stay);
    }

    /** @return whether the guest is staying at the property right now */
    public boolean isInHouse() {
        return status == BookingStatus.CHECKED_IN;
    }

    private Instant closingMoment() {
        var moment = status == BookingStatus.CHECKED_OUT ? lifecycle.checkedOutAt() : lifecycle.cancelledAt();
        return moment == null ? Instant.now() : moment;
    }

    private static void requirePositiveWhenPresent(Long value, String name) {
        if (value != null && value <= 0) throw new IllegalArgumentException(name + " must be a positive number");
    }
}

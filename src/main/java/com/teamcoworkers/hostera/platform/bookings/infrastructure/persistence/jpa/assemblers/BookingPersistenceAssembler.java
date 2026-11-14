package com.teamcoworkers.hostera.platform.bookings.infrastructure.persistence.jpa.assemblers;

import com.teamcoworkers.hostera.platform.bookings.domain.model.aggregates.Booking;
import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.BookingLifecycle;
import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.BookingStatus;
import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.Guest;
import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.StayPeriod;
import com.teamcoworkers.hostera.platform.bookings.infrastructure.persistence.jpa.entities.BookingPersistenceEntity;

public final class BookingPersistenceAssembler {

    private BookingPersistenceAssembler() {
    }

    public static Booking toDomainFromPersistence(BookingPersistenceEntity entity) {
        if (entity == null) return null;
        return Booking.reconstitute(entity.getId(), entity.getPropertyId(), entity.getCode(),
                BookingStatus.fromValue(entity.getStatus()),
                new Guest(entity.getGuestName(), entity.getGuestEmail(), entity.getGuestPhone(),
                        entity.getPreferredLanguage(), entity.getGuestDocumentType(), entity.getGuestDocumentNumber()),
                new StayPeriod(entity.getCheckInDate(), entity.getCheckOutDate()), entity.getGuests(),
                entity.getRoomTypeId(), entity.getRoomId(), entity.getRatePlanId(), entity.getTotalAmount(),
                entity.getGuestRequest(), entity.getBookedAt(),
                new BookingLifecycle(entity.getConfirmedAt(), entity.getConfirmedBy(),
                        entity.getCancelledAt(), entity.getCancelledBy(), entity.getCancellationReason(),
                        entity.getCancellationNote(), entity.getNoShowAt(), entity.getNoShowBy(),
                        entity.getCheckedInAt(), entity.getCheckedInBy(), entity.getCheckedOutAt(),
                        entity.getCheckedOutBy(), entity.getRoomCondition(), entity.getDepartureNote()));
    }

    public static BookingPersistenceEntity toPersistenceFromDomain(Booking booking) {
        var entity = new BookingPersistenceEntity();
        if (booking.getId() != null) entity.setId(booking.getId());
        entity.setPropertyId(booking.getPropertyId());
        entity.setCode(booking.getCode());
        entity.setStatus(booking.getStatus().value());
        var guest = booking.getGuest();
        entity.setGuestName(guest.name());
        entity.setGuestEmail(guest.email());
        entity.setGuestPhone(guest.phone());
        entity.setPreferredLanguage(guest.preferredLanguage());
        entity.setGuestDocumentType(guest.documentType());
        entity.setGuestDocumentNumber(guest.documentNumber());
        entity.setCheckInDate(booking.getStay().checkInDate());
        entity.setCheckOutDate(booking.getStay().checkOutDate());
        entity.setGuests(booking.getGuests());
        entity.setRoomTypeId(booking.getRoomTypeId());
        entity.setRoomId(booking.getRoomId());
        entity.setRatePlanId(booking.getRatePlanId());
        entity.setTotalAmount(booking.getTotalAmount());
        entity.setGuestRequest(booking.getGuestRequest());
        entity.setBookedAt(booking.getCreatedAt());
        var lifecycle = booking.getLifecycle();
        entity.setConfirmedAt(lifecycle.confirmedAt());
        entity.setConfirmedBy(lifecycle.confirmedBy());
        entity.setCancelledAt(lifecycle.cancelledAt());
        entity.setCancelledBy(lifecycle.cancelledBy());
        entity.setCancellationReason(lifecycle.cancellationReason());
        entity.setCancellationNote(lifecycle.cancellationNote());
        entity.setNoShowAt(lifecycle.noShowAt());
        entity.setNoShowBy(lifecycle.noShowBy());
        entity.setCheckedInAt(lifecycle.checkedInAt());
        entity.setCheckedInBy(lifecycle.checkedInBy());
        entity.setCheckedOutAt(lifecycle.checkedOutAt());
        entity.setCheckedOutBy(lifecycle.checkedOutBy());
        entity.setRoomCondition(lifecycle.roomCondition());
        entity.setDepartureNote(lifecycle.departureNote());
        return entity;
    }
}

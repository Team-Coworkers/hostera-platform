package com.teamcoworkers.hostera.platform.bookings.interfaces.rest.transform;

import com.teamcoworkers.hostera.platform.bookings.application.commands.BookingData;
import com.teamcoworkers.hostera.platform.bookings.application.commands.CreateBookingCommand;
import com.teamcoworkers.hostera.platform.bookings.application.commands.UpdateBookingCommand;
import com.teamcoworkers.hostera.platform.bookings.domain.model.aggregates.Booking;
import com.teamcoworkers.hostera.platform.bookings.interfaces.rest.resources.BookingResource;
import com.teamcoworkers.hostera.platform.bookings.interfaces.rest.resources.SaveBookingResource;

public final class BookingResourceFromEntityAssembler {

    private BookingResourceFromEntityAssembler() {
    }

    public static BookingResource toResourceFromEntity(Booking booking) {
        return new BookingResource(
                booking.getId(),
                booking.getPropertyId(),
                booking.getCode(),
                booking.getStatus().value(),
                booking.getGuest().name(),
                booking.getGuest().email(),
                booking.getGuest().phone(),
                booking.getGuest().preferredLanguage(),
                booking.getStay().checkInDate(),
                booking.getStay().checkOutDate(),
                booking.getGuests(),
                booking.getRoomTypeId(),
                booking.getRoomId(),
                booking.getRatePlanId(),
                booking.getTotalAmount(),
                booking.getGuestRequest(),
                booking.getCreatedAt(),
                booking.getLifecycle().confirmedAt(),
                booking.getLifecycle().confirmedBy(),
                booking.getLifecycle().cancelledAt(),
                booking.getLifecycle().cancelledBy(),
                booking.getLifecycle().cancellationReason(),
                booking.getLifecycle().cancellationNote(),
                booking.getLifecycle().noShowAt(),
                booking.getLifecycle().noShowBy(),
                booking.getLifecycle().checkedInAt(),
                booking.getLifecycle().checkedInBy(),
                booking.getGuest().documentType(),
                booking.getGuest().documentNumber(),
                booking.getLifecycle().checkedOutAt(),
                booking.getLifecycle().checkedOutBy(),
                booking.getLifecycle().roomCondition(),
                booking.getLifecycle().departureNote());
    }

    public static CreateBookingCommand toCreateCommand(SaveBookingResource resource) {
        return new CreateBookingCommand(toData(resource));
    }

    public static UpdateBookingCommand toUpdateCommand(Long bookingId, SaveBookingResource resource) {
        return new UpdateBookingCommand(bookingId, toData(resource));
    }

    private static BookingData toData(SaveBookingResource resource) {
        return new BookingData(
                resource.propertyId(),
                resource.code(),
                resource.status(),
                resource.guestName(),
                resource.guestEmail(),
                resource.guestPhone(),
                resource.preferredLanguage(),
                resource.guestDocumentType(),
                resource.guestDocumentNumber(),
                resource.checkInDate(),
                resource.checkOutDate(),
                resource.guests(),
                resource.roomTypeId(),
                resource.roomId(),
                resource.ratePlanId(),
                resource.totalAmount(),
                resource.guestRequest(),
                resource.createdAt(),
                resource.confirmedAt(),
                resource.confirmedBy(),
                resource.cancelledAt(),
                resource.cancelledBy(),
                resource.cancellationReason(),
                resource.cancellationNote(),
                resource.noShowAt(),
                resource.noShowBy(),
                resource.checkedInAt(),
                resource.checkedInBy(),
                resource.checkedOutAt(),
                resource.checkedOutBy(),
                resource.roomCondition(),
                resource.departureNote());
    }
}

package com.teamcoworkers.hostera.platform.bookings.interfaces.acl;

import com.teamcoworkers.hostera.platform.bookings.domain.model.aggregates.Booking;
import com.teamcoworkers.hostera.platform.bookings.domain.repositories.BookingRepository;
import org.springframework.stereotype.Service;

/** What other bounded contexts may ask the bookings context. */
@Service
public class BookingsContextFacade {

    private final BookingRepository bookingRepository;

    public BookingsContextFacade(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public boolean existsBookingById(Long bookingId) {
        return bookingId != null && bookingRepository.existsById(bookingId);
    }

    /**
     * @return whether the booking belongs to the property, is checked in and holds the given room,
     * that is, whether its guest may receive a key card for that room
     */
    public boolean isGuestInHouse(Long bookingId, Long propertyId, Long roomId) {
        if (bookingId == null || roomId == null) return false;
        return bookingRepository.findById(bookingId)
                .filter(booking -> booking.getPropertyId().equals(propertyId))
                .filter(Booking::isInHouse)
                .filter(booking -> roomId.equals(booking.getRoomId()))
                .isPresent();
    }
}

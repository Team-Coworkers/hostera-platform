package com.teamcoworkers.hostera.platform.bookings.application.internal.queryservices;

import com.teamcoworkers.hostera.platform.bookings.application.queries.GetAllBookingsQuery;
import com.teamcoworkers.hostera.platform.bookings.application.queries.GetBookingByIdQuery;
import com.teamcoworkers.hostera.platform.bookings.application.queryservices.BookingQueryService;
import com.teamcoworkers.hostera.platform.bookings.domain.model.aggregates.Booking;
import com.teamcoworkers.hostera.platform.bookings.domain.repositories.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class BookingQueryServiceImpl implements BookingQueryService {

    private final BookingRepository bookingRepository;

    public BookingQueryServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<Booking> handle(GetAllBookingsQuery query) {
        var bookings = query.propertyId() == null
                ? bookingRepository.findAll()
                : bookingRepository.findAllByPropertyId(query.propertyId());
        var stream = bookings.stream()
                .filter(booking -> query.status() == null || booking.getStatus().value().equals(query.status()))
                .filter(booking -> query.roomId() == null || query.roomId().equals(booking.getRoomId()))
                .sorted(toComparator(query.sort(), query.order()));
        if (query.limit() != null && query.limit() >= 0) stream = stream.limit(query.limit());
        return stream.toList();
    }

    @Override
    public Optional<Booking> handle(GetBookingByIdQuery query) {
        return bookingRepository.findById(query.bookingId());
    }

    private static Comparator<Booking> toComparator(String sort, String order) {
        Comparator<Booking> comparator = switch (sort == null ? "id" : sort) {
            case "code" -> Comparator.comparing(Booking::getCode);
            case "checkInDate" -> Comparator.comparing(booking -> booking.getStay().checkInDate());
            case "createdAt" -> Comparator.comparing(Booking::getCreatedAt);
            default -> Comparator.comparing(Booking::getId);
        };
        return "desc".equalsIgnoreCase(order) ? comparator.reversed() : comparator;
    }
}

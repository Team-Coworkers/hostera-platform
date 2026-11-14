package com.teamcoworkers.hostera.platform.bookings.application.internal.commandservices;

import com.teamcoworkers.hostera.platform.bookings.application.commands.BookingData;
import com.teamcoworkers.hostera.platform.bookings.application.commands.CreateBookingCommand;
import com.teamcoworkers.hostera.platform.bookings.application.commands.DeleteBookingCommand;
import com.teamcoworkers.hostera.platform.bookings.application.commands.UpdateBookingCommand;
import com.teamcoworkers.hostera.platform.bookings.application.commandservices.BookingCommandService;
import com.teamcoworkers.hostera.platform.bookings.application.internal.outboundservices.acl.ExternalRoomsService;
import com.teamcoworkers.hostera.platform.bookings.domain.model.aggregates.Booking;
import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.BookingLifecycle;
import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.BookingStatus;
import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.Guest;
import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.StayPeriod;
import com.teamcoworkers.hostera.platform.bookings.domain.repositories.BookingRepository;
import com.teamcoworkers.hostera.platform.organizations.interfaces.acl.OrganizationsContextFacade;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Creates, replaces and deletes bookings.
 * <p>
 * Besides the booking invariants, it checks that the property exists and that no other active
 * booking holds the same room for overlapping nights. A guest cannot check in to a room the rooms
 * context reports as blocked or out of service.
 * </p>
 */
@Service
public class BookingCommandServiceImpl implements BookingCommandService {

    private final BookingRepository bookingRepository;
    private final OrganizationsContextFacade organizationsContextFacade;
    private final ExternalRoomsService externalRoomsService;

    public BookingCommandServiceImpl(BookingRepository bookingRepository,
                                     OrganizationsContextFacade organizationsContextFacade,
                                     ExternalRoomsService externalRoomsService) {
        this.bookingRepository = bookingRepository;
        this.organizationsContextFacade = organizationsContextFacade;
        this.externalRoomsService = externalRoomsService;
    }

    @Override
    public Result<Booking, ApplicationError> handle(CreateBookingCommand command) {
        var data = command.data();
        if (!organizationsContextFacade.existsPropertyById(data.propertyId()))
            return Result.failure(ApplicationError.notFound("Property", String.valueOf(data.propertyId())));
        Booking booking;
        try {
            booking = new Booking(data.propertyId(), data.code(), BookingStatus.fromValue(data.status()),
                    toGuest(data), new StayPeriod(data.checkInDate(), data.checkOutDate()), data.guests(),
                    data.roomTypeId(), data.roomId(), data.ratePlanId(), data.totalAmount(), data.guestRequest(),
                    data.createdAt(), toLifecycle(data));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("booking", e.getMessage()));
        }
        var conflict = findConflict(booking);
        if (conflict.isPresent()) return Result.failure(conflict.get());
        return Result.success(bookingRepository.save(booking));
    }

    @Override
    public Result<Booking, ApplicationError> handle(UpdateBookingCommand command) {
        var existing = bookingRepository.findById(command.bookingId());
        if (existing.isEmpty())
            return Result.failure(ApplicationError.notFound("Booking", String.valueOf(command.bookingId())));
        var data = command.data();
        if (!organizationsContextFacade.existsPropertyById(data.propertyId()))
            return Result.failure(ApplicationError.notFound("Property", String.valueOf(data.propertyId())));
        var wasInHouse = existing.get().isInHouse();
        Booking booking;
        try {
            booking = existing.get().updateInformation(data.propertyId(), data.code(),
                    BookingStatus.fromValue(data.status()), toGuest(data),
                    new StayPeriod(data.checkInDate(), data.checkOutDate()), data.guests(), data.roomTypeId(),
                    data.roomId(), data.ratePlanId(), data.totalAmount(), data.guestRequest(), toLifecycle(data));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("booking", e.getMessage()));
        }
        if (!wasInHouse && booking.isInHouse() && externalRoomsService.isRoomBlockedToday(booking.getRoomId()))
            return Result.failure(ApplicationError.businessRuleViolation("room-available",
                    "Room %d is blocked and cannot receive guests".formatted(booking.getRoomId())));
        var conflict = findConflict(booking);
        if (conflict.isPresent()) return Result.failure(conflict.get());
        return Result.success(bookingRepository.save(booking));
    }

    @Override
    public Result<Long, ApplicationError> handle(DeleteBookingCommand command) {
        if (!bookingRepository.existsById(command.bookingId()))
            return Result.failure(ApplicationError.notFound("Booking", String.valueOf(command.bookingId())));
        bookingRepository.deleteById(command.bookingId());
        return Result.success(command.bookingId());
    }

    /** @return the conflict error when another active booking holds the room for an overlapping night */
    private Optional<ApplicationError> findConflict(Booking booking) {
        if (booking.getRoomId() == null || !booking.getStatus().isActive()) return Optional.empty();
        return bookingRepository.findAllByRoomId(booking.getRoomId()).stream()
                .filter(booking::conflictsWith)
                .findFirst()
                .map(other -> ApplicationError.conflict("Booking",
                        "Room %d is already held by booking %s from %s to %s".formatted(
                                booking.getRoomId(), other.getCode(),
                                other.getStay().checkInDate(), other.getStay().checkOutDate())));
    }

    private static Guest toGuest(BookingData data) {
        return new Guest(data.guestName(), data.guestEmail(), data.guestPhone(), data.preferredLanguage(),
                data.guestDocumentType(), data.guestDocumentNumber());
    }

    private static BookingLifecycle toLifecycle(BookingData data) {
        return new BookingLifecycle(data.confirmedAt(), data.confirmedBy(),
                data.cancelledAt(), data.cancelledBy(), data.cancellationReason(), data.cancellationNote(),
                data.noShowAt(), data.noShowBy(), data.checkedInAt(), data.checkedInBy(),
                data.checkedOutAt(), data.checkedOutBy(), data.roomCondition(), data.departureNote());
    }
}

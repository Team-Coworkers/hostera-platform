package com.teamcoworkers.hostera.platform.bookings.application.internal.commandservices;

import com.teamcoworkers.hostera.platform.bookings.application.commands.BookingData;
import com.teamcoworkers.hostera.platform.bookings.application.commands.CreateBookingCommand;
import com.teamcoworkers.hostera.platform.bookings.application.commands.UpdateBookingCommand;
import com.teamcoworkers.hostera.platform.bookings.application.internal.outboundservices.acl.ExternalRoomsService;
import com.teamcoworkers.hostera.platform.bookings.domain.model.aggregates.Booking;
import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.BookingStatus;
import com.teamcoworkers.hostera.platform.bookings.domain.repositories.BookingRepository;
import com.teamcoworkers.hostera.platform.organizations.interfaces.acl.OrganizationsContextFacade;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.teamcoworkers.hostera.platform.bookings.BookingFixtures.booking;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingCommandServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private OrganizationsContextFacade organizationsContextFacade;

    @Mock
    private ExternalRoomsService externalRoomsService;

    @InjectMocks
    private BookingCommandServiceImpl service;

    private static BookingData data(String code, String status, Long roomId, String checkIn, String checkOut,
                                    Integer guests) {
        return new BookingData(1L, code, status, "Adriana Torres", "adriana.torres@example.com", "+51 910 100 200", "es", null, "",
                LocalDate.parse(checkIn), LocalDate.parse(checkOut), guests, 1L, roomId, 1L, new BigDecimal("340"), "",
                LocalDate.parse("2026-10-01"), null, null, null, null, null, "", null, null, null, null, null, null,
                null, "");
    }

    private static String errorCode(Result<?, ApplicationError> result) {
        assertThat(result).isInstanceOf(Result.Failure.class);
        return ((Result.Failure<?, ApplicationError>) result).error().code();
    }

    @Test
    void createsTheBookingWhenTheRoomIsFree() {
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);
        when(bookingRepository.findAllByRoomId(12L))
                .thenReturn(List.of(booking(1L, BookingStatus.CONFIRMED, 12L, "2026-10-01", "2026-10-07")));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(call -> call.getArgument(0));

        var result = service.handle(new CreateBookingCommand(data("BKG-2001", "confirmed", 12L, "2026-10-07", "2026-10-09", 2)));

        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void rejectsABookingThatOverlapsAnActiveBookingOfTheSameRoom() {
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);
        when(bookingRepository.findAllByRoomId(12L))
                .thenReturn(List.of(booking(1L, BookingStatus.CHECKED_IN, 12L, "2026-10-06", "2026-10-08")));

        var result = service.handle(new CreateBookingCommand(data("BKG-2001", "confirmed", 12L, "2026-10-07", "2026-10-09", 2)));

        assertThat(errorCode(result)).isEqualTo("BOOKING_CONFLICT");
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void rejectsAnUnknownProperty() {
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(false);

        var result = service.handle(new CreateBookingCommand(data("BKG-2001", "confirmed", 12L, "2026-10-07", "2026-10-09", 2)));

        assertThat(errorCode(result)).isEqualTo("PROPERTY_NOT_FOUND");
    }

    @Test
    void rejectsAnInvalidStatusAsAValidationError() {
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);

        var result = service.handle(new CreateBookingCommand(data("BKG-2001", "archived", 12L, "2026-10-07", "2026-10-09", 2)));

        assertThat(errorCode(result)).isEqualTo("VALIDATION_ERROR");
    }

    @Test
    void rejectsACheckOutBeforeTheCheckIn() {
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);

        var result = service.handle(new CreateBookingCommand(data("BKG-2001", "confirmed", 12L, "2026-10-09", "2026-10-07", 2)));

        assertThat(errorCode(result)).isEqualTo("VALIDATION_ERROR");
    }

    @Test
    void reportsAMissingBookingOnUpdate() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        var result = service.handle(new UpdateBookingCommand(99L, data("BKG-2001", "confirmed", 12L, "2026-10-07", "2026-10-09", 2)));

        assertThat(errorCode(result)).isEqualTo("BOOKING_NOT_FOUND");
    }

    @Test
    void refusesTheCheckInToABlockedRoom() {
        var existing = booking(4L, BookingStatus.CONFIRMED, 12L, "2026-10-07", "2026-10-09");
        when(bookingRepository.findById(4L)).thenReturn(Optional.of(existing));
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);
        when(externalRoomsService.isRoomBlockedToday(12L)).thenReturn(true);

        var result = service.handle(new UpdateBookingCommand(4L, data("BKG-1004", "checked-in", 12L, "2026-10-07", "2026-10-09", 2)));

        assertThat(errorCode(result)).isEqualTo("BUSINESS_RULE_VIOLATION");
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void letsABookingKeepItsOwnRoomWhenItIsUpdated() {
        var existing = booking(4L, BookingStatus.CONFIRMED, 12L, "2026-10-07", "2026-10-09");
        when(bookingRepository.findById(4L)).thenReturn(Optional.of(existing));
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);
        when(bookingRepository.findAllByRoomId(12L)).thenReturn(List.of(existing));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(call -> call.getArgument(0));

        var result = service.handle(new UpdateBookingCommand(4L, data("BKG-1004", "checked-in", 12L, "2026-10-07", "2026-10-09", 2)));

        assertThat(result.isSuccess()).isTrue();
    }
}

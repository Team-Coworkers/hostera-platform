package com.teamcoworkers.hostera.platform.bookings.interfaces.rest;

import com.teamcoworkers.hostera.platform.bookings.application.commands.CreateBookingCommand;
import com.teamcoworkers.hostera.platform.bookings.application.commandservices.BookingCommandService;
import com.teamcoworkers.hostera.platform.bookings.application.queries.GetAllBookingsQuery;
import com.teamcoworkers.hostera.platform.bookings.application.queries.GetBookingByIdQuery;
import com.teamcoworkers.hostera.platform.bookings.application.queryservices.BookingQueryService;
import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.BookingStatus;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static com.teamcoworkers.hostera.platform.bookings.BookingFixtures.booking;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingsControllerTest {

    private static final String VALID_BOOKING = """
            {
              "propertyId": 1, "code": "BKG-2001", "status": "confirmed",
              "guestName": "Adriana Torres", "guestEmail": "adriana.torres@example.com",
              "guestPhone": "+51 910 100 200", "preferredLanguage": "es",
              "checkInDate": "2026-10-07", "checkOutDate": "2026-10-09", "guests": 2,
              "roomTypeId": 1, "roomId": 12, "ratePlanId": 1, "totalAmount": 340,
              "guestRequest": "", "createdAt": "2026-10-01",
              "confirmedAt": "2026-10-01T15:11:00.000Z", "confirmedBy": "Demo operator",
              "cancelledAt": null, "cancellationNote": "", "guestDocumentNumber": "", "departureNote": ""
            }""";

    @Mock
    private BookingCommandService commandService;

    @Mock
    private BookingQueryService queryService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new BookingsController(commandService, queryService)).build();
    }

    @Test
    void listsTheBookingsWithTheJsonServerParameters() throws Exception {
        var query = new GetAllBookingsQuery(1L, null, null, "code", "desc", 1);
        when(queryService.handle(query))
                .thenReturn(List.of(booking(3L, BookingStatus.CONFIRMED, 12L, "2026-10-07", "2026-10-09")));

        mockMvc.perform(get("/api/v1/bookings").param("propertyId", "1").param("_sort", "code")
                        .param("_order", "desc").param("_limit", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].code").value("BKG-1003"))
                .andExpect(jsonPath("$[0].status").value("confirmed"))
                .andExpect(jsonPath("$[0].guestName").value("Adriana Torres"))
                .andExpect(jsonPath("$[0].checkInDate").value("2026-10-07"))
                .andExpect(jsonPath("$[0].createdAt").value("2026-10-01"));
        verify(queryService).handle(query);
    }

    @Test
    void returnsNotFoundForAnUnknownBooking() throws Exception {
        when(queryService.handle(new GetBookingByIdQuery(99L))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/bookings/99")).andExpect(status().isNotFound());
    }

    @Test
    void createsABooking() throws Exception {
        when(commandService.handle(any(CreateBookingCommand.class)))
                .thenReturn(Result.success(booking(23L, BookingStatus.CONFIRMED, 12L, "2026-10-07", "2026-10-09")));

        mockMvc.perform(post("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON).content(VALID_BOOKING))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(23))
                .andExpect(jsonPath("$.roomId").value(12));
    }

    @Test
    void rejectsABookingWithoutGuestsBeforeReachingTheService() throws Exception {
        var invalid = VALID_BOOKING.replace("\"guests\": 2", "\"guests\": 0");

        mockMvc.perform(post("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON).content(invalid))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(commandService);
    }

    @Test
    void answersConflictWithAProblemDetailWhenTheRoomIsTaken() throws Exception {
        when(commandService.handle(any(CreateBookingCommand.class)))
                .thenReturn(Result.failure(ApplicationError.conflict("Booking", "Room 12 is already held")));

        mockMvc.perform(post("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON).content(VALID_BOOKING))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    void deletesABooking() throws Exception {
        when(commandService.handle(any(com.teamcoworkers.hostera.platform.bookings.application.commands.DeleteBookingCommand.class)))
                .thenReturn(Result.success(5L));

        mockMvc.perform(delete("/api/v1/bookings/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }
}

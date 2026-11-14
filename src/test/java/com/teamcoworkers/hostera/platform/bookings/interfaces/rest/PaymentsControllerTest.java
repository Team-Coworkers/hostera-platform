package com.teamcoworkers.hostera.platform.bookings.interfaces.rest;

import com.teamcoworkers.hostera.platform.bookings.application.commands.CreatePaymentCommand;
import com.teamcoworkers.hostera.platform.bookings.application.commandservices.PaymentCommandService;
import com.teamcoworkers.hostera.platform.bookings.application.queries.GetAllPaymentsQuery;
import com.teamcoworkers.hostera.platform.bookings.application.queryservices.PaymentQueryService;
import com.teamcoworkers.hostera.platform.bookings.domain.model.aggregates.Payment;
import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.PaymentMethod;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PaymentsControllerTest {

    @Mock
    private PaymentCommandService commandService;

    @Mock
    private PaymentQueryService queryService;

    private MockMvc mockMvc;

    private static Payment payment() {
        return Payment.reconstitute(1L, 1L, 2L, new BigDecimal("255"), PaymentMethod.CARD_TERMINAL,
                Instant.parse("2026-10-05T16:30:00Z"), "POS-470075", "Demo operator");
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new PaymentsController(commandService, queryService)).build();
    }

    @Test
    void listsThePaymentsOfABooking() throws Exception {
        when(queryService.handle(new GetAllPaymentsQuery(null, 2L))).thenReturn(List.of(payment()));

        mockMvc.perform(get("/api/v1/payments").param("bookingId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookingId").value(2))
                .andExpect(jsonPath("$[0].method").value("card-terminal"))
                .andExpect(jsonPath("$[0].amount").value(255));
    }

    @Test
    void recordsAPayment() throws Exception {
        when(commandService.handle(any(CreatePaymentCommand.class))).thenReturn(Result.success(payment()));

        mockMvc.perform(post("/api/v1/payments").contentType(MediaType.APPLICATION_JSON).content("""
                        {"propertyId": 1, "bookingId": 2, "amount": 255, "method": "card-terminal",
                         "paidAt": "2026-10-05T16:30:00.000Z", "reference": "POS-470075", "recordedBy": "Demo operator"}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void rejectsAZeroAmount() throws Exception {
        mockMvc.perform(post("/api/v1/payments").contentType(MediaType.APPLICATION_JSON).content("""
                        {"propertyId": 1, "bookingId": 2, "amount": 0, "method": "cash"}"""))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(commandService);
    }
}

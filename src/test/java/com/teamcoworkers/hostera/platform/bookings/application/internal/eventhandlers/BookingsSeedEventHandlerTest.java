package com.teamcoworkers.hostera.platform.bookings.application.internal.eventhandlers;

import com.teamcoworkers.hostera.platform.bookings.domain.repositories.BookingRepository;
import com.teamcoworkers.hostera.platform.bookings.domain.repositories.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/** Starts the application with the seed on and checks every frontend row was loaded with its id. */
@SpringBootTest(properties = {
        "app.seed.enabled=true",
        "spring.datasource.url=jdbc:h2:mem:bookings-seed;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
})
@ActiveProfiles("test")
class BookingsSeedEventHandlerTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void loadsTheFrontendBookingsAndPaymentsWithTheSameIds() {
        assertThat(bookingRepository.count()).isEqualTo(22);
        assertThat(paymentRepository.count()).isEqualTo(11);
        assertThat(bookingRepository.findById(1L)).hasValueSatisfying(booking ->
                assertThat(booking.getCode()).isEqualTo("BKG-1048"));
        assertThat(paymentRepository.findById(1L)).hasValueSatisfying(payment ->
                assertThat(payment.getBookingId()).isEqualTo(2L));
    }
}

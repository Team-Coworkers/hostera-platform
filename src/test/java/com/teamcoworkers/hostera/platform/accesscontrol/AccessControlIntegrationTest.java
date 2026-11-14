package com.teamcoworkers.hostera.platform.accesscontrol;

import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.CredentialStatus;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.RevocationReason;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.repositories.AccessEventRepository;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.repositories.CredentialRepository;
import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.BookingStatus;
import com.teamcoworkers.hostera.platform.bookings.domain.repositories.BookingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/** Starts the application with the frontend seed and checks the integration with the bookings context. */
@SpringBootTest(properties = {
        "app.seed.enabled=true",
        "spring.datasource.url=jdbc:h2:mem:access-control;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
})
@ActiveProfiles("test")
class AccessControlIntegrationTest {

    @Autowired
    private CredentialRepository credentialRepository;

    @Autowired
    private AccessEventRepository accessEventRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void loadsTheFrontendCredentialsAndAccessEventsWithTheSameIds() {
        assertThat(credentialRepository.count()).isEqualTo(20);
        assertThat(accessEventRepository.count()).isEqualTo(77);
        assertThat(credentialRepository.findById(1L)).hasValueSatisfying(credential ->
                assertThat(credential.getCardId()).isEqualTo("8F2A"));
        assertThat(accessEventRepository.findById(1L)).hasValueSatisfying(event ->
                assertThat(event.getCredentialId()).isEqualTo(7L));
    }

    @Test
    void checkingOutABookingRevokesItsKeyCards() {
        var booking = bookingRepository.findById(3L).orElseThrow();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CHECKED_IN);

        booking.updateInformation(booking.getPropertyId(), booking.getCode(), BookingStatus.CHECKED_OUT,
                booking.getGuest(), booking.getStay(), booking.getGuests(), booking.getRoomTypeId(),
                booking.getRoomId(), booking.getRatePlanId(), booking.getTotalAmount(), booking.getGuestRequest(),
                booking.getLifecycle());
        bookingRepository.save(booking);

        var keyCards = credentialRepository.findAllByBookingId(3L);
        assertThat(keyCards).hasSize(2).allSatisfy(credential -> {
            assertThat(credential.isRevoked()).isTrue();
            assertThat(credential.getRevocation().reason()).isEqualTo(RevocationReason.OTHER);
            assertThat(credential.statusAt(Instant.now())).isEqualTo(CredentialStatus.REVOKED);
        });
    }
}

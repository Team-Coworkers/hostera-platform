package com.teamcoworkers.hostera.platform.accesscontrol;

import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.CreateCredentialCommand;
import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.CredentialData;
import com.teamcoworkers.hostera.platform.accesscontrol.application.commandservices.CredentialCommandService;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.CredentialStatus;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.RevocationReason;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.repositories.AccessEventRepository;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.repositories.CredentialRepository;
import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.BookingStatus;
import com.teamcoworkers.hostera.platform.bookings.domain.model.aggregates.Booking;
import com.teamcoworkers.hostera.platform.bookings.domain.repositories.BookingRepository;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.RoomStatus;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RoomRepository;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;
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

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private CredentialCommandService credentialCommandService;

    private void changeStatus(Booking booking, BookingStatus status) {
        booking.updateInformation(booking.getPropertyId(), booking.getCode(), status, booking.getGuest(),
                booking.getStay(), booking.getGuests(), booking.getRoomTypeId(), booking.getRoomId(),
                booking.getRatePlanId(), booking.getTotalAmount(), booking.getGuestRequest(), booking.getLifecycle());
        bookingRepository.save(booking);
    }

    private RoomStatus roomStatus(Long roomId) {
        return roomRepository.findById(roomId).orElseThrow().getStatus();
    }

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
    void checkingOutABookingRevokesItsKeyCardsAndLeavesTheRoomForHousekeeping() {
        var booking = bookingRepository.findById(3L).orElseThrow();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CHECKED_IN);

        changeStatus(booking, BookingStatus.CHECKED_OUT);

        assertThat(roomStatus(6L)).isEqualTo(RoomStatus.NEEDS_CLEANING);

        var keyCards = credentialRepository.findAllByBookingId(3L);
        assertThat(keyCards).hasSize(2).allSatisfy(credential -> {
            assertThat(credential.isRevoked()).isTrue();
            assertThat(credential.getRevocation().reason()).isEqualTo(RevocationReason.OTHER);
            assertThat(credential.statusAt(Instant.now())).isEqualTo(CredentialStatus.REVOKED);
        });
    }

    @Test
    void checkingInABookingOccupiesItsRoom() {
        var booking = bookingRepository.findById(1L).orElseThrow();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);

        changeStatus(booking, BookingStatus.CHECKED_IN);

        assertThat(roomStatus(booking.getRoomId())).isEqualTo(RoomStatus.OCCUPIED);
    }

    @Test
    void refusesAKeyCardForABlockedRoom() {
        var booking = bookingRepository.findById(8L).orElseThrow();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CHECKED_IN);
        roomRepository.save(roomRepository.findById(booking.getRoomId()).orElseThrow().changeStatus(RoomStatus.BLOCKED));

        var result = credentialCommandService.handle(new CreateCredentialCommand(new CredentialData(booking.getPropertyId(), "9C11",
                "guest-key-card", booking.getGuest().name(), null, 8L, booking.getCode(), booking.getRoomId(), null,
                Instant.now(), Instant.now().plusSeconds(86_400), null, "Demo operator", null, null, null, "")));

        assertThat(result).isInstanceOf(Result.Failure.class);
        assertThat(((Result.Failure<?, ApplicationError>) result).error().code()).isEqualTo("BUSINESS_RULE_VIOLATION");
    }
}

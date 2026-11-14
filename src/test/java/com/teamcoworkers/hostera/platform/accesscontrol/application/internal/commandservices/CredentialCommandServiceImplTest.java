package com.teamcoworkers.hostera.platform.accesscontrol.application.internal.commandservices;

import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.CreateCredentialCommand;
import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.CredentialData;
import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.RevokeBookingCredentialsCommand;
import com.teamcoworkers.hostera.platform.accesscontrol.application.internal.outboundservices.acl.ExternalRoomsService;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates.Credential;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.Revocation;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.RevocationReason;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.repositories.CredentialRepository;
import com.teamcoworkers.hostera.platform.bookings.interfaces.acl.BookingsContextFacade;
import com.teamcoworkers.hostera.platform.organizations.interfaces.acl.OrganizationsContextFacade;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static com.teamcoworkers.hostera.platform.accesscontrol.CredentialFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CredentialCommandServiceImplTest {

    @Mock
    private CredentialRepository credentialRepository;

    @Mock
    private OrganizationsContextFacade organizationsContextFacade;

    @Mock
    private BookingsContextFacade bookingsContextFacade;

    @Mock
    private ExternalRoomsService externalRoomsService;

    @InjectMocks
    private CredentialCommandServiceImpl service;

    private static CredentialData guestCardData(Long roomId) {
        return new CredentialData(1L, "8F2A", "guest-key-card", "Elena Ferrer", null, 3L, "BKG-1032", roomId, null,
                FROM, UNTIL, FROM, "Demo operator", null, null, null, "");
    }

    private static String errorCode(Result<?, ApplicationError> result) {
        assertThat(result).isInstanceOf(Result.Failure.class);
        return ((Result.Failure<?, ApplicationError>) result).error().code();
    }

    @Test
    void issuesAKeyCardForTheRoomOfACheckedInBooking() {
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);
        when(bookingsContextFacade.existsBookingById(3L)).thenReturn(true);
        when(bookingsContextFacade.isGuestInHouse(3L, 1L, 6L)).thenReturn(true);
        when(externalRoomsService.isRoomBlocked(1L, 6L)).thenReturn(false);
        when(credentialRepository.save(any(Credential.class))).thenAnswer(call -> call.getArgument(0));

        assertThat(service.handle(new CreateCredentialCommand(guestCardData(6L))).isSuccess()).isTrue();
    }

    @Test
    void refusesAKeyCardForARoomThatIsNotAssignedToTheCheckedInBooking() {
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);
        when(bookingsContextFacade.existsBookingById(3L)).thenReturn(true);
        when(bookingsContextFacade.isGuestInHouse(3L, 1L, 9L)).thenReturn(false);

        var result = service.handle(new CreateCredentialCommand(guestCardData(9L)));

        assertThat(errorCode(result)).isEqualTo("BUSINESS_RULE_VIOLATION");
        verify(credentialRepository, never()).save(any());
    }

    @Test
    void refusesAKeyCardForABlockedRoom() {
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);
        when(bookingsContextFacade.existsBookingById(3L)).thenReturn(true);
        when(bookingsContextFacade.isGuestInHouse(3L, 1L, 6L)).thenReturn(true);
        when(externalRoomsService.isRoomBlocked(1L, 6L)).thenReturn(true);

        assertThat(errorCode(service.handle(new CreateCredentialCommand(guestCardData(6L)))))
                .isEqualTo("BUSINESS_RULE_VIOLATION");
    }

    @Test
    void reportsAMissingBooking() {
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);
        when(bookingsContextFacade.existsBookingById(3L)).thenReturn(false);

        assertThat(errorCode(service.handle(new CreateCredentialCommand(guestCardData(6L)))))
                .isEqualTo("BOOKING_NOT_FOUND");
    }

    @Test
    void revokesOnlyTheKeyCardsThatWereStillValidWhenTheBookingCloses() {
        var lost = new Revocation(DURING, "Demo operator", RevocationReason.LOST_CARD, "");
        var lostCard = guestCard(7L, 16L, lost);
        var activeCard = guestCard(8L, 16L, null);
        when(credentialRepository.findAllByBookingId(12L)).thenReturn(List.of(lostCard, activeCard));

        int revoked = service.handle(new RevokeBookingCredentialsCommand(12L, "checked-out", Instant.parse("2026-10-06T11:00:00Z")));

        assertThat(revoked).isEqualTo(1);
        assertThat(lostCard.getRevocation()).isEqualTo(lost);
        assertThat(activeCard.getRevocation().revokedAt()).isEqualTo(Instant.parse("2026-10-06T11:00:00Z"));
        verify(credentialRepository, times(1)).save(activeCard);
    }
}

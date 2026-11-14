package com.teamcoworkers.hostera.platform.accesscontrol.application.internal.commandservices;

import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.*;
import com.teamcoworkers.hostera.platform.accesscontrol.application.commandservices.CredentialCommandService;
import com.teamcoworkers.hostera.platform.accesscontrol.application.internal.outboundservices.acl.ExternalRoomsService;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates.Credential;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.RevocationReason;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.repositories.CredentialRepository;
import com.teamcoworkers.hostera.platform.bookings.interfaces.acl.BookingsContextFacade;
import com.teamcoworkers.hostera.platform.organizations.interfaces.acl.OrganizationsContextFacade;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Issues, replaces and deletes credentials. A guest key card is only issued for the room of a
 * booking that is checked in, and never for a blocked room; a staff credential only for a staff
 * member of the same property.
 */
@Service
public class CredentialCommandServiceImpl implements CredentialCommandService {

    static final String SYSTEM_OPERATOR = "Hostera";

    private final CredentialRepository credentialRepository;
    private final OrganizationsContextFacade organizationsContextFacade;
    private final BookingsContextFacade bookingsContextFacade;
    private final ExternalRoomsService externalRoomsService;

    public CredentialCommandServiceImpl(CredentialRepository credentialRepository,
                                        OrganizationsContextFacade organizationsContextFacade,
                                        BookingsContextFacade bookingsContextFacade,
                                        ExternalRoomsService externalRoomsService) {
        this.credentialRepository = credentialRepository;
        this.organizationsContextFacade = organizationsContextFacade;
        this.bookingsContextFacade = bookingsContextFacade;
        this.externalRoomsService = externalRoomsService;
    }

    @Override
    public Result<Credential, ApplicationError> handle(CreateCredentialCommand command) {
        var data = command.data();
        if (!organizationsContextFacade.existsPropertyById(data.propertyId()))
            return Result.failure(ApplicationError.notFound("Property", String.valueOf(data.propertyId())));
        Credential credential;
        try {
            credential = CredentialAssembler.toCredential(data);
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("credential", e.getMessage()));
        }
        var rejection = checkIssuing(credential);
        if (rejection.isPresent()) return Result.failure(rejection.get());
        return Result.success(credentialRepository.save(credential));
    }

    @Override
    public Result<Credential, ApplicationError> handle(UpdateCredentialCommand command) {
        var existing = credentialRepository.findById(command.credentialId());
        if (existing.isEmpty())
            return Result.failure(ApplicationError.notFound("Credential", String.valueOf(command.credentialId())));
        var data = command.data();
        if (!organizationsContextFacade.existsPropertyById(data.propertyId()))
            return Result.failure(ApplicationError.notFound("Property", String.valueOf(data.propertyId())));
        try {
            return Result.success(credentialRepository.save(CredentialAssembler.update(existing.get(), data)));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("credential", e.getMessage()));
        }
    }

    @Override
    public Result<Long, ApplicationError> handle(DeleteCredentialCommand command) {
        if (!credentialRepository.existsById(command.credentialId()))
            return Result.failure(ApplicationError.notFound("Credential", String.valueOf(command.credentialId())));
        credentialRepository.deleteById(command.credentialId());
        return Result.success(command.credentialId());
    }

    @Override
    public int handle(RevokeBookingCredentialsCommand command) {
        int revoked = 0;
        for (var credential : credentialRepository.findAllByBookingId(command.bookingId())) {
            if (credential.revoke(command.revokedAt(), SYSTEM_OPERATOR, RevocationReason.OTHER,
                    "Revoked automatically: booking " + command.bookingStatus())) {
                credentialRepository.save(credential);
                revoked++;
            }
        }
        return revoked;
    }

    /** @return why the credential cannot be issued, if it cannot */
    private Optional<ApplicationError> checkIssuing(Credential credential) {
        if (credential.isRevoked()) return Optional.empty();
        if (!credential.isGuestKeyCard()) {
            if (!organizationsContextFacade.isStaffMemberOfProperty(credential.getStaffMemberId(), credential.getPropertyId()))
                return Optional.of(ApplicationError.validationError("credential",
                        "Staff member %d does not work at property %d".formatted(credential.getStaffMemberId(),
                                credential.getPropertyId())));
            return Optional.empty();
        }
        if (!bookingsContextFacade.existsBookingById(credential.getBookingId()))
            return Optional.of(ApplicationError.notFound("Booking", String.valueOf(credential.getBookingId())));
        if (!bookingsContextFacade.isGuestInHouse(credential.getBookingId(), credential.getPropertyId(), credential.getRoomId()))
            return Optional.of(ApplicationError.businessRuleViolation("guest-in-house",
                    "Room %d is not assigned to booking %d while its guest is checked in".formatted(
                            credential.getRoomId(), credential.getBookingId())));
        if (externalRoomsService.isRoomBlocked(credential.getPropertyId(), credential.getRoomId()))
            return Optional.of(ApplicationError.businessRuleViolation("room-available",
                    "Room %d is blocked".formatted(credential.getRoomId())));
        return Optional.empty();
    }
}

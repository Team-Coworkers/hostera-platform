package com.teamcoworkers.hostera.platform.accesscontrol.application.internal.commandservices;

import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.AccessEventData;
import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.CreateAccessEventCommand;
import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.DeleteAccessEventCommand;
import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.UpdateAccessEventCommand;
import com.teamcoworkers.hostera.platform.accesscontrol.application.commandservices.AccessEventCommandService;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates.AccessEvent;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates.Credential;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.AccessPoint;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.repositories.AccessEventRepository;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.repositories.CredentialRepository;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * Records access attempts. The result is never taken from the request: the credential decides it,
 * so an attempt with a revoked, lost, expired or not yet active credential, or at another room, is
 * recorded as denied.
 */
@Service
public class AccessEventCommandServiceImpl implements AccessEventCommandService {

    private final AccessEventRepository accessEventRepository;
    private final CredentialRepository credentialRepository;

    public AccessEventCommandServiceImpl(AccessEventRepository accessEventRepository,
                                         CredentialRepository credentialRepository) {
        this.accessEventRepository = accessEventRepository;
        this.credentialRepository = credentialRepository;
    }

    @Override
    public Result<AccessEvent, ApplicationError> handle(CreateAccessEventCommand command) {
        return evaluate(command.data(), (credential) -> AccessEvent.recordAttempt(credential,
                AccessPoint.fromValue(command.data().accessPoint()), command.data().roomId(), command.data().occurredAt()));
    }

    @Override
    public Result<AccessEvent, ApplicationError> handle(UpdateAccessEventCommand command) {
        var existing = accessEventRepository.findById(command.accessEventId());
        if (existing.isEmpty())
            return Result.failure(ApplicationError.notFound("Access_Event", String.valueOf(command.accessEventId())));
        var data = command.data();
        return evaluate(data, (credential) -> existing.get().updateAttempt(credential,
                AccessPoint.fromValue(data.accessPoint()), data.roomId(), data.occurredAt()));
    }

    @Override
    public Result<Long, ApplicationError> handle(DeleteAccessEventCommand command) {
        if (!accessEventRepository.existsById(command.accessEventId()))
            return Result.failure(ApplicationError.notFound("Access_Event", String.valueOf(command.accessEventId())));
        accessEventRepository.deleteById(command.accessEventId());
        return Result.success(command.accessEventId());
    }

    private Result<AccessEvent, ApplicationError> evaluate(AccessEventData data, Function<Credential, AccessEvent> attempt) {
        var credential = data.credentialId() == null ? java.util.Optional.<Credential>empty()
                : credentialRepository.findById(data.credentialId());
        if (credential.isEmpty())
            return Result.failure(ApplicationError.notFound("Credential", String.valueOf(data.credentialId())));
        if (data.propertyId() != null && !data.propertyId().equals(credential.get().getPropertyId()))
            return Result.failure(ApplicationError.validationError("access-event",
                    "Credential %d does not belong to property %d".formatted(data.credentialId(), data.propertyId())));
        try {
            return Result.success(accessEventRepository.save(attempt.apply(credential.get())));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("access-event", e.getMessage()));
        }
    }
}

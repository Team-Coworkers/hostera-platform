package com.teamcoworkers.hostera.platform.accesscontrol.application.commandservices;

import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.*;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates.Credential;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;

public interface CredentialCommandService {

    Result<Credential, ApplicationError> handle(CreateCredentialCommand command);

    Result<Credential, ApplicationError> handle(UpdateCredentialCommand command);

    Result<Long, ApplicationError> handle(DeleteCredentialCommand command);

    /** @return how many key cards were revoked */
    int handle(RevokeBookingCredentialsCommand command);
}

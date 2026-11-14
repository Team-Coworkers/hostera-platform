package com.teamcoworkers.hostera.platform.accesscontrol.application.commandservices;

import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.CreateAccessEventCommand;
import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.DeleteAccessEventCommand;
import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.UpdateAccessEventCommand;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates.AccessEvent;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;

public interface AccessEventCommandService {

    Result<AccessEvent, ApplicationError> handle(CreateAccessEventCommand command);

    Result<AccessEvent, ApplicationError> handle(UpdateAccessEventCommand command);

    Result<Long, ApplicationError> handle(DeleteAccessEventCommand command);
}

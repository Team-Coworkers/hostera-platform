package com.teamcoworkers.hostera.platform.organizations.application.commandservices;

import com.teamcoworkers.hostera.platform.organizations.application.commands.CreatePropertyCommand;
import com.teamcoworkers.hostera.platform.organizations.application.commands.DeletePropertyCommand;
import com.teamcoworkers.hostera.platform.organizations.application.commands.UpdatePropertyCommand;
import com.teamcoworkers.hostera.platform.organizations.domain.model.aggregates.Property;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;

public interface PropertyCommandService {
    Result<Property, ApplicationError> handle(CreatePropertyCommand command);
    Result<Property, ApplicationError> handle(UpdatePropertyCommand command);
    Result<Long, ApplicationError> handle(DeletePropertyCommand command);
}

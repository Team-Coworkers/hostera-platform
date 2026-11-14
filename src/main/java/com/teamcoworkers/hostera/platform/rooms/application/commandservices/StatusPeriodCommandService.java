package com.teamcoworkers.hostera.platform.rooms.application.commandservices;

import com.teamcoworkers.hostera.platform.rooms.application.commands.CreateStatusPeriodCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.DeleteStatusPeriodCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.UpdateStatusPeriodCommand;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.StatusPeriod;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;

public interface StatusPeriodCommandService {
    Result<StatusPeriod, ApplicationError> handle(CreateStatusPeriodCommand command);
    Result<StatusPeriod, ApplicationError> handle(UpdateStatusPeriodCommand command);
    Result<Long, ApplicationError> handle(DeleteStatusPeriodCommand command);
}

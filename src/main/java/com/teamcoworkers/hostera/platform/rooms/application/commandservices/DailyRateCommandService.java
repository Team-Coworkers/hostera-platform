package com.teamcoworkers.hostera.platform.rooms.application.commandservices;

import com.teamcoworkers.hostera.platform.rooms.application.commands.CreateDailyRateCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.DeleteDailyRateCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.UpdateDailyRateCommand;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.DailyRate;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;

public interface DailyRateCommandService {
    Result<DailyRate, ApplicationError> handle(CreateDailyRateCommand command);
    Result<DailyRate, ApplicationError> handle(UpdateDailyRateCommand command);
    Result<Long, ApplicationError> handle(DeleteDailyRateCommand command);
}

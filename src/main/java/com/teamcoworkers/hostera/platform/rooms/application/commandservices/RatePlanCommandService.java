package com.teamcoworkers.hostera.platform.rooms.application.commandservices;

import com.teamcoworkers.hostera.platform.rooms.application.commands.CreateRatePlanCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.DeleteRatePlanCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.UpdateRatePlanCommand;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.RatePlan;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;

public interface RatePlanCommandService {
    Result<RatePlan, ApplicationError> handle(CreateRatePlanCommand command);
    Result<RatePlan, ApplicationError> handle(UpdateRatePlanCommand command);
    Result<Long, ApplicationError> handle(DeleteRatePlanCommand command);
}

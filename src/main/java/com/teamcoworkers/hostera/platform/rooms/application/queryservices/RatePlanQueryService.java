package com.teamcoworkers.hostera.platform.rooms.application.queryservices;

import com.teamcoworkers.hostera.platform.rooms.application.queries.GetAllRatePlansQuery;
import com.teamcoworkers.hostera.platform.rooms.application.queries.GetRatePlanByIdQuery;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.RatePlan;

import java.util.List;
import java.util.Optional;

public interface RatePlanQueryService {
    List<RatePlan> handle(GetAllRatePlansQuery query);
    Optional<RatePlan> handle(GetRatePlanByIdQuery query);
}

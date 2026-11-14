package com.teamcoworkers.hostera.platform.rooms.application.queryservices;

import com.teamcoworkers.hostera.platform.rooms.application.queries.GetAllStatusPeriodsQuery;
import com.teamcoworkers.hostera.platform.rooms.application.queries.GetStatusPeriodByIdQuery;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.StatusPeriod;

import java.util.List;
import java.util.Optional;

public interface StatusPeriodQueryService {
    List<StatusPeriod> handle(GetAllStatusPeriodsQuery query);
    Optional<StatusPeriod> handle(GetStatusPeriodByIdQuery query);
}

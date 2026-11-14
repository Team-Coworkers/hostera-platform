package com.teamcoworkers.hostera.platform.rooms.application.queryservices;

import com.teamcoworkers.hostera.platform.rooms.application.queries.GetAllDailyRatesQuery;
import com.teamcoworkers.hostera.platform.rooms.application.queries.GetDailyRateByIdQuery;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.DailyRate;

import java.util.List;
import java.util.Optional;

public interface DailyRateQueryService {
    List<DailyRate> handle(GetAllDailyRatesQuery query);
    Optional<DailyRate> handle(GetDailyRateByIdQuery query);
}

package com.teamcoworkers.hostera.platform.accesscontrol.application.queryservices;

import com.teamcoworkers.hostera.platform.accesscontrol.application.queries.GetAllAccessEventsQuery;
import com.teamcoworkers.hostera.platform.accesscontrol.application.queries.GetAccessEventByIdQuery;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates.AccessEvent;

import java.util.List;
import java.util.Optional;

public interface AccessEventQueryService {

    List<AccessEvent> handle(GetAllAccessEventsQuery query);

    Optional<AccessEvent> handle(GetAccessEventByIdQuery query);
}

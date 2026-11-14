package com.teamcoworkers.hostera.platform.organizations.application.queryservices;

import com.teamcoworkers.hostera.platform.organizations.application.queries.GetAllPropertiesQuery;
import com.teamcoworkers.hostera.platform.organizations.application.queries.GetPropertyByIdQuery;
import com.teamcoworkers.hostera.platform.organizations.domain.model.aggregates.Property;

import java.util.List;
import java.util.Optional;

public interface PropertyQueryService {
    List<Property> handle(GetAllPropertiesQuery query);
    Optional<Property> handle(GetPropertyByIdQuery query);
}

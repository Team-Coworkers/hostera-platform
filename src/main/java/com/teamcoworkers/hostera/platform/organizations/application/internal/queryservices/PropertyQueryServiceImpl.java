package com.teamcoworkers.hostera.platform.organizations.application.internal.queryservices;

import com.teamcoworkers.hostera.platform.organizations.application.queries.GetAllPropertiesQuery;
import com.teamcoworkers.hostera.platform.organizations.application.queries.GetPropertyByIdQuery;
import com.teamcoworkers.hostera.platform.organizations.application.queryservices.PropertyQueryService;
import com.teamcoworkers.hostera.platform.organizations.domain.model.aggregates.Property;
import com.teamcoworkers.hostera.platform.organizations.domain.repositories.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PropertyQueryServiceImpl implements PropertyQueryService {
    private final PropertyRepository propertyRepository;

    public PropertyQueryServiceImpl(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @Override
    public List<Property> handle(GetAllPropertiesQuery query) {
        return query.organizationId() == null
                ? propertyRepository.findAll()
                : propertyRepository.findAllByOrganizationId(query.organizationId());
    }

    @Override
    public Optional<Property> handle(GetPropertyByIdQuery query) {
        return propertyRepository.findById(query.propertyId());
    }
}

package com.teamcoworkers.hostera.platform.organizations.infrastructure.persistence.jpa.assemblers;

import com.teamcoworkers.hostera.platform.organizations.domain.model.aggregates.Property;
import com.teamcoworkers.hostera.platform.organizations.domain.model.valueobjects.CurrencyCode;
import com.teamcoworkers.hostera.platform.organizations.infrastructure.persistence.jpa.entities.PropertyPersistenceEntity;

public final class PropertyPersistenceAssembler {
    private PropertyPersistenceAssembler() {
    }

    public static Property toDomainFromPersistence(PropertyPersistenceEntity entity) {
        if (entity == null) return null;
        return Property.reconstitute(entity.getId(), entity.getOrganizationId(), entity.getName(), entity.getCity(),
                new CurrencyCode(entity.getCurrency()));
    }

    public static PropertyPersistenceEntity toPersistenceFromDomain(Property property) {
        var entity = new PropertyPersistenceEntity();
        if (property.getId() != null) entity.setId(property.getId());
        entity.setOrganizationId(property.getOrganizationId());
        entity.setName(property.getName());
        entity.setCity(property.getCity());
        entity.setCurrency(property.getCurrency().code());
        return entity;
    }
}

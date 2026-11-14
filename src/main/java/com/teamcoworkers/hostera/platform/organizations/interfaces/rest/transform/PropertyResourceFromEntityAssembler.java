package com.teamcoworkers.hostera.platform.organizations.interfaces.rest.transform;

import com.teamcoworkers.hostera.platform.organizations.application.commands.CreatePropertyCommand;
import com.teamcoworkers.hostera.platform.organizations.application.commands.UpdatePropertyCommand;
import com.teamcoworkers.hostera.platform.organizations.domain.model.aggregates.Property;
import com.teamcoworkers.hostera.platform.organizations.interfaces.rest.resources.PropertyResource;
import com.teamcoworkers.hostera.platform.organizations.interfaces.rest.resources.SavePropertyResource;

public final class PropertyResourceFromEntityAssembler {
    private PropertyResourceFromEntityAssembler() {
    }

    public static PropertyResource toResourceFromEntity(Property property) {
        return new PropertyResource(property.getId(), property.getOrganizationId(), property.getName(),
                property.getCity(), property.getCurrency().code());
    }

    public static CreatePropertyCommand toCreateCommand(SavePropertyResource resource) {
        return new CreatePropertyCommand(resource.organizationId(), resource.name(), resource.city(), resource.currency());
    }

    public static UpdatePropertyCommand toUpdateCommand(Long propertyId, SavePropertyResource resource) {
        return new UpdatePropertyCommand(propertyId, resource.organizationId(), resource.name(), resource.city(), resource.currency());
    }
}

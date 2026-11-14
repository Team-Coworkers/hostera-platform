package com.teamcoworkers.hostera.platform.organizations.domain.model.aggregates;

import com.teamcoworkers.hostera.platform.organizations.domain.model.valueobjects.CurrencyCode;
import com.teamcoworkers.hostera.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

/**
 * Property aggregate root: a hotel (or one site of a small chain) operated with Hostera.
 * <p>
 * Invariants: it belongs to an organization, its name and city are not blank, and it charges in
 * one valid currency.
 * </p>
 */
@Getter
public class Property extends AbstractDomainAggregateRoot<Property> {
    private Long id;
    private Long organizationId;
    private String name;
    private String city;
    private CurrencyCode currency;

    public Property(Long organizationId, String name, String city, CurrencyCode currency) {
        updateInformation(organizationId, name, city, currency);
    }

    public static Property reconstitute(Long id, Long organizationId, String name, String city, CurrencyCode currency) {
        var property = new Property(organizationId, name, city, currency);
        property.id = id;
        return property;
    }

    public Property updateInformation(Long organizationId, String name, String city, CurrencyCode currency) {
        if (organizationId == null || organizationId <= 0)
            throw new IllegalArgumentException("Organization id must be a positive number");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Property name is required");
        if (city == null || city.isBlank()) throw new IllegalArgumentException("Property city is required");
        if (currency == null) throw new IllegalArgumentException("Property currency is required");
        this.organizationId = organizationId;
        this.name = name.trim();
        this.city = city.trim();
        this.currency = currency;
        return this;
    }
}

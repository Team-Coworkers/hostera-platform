package com.teamcoworkers.hostera.platform.organizations.application.internal.eventhandlers;

import com.teamcoworkers.hostera.platform.organizations.domain.model.aggregates.Property;
import com.teamcoworkers.hostera.platform.organizations.domain.model.aggregates.StaffMember;
import com.teamcoworkers.hostera.platform.organizations.domain.model.valueobjects.CurrencyCode;
import com.teamcoworkers.hostera.platform.organizations.domain.repositories.PropertyRepository;
import com.teamcoworkers.hostera.platform.organizations.domain.repositories.StaffMemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.core.annotation.Order;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Loads the demonstration properties and staff of the Hostera frontend on an empty database, so
 * the deployed application does not start empty. The rows keep the order of {@code server/db.json}
 * of the frontend, so their generated identifiers match the ones the other contexts reference.
 */
@Service
public class OrganizationsSeedEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationsSeedEventHandler.class);

    private final PropertyRepository propertyRepository;
    private final StaffMemberRepository staffMemberRepository;
    private final boolean seedEnabled;

    public OrganizationsSeedEventHandler(PropertyRepository propertyRepository,
                                         StaffMemberRepository staffMemberRepository,
                                         @Value("${app.seed.enabled:true}") boolean seedEnabled) {
        this.propertyRepository = propertyRepository;
        this.staffMemberRepository = staffMemberRepository;
        this.seedEnabled = seedEnabled;
    }

    @Order(10)
    @EventListener
    public void on(ApplicationReadyEvent event) {
        if (!seedEnabled || propertyRepository.count() > 0) return;
        var pen = new CurrencyCode("PEN");
        propertyRepository.save(new Property(1L, "Cartagena 04", "Cartagena", pen));
        propertyRepository.save(new Property(1L, "Lima 01", "Lima", pen));
        String[][] staff = {
                {"1", "Lucía Martín", "Operations manager"},
                {"1", "Andrés León", "Maintenance technician"},
                {"1", "Rosa Pérez", "Housekeeping supervisor"},
                {"1", "Jorge Salas", "Front desk agent"},
                {"1", "Diana Flores", "Night auditor"},
                {"1", "Pedro Rivas", "Housekeeping attendant"},
                {"2", "Carla Núñez", "Front desk agent"},
                {"2", "Miguel Ortiz", "Housekeeping attendant"},
        };
        for (var row : staff) staffMemberRepository.save(new StaffMember(Long.valueOf(row[0]), row[1], row[2]));
        LOGGER.info("Seeded {} properties and {} staff members", 2, staff.length);
    }
}

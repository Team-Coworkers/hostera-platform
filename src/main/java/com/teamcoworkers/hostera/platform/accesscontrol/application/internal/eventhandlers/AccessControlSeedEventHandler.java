package com.teamcoworkers.hostera.platform.accesscontrol.application.internal.eventhandlers;

import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.CredentialData;
import com.teamcoworkers.hostera.platform.accesscontrol.application.internal.commandservices.CredentialAssembler;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates.AccessEvent;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.AccessPoint;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.AccessResult;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.DenialReason;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.HolderType;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.repositories.AccessEventRepository;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.repositories.CredentialRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;

/**
 * Loads the demonstration credentials and access events of the Hostera frontend on an empty
 * database, from {@code seed/credentials.json} and {@code seed/access-events.json}, in the same
 * order as {@code server/db.json} so their identifiers match. They are history, so they are saved
 * as they are instead of being issued or evaluated again. It runs after the bookings seed.
 */
@Service
public class AccessControlSeedEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessControlSeedEventHandler.class);

    private record CredentialsFile(List<CredentialData> credentials) {
    }

    private record AccessEventRow(Long propertyId, Instant occurredAt, Long credentialId, String cardId,
                                  String holderName, String holderType, String accessPoint, Long roomId,
                                  String result, String denialReason) {
    }

    private record AccessEventsFile(@JsonProperty("access-events") List<AccessEventRow> accessEvents) {
    }

    private final CredentialRepository credentialRepository;
    private final AccessEventRepository accessEventRepository;
    private final boolean seedEnabled;
    private final JsonMapper jsonMapper = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    public AccessControlSeedEventHandler(CredentialRepository credentialRepository,
                                         AccessEventRepository accessEventRepository,
                                         @Value("${app.seed.enabled:true}") boolean seedEnabled) {
        this.credentialRepository = credentialRepository;
        this.accessEventRepository = accessEventRepository;
        this.seedEnabled = seedEnabled;
    }

    @Order(30)
    @EventListener
    public void on(ApplicationReadyEvent event) {
        if (!seedEnabled || credentialRepository.count() > 0) return;
        var credentials = read("seed/credentials.json", CredentialsFile.class).credentials();
        var accessEvents = read("seed/access-events.json", AccessEventsFile.class).accessEvents();
        credentials.forEach(data -> credentialRepository.save(CredentialAssembler.toCredential(data)));
        accessEvents.forEach(row -> accessEventRepository.save(new AccessEvent(row.propertyId(), row.occurredAt(),
                row.credentialId(), row.cardId(), row.holderName(),
                row.holderType() == null ? null : HolderType.fromValue(row.holderType()),
                AccessPoint.fromValue(row.accessPoint()), row.roomId(), AccessResult.fromValue(row.result()),
                row.denialReason() == null ? null : DenialReason.fromValue(row.denialReason()))));
        LOGGER.info("Seeded {} credentials and {} access events", credentials.size(), accessEvents.size());
    }

    private <T> T read(String path, Class<T> type) {
        try (InputStream input = new ClassPathResource(path).getInputStream()) {
            return jsonMapper.readValue(input, type);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read the seed file " + path, e);
        }
    }
}

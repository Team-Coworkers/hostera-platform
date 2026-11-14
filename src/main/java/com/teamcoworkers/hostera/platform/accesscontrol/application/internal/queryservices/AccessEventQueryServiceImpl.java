package com.teamcoworkers.hostera.platform.accesscontrol.application.internal.queryservices;

import com.teamcoworkers.hostera.platform.accesscontrol.application.queries.GetAccessEventByIdQuery;
import com.teamcoworkers.hostera.platform.accesscontrol.application.queries.GetAllAccessEventsQuery;
import com.teamcoworkers.hostera.platform.accesscontrol.application.queryservices.AccessEventQueryService;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates.AccessEvent;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates.Credential;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.repositories.AccessEventRepository;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.repositories.CredentialRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccessEventQueryServiceImpl implements AccessEventQueryService {

    private final AccessEventRepository accessEventRepository;
    private final CredentialRepository credentialRepository;

    public AccessEventQueryServiceImpl(AccessEventRepository accessEventRepository,
                                       CredentialRepository credentialRepository) {
        this.accessEventRepository = accessEventRepository;
        this.credentialRepository = credentialRepository;
    }

    /** The bookingId filter keeps the attempts made with the key cards of that booking. */
    @Override
    public List<AccessEvent> handle(GetAllAccessEventsQuery query) {
        Set<Long> bookingCredentials = query.bookingId() == null ? null
                : credentialRepository.findAllByBookingId(query.bookingId()).stream()
                .map(Credential::getId).collect(Collectors.toSet());
        var events = query.propertyId() == null ? accessEventRepository.findAll()
                : accessEventRepository.findAllByPropertyId(query.propertyId());
        return events.stream()
                .filter(event -> query.roomId() == null || query.roomId().equals(event.getRoomId()))
                .filter(event -> query.credentialId() == null || query.credentialId().equals(event.getCredentialId()))
                .filter(event -> bookingCredentials == null || bookingCredentials.contains(event.getCredentialId()))
                .toList();
    }

    @Override
    public Optional<AccessEvent> handle(GetAccessEventByIdQuery query) {
        return accessEventRepository.findById(query.accessEventId());
    }
}

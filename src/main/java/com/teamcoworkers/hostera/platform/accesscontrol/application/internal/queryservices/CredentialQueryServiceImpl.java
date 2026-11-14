package com.teamcoworkers.hostera.platform.accesscontrol.application.internal.queryservices;

import com.teamcoworkers.hostera.platform.accesscontrol.application.queries.GetAllCredentialsQuery;
import com.teamcoworkers.hostera.platform.accesscontrol.application.queries.GetCredentialByIdQuery;
import com.teamcoworkers.hostera.platform.accesscontrol.application.queryservices.CredentialQueryService;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates.Credential;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.repositories.CredentialRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CredentialQueryServiceImpl implements CredentialQueryService {

    private final CredentialRepository credentialRepository;

    public CredentialQueryServiceImpl(CredentialRepository credentialRepository) {
        this.credentialRepository = credentialRepository;
    }

    @Override
    public List<Credential> handle(GetAllCredentialsQuery query) {
        var credentials = query.bookingId() != null ? credentialRepository.findAllByBookingId(query.bookingId())
                : query.propertyId() != null ? credentialRepository.findAllByPropertyId(query.propertyId())
                : credentialRepository.findAll();
        return credentials.stream()
                .filter(credential -> query.propertyId() == null || query.propertyId().equals(credential.getPropertyId()))
                .filter(credential -> query.roomId() == null || query.roomId().equals(credential.getRoomId()))
                .toList();
    }

    @Override
    public Optional<Credential> handle(GetCredentialByIdQuery query) {
        return credentialRepository.findById(query.credentialId());
    }
}

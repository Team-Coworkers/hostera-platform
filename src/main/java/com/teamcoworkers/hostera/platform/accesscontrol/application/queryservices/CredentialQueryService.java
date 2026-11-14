package com.teamcoworkers.hostera.platform.accesscontrol.application.queryservices;

import com.teamcoworkers.hostera.platform.accesscontrol.application.queries.GetAllCredentialsQuery;
import com.teamcoworkers.hostera.platform.accesscontrol.application.queries.GetCredentialByIdQuery;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates.Credential;

import java.util.List;
import java.util.Optional;

public interface CredentialQueryService {

    List<Credential> handle(GetAllCredentialsQuery query);

    Optional<Credential> handle(GetCredentialByIdQuery query);
}

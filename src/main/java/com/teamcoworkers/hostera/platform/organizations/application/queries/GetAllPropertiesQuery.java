package com.teamcoworkers.hostera.platform.organizations.application.queries;

/** Optional filters are {@code null} when the request does not send them. */
public record GetAllPropertiesQuery(Long organizationId) {
}

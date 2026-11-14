package com.teamcoworkers.hostera.platform.organizations.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "StaffMember", description = "Person who works at a property")
public record StaffMemberResource(
        @Schema(example = "1") Long id,
        @Schema(example = "1") Long propertyId,
        @Schema(example = "Lucía Martín") String name,
        @Schema(example = "Operations manager") String role) {
}

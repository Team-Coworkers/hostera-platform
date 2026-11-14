package com.teamcoworkers.hostera.platform.organizations.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(name = "SaveStaffMemberRequest", description = "Payload to create or update a staff member")
public record SaveStaffMemberResource(
        @NotNull @Positive @Schema(example = "1") Long propertyId,
        @NotBlank @Schema(example = "Lucía Martín") String name,
        @NotBlank @Schema(example = "Operations manager") String role) {
}

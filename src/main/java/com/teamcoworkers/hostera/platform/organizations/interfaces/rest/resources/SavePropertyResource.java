package com.teamcoworkers.hostera.platform.organizations.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Pattern;

@Schema(name = "SavePropertyRequest", description = "Payload to create or update a property")
public record SavePropertyResource(
        @NotNull @Positive @Schema(example = "1") Long organizationId,
        @NotBlank @Schema(example = "Cartagena 04") String name,
        @NotBlank @Schema(example = "Cartagena") String city,
        @NotBlank @Pattern(regexp = "[A-Za-z]{3}") @Schema(example = "PEN") String currency) {
}

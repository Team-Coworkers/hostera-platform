package com.teamcoworkers.hostera.platform.organizations.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Property", description = "Hotel property operated with Hostera")
public record PropertyResource(
        @Schema(example = "1") Long id,
        @Schema(example = "1") Long organizationId,
        @Schema(example = "Cartagena 04") String name,
        @Schema(example = "Cartagena") String city,
        @Schema(example = "PEN") String currency) {
}

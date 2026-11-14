package com.teamcoworkers.hostera.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Resource representing an IAM user returned by the REST API.
 */
@Schema(
    name = "UserResponse",
    description = "User information response",
    example = "{\"id\": 1, \"username\": \"maria.quispe@hostera.pe\", \"roles\": [\"ROLE_USER\", \"ROLE_INSTRUCTOR\"]}"
)
public record UserResource(
    @Schema(description = "User unique identifier", example = "1")
    Long id,

    @Schema(description = "User username", example = "maria.quispe@hostera.pe")
    String username,

    @Schema(description = "User assigned roles", example = "[\"ROLE_USER\"]")
    List<String> roles
) {
}

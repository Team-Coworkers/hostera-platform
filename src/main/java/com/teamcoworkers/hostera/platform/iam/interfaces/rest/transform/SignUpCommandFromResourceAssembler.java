package com.teamcoworkers.hostera.platform.iam.interfaces.rest.transform;

import com.teamcoworkers.hostera.platform.iam.application.commands.SignUpCommand;
import com.teamcoworkers.hostera.platform.iam.domain.model.entities.Role;
import com.teamcoworkers.hostera.platform.iam.interfaces.rest.resources.SignUpResource;



/**
 * Assembler that translates {@link SignUpResource} into {@link SignUpCommand}.
 */
public class SignUpCommandFromResourceAssembler {
    /**
     * Converts the incoming sign-up resource to an application command.
     *
     * @param resource sign-up payload from REST API
     * @return sign-up command consumed by the application layer
     */
    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        return new SignUpCommand(resource.username(), resource.password(), java.util.List.of(Role.getDefaultRole()));
    }
}

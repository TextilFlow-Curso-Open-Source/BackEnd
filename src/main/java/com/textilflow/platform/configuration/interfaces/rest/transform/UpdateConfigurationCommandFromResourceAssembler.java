package com.textilflow.platform.configuration.interfaces.rest.transform;

import com.textilflow.platform.configuration.domain.model.commands.UpdateConfigurationCommand;
import com.textilflow.platform.configuration.domain.model.valueobjects.*;
import com.textilflow.platform.configuration.interfaces.rest.resources.UpdateConfigurationResource;

/**
 * Assembler to transform UpdateConfigurationResource to UpdateConfigurationCommand
 */
public class UpdateConfigurationCommandFromResourceAssembler {

    /**
     * Transform UpdateConfigurationResource to UpdateConfigurationCommand
     */
    public static UpdateConfigurationCommand toCommandFromResource(Long configurationId,
                                                                   UpdateConfigurationResource resource) {
        return new UpdateConfigurationCommand(
                configurationId,
                Language.fromString(resource.language()),
                ViewMode.fromString(resource.viewMode()),
                resource.subscriptionPlan() != null ?
                        SubscriptionPlan.fromString(resource.subscriptionPlan()) :
                        null
        );
    }
}
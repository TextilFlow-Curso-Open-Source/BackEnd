package com.textilflow.platform.configuration.domain.model.commands;

import com.textilflow.platform.configuration.domain.model.valueobjects.*;

/**
 * Update configuration command
 */
public record UpdateConfigurationCommand(
        Long configurationId,
        Language language,
        ViewMode viewMode,
        SubscriptionPlan subscriptionPlan
) {
}
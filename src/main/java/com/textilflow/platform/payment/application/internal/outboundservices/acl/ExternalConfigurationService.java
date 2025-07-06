package com.textilflow.platform.payment.application.internal.outboundservices.acl;

import com.textilflow.platform.configuration.interfaces.acl.ConfigurationContextFacade;
import com.textilflow.platform.configuration.domain.services.ConfigurationCommandService;
import com.textilflow.platform.configuration.domain.model.commands.UpdateConfigurationCommand;
import com.textilflow.platform.configuration.domain.model.queries.GetConfigurationByUserIdQuery;
import com.textilflow.platform.configuration.domain.services.ConfigurationQueryService;
import com.textilflow.platform.configuration.domain.model.valueobjects.SubscriptionPlan;
import com.textilflow.platform.configuration.domain.model.valueobjects.Language;
import com.textilflow.platform.configuration.domain.model.valueobjects.ViewMode;
import org.springframework.stereotype.Service;

/**
 * External configuration service for payment context
 * Provides anti-corruption layer to interact with Configuration bounded context
 */
@Service
public class ExternalConfigurationService {

    private final ConfigurationContextFacade configurationContextFacade;
    private final ConfigurationCommandService configurationCommandService;
    private final ConfigurationQueryService configurationQueryService;

    public ExternalConfigurationService(ConfigurationContextFacade configurationContextFacade,
                                        ConfigurationCommandService configurationCommandService,
                                        ConfigurationQueryService configurationQueryService) {
        this.configurationContextFacade = configurationContextFacade;
        this.configurationCommandService = configurationCommandService;
        this.configurationQueryService = configurationQueryService;
    }

    /**
     * Check if user exists in the system
     */
    public boolean userExists(Long userId) {
        return configurationContextFacade.hasConfiguration(userId);
    }

    /**
     * Update user's subscription plan after successful payment
     */
    public void updateSubscriptionPlan(Long userId, String subscriptionPlan) {
        System.out.println("=== Updating subscription via Configuration context ===");

        // Get current configuration
        var configQuery = new GetConfigurationByUserIdQuery(userId);
        var configOpt = configurationQueryService.handle(configQuery);

        if (configOpt.isEmpty()) {
            throw new IllegalArgumentException("Configuration not found for user: " + userId);
        }

        var config = configOpt.get();

        // Create update command with current values + new subscription plan
        var updateCommand = new UpdateConfigurationCommand(
                config.getId(),
                config.getLanguage(),           // Keep current language
                config.getViewMode(),           // Keep current view mode
                SubscriptionPlan.fromString(subscriptionPlan)  // Update subscription plan
        );

        // Execute update
        var result = configurationCommandService.handle(updateCommand);

        if (result.isEmpty()) {
            throw new RuntimeException("Failed to update subscription plan for user: " + userId);
        }

        System.out.println("Subscription plan updated successfully to: " + subscriptionPlan);
    }

    /**
     * Get current subscription plan for user
     */
    public String getCurrentSubscriptionPlan(Long userId) {
        return configurationContextFacade.getSubscriptionPlan(userId);
    }

    /**
     * Check if user has active subscription
     */
    public boolean hasActiveSubscription(Long userId) {
        return configurationContextFacade.hasActiveSubscription(userId);
    }
}
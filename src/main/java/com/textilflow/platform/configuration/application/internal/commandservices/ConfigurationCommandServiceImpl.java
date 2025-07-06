package com.textilflow.platform.configuration.application.internal.commandservices;

import com.textilflow.platform.configuration.domain.model.aggregates.Configuration;
import com.textilflow.platform.configuration.domain.model.commands.ActivateSubscriptionCommand;
import com.textilflow.platform.configuration.domain.model.commands.CreateConfigurationCommand;
import com.textilflow.platform.configuration.domain.model.commands.UpdateConfigurationCommand;
import com.textilflow.platform.configuration.domain.model.valueobjects.SubscriptionStatus;
import com.textilflow.platform.configuration.domain.services.ConfigurationCommandService;
import com.textilflow.platform.configuration.infrastructure.persistence.jpa.repositories.ConfigurationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConfigurationCommandServiceImpl implements ConfigurationCommandService {

    private final ConfigurationRepository configurationRepository;

    public ConfigurationCommandServiceImpl(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    @Override
    public Optional<Configuration> handle(CreateConfigurationCommand command) {
        var configuration = new Configuration(
                command.userId(),
                command.language(),
                command.viewMode(),
                command.subscriptionPlan()
        );
        // Constructor already sets subscriptionStatus = PENDING by default

        return Optional.of(configurationRepository.save(configuration));
    }

    @Override
    public Optional<Configuration> handle(UpdateConfigurationCommand command) {
        var result = configurationRepository.findById(command.configId());

        if (result.isEmpty()) {
            throw new IllegalArgumentException("Configuration with id " + command.configId() + " does not exist");
        }

        var configurationToUpdate = result.get();

        // *** NEW: Logic to handle different types of updates ***

        // If subscriptionStatus and subscriptionPlan are provided, it's a subscription activation
        if (command.subscriptionStatus() != null && command.subscriptionPlan() != null) {
            if (command.subscriptionStatus() == SubscriptionStatus.ACTIVE) {
                configurationToUpdate.activateSubscription(command.subscriptionPlan());
            } else {
                configurationToUpdate.updateSubscriptionStatus(command.subscriptionStatus());
                if (command.subscriptionPlan() != null) {
                    configurationToUpdate.updateSubscriptionPlan(command.subscriptionPlan());
                }
            }
        }
        // If only subscriptionPlan is provided (without status), update only the plan
        else if (command.subscriptionPlan() != null) {
            configurationToUpdate.updateSubscriptionPlan(command.subscriptionPlan());
        }
        // If language and viewMode are provided, update preferences
        else if (command.language() != null && command.viewMode() != null) {
            configurationToUpdate.updateSettings(command.language(), command.viewMode());
        }
        // If only subscriptionStatus is provided
        else if (command.subscriptionStatus() != null) {
            configurationToUpdate.updateSubscriptionStatus(command.subscriptionStatus());
        }

        try {
            var updatedConfiguration = configurationRepository.save(configurationToUpdate);
            return Optional.of(updatedConfiguration);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while updating configuration: " + e.getMessage());
        }
    }

    // *** MISSING METHOD: Handle ActivateSubscriptionCommand ***
    @Override
    public Optional<Configuration> handle(ActivateSubscriptionCommand command) {
        // Find configuration by userId
        var result = configurationRepository.findByUserId_Value(command.userId());

        if (result.isEmpty()) {
            throw new IllegalArgumentException("Configuration for user " + command.userId() + " does not exist");
        }

        var configurationToUpdate = result.get();

        // Activate subscription
        configurationToUpdate.activateSubscription(command.subscriptionPlan());

        try {
            var updatedConfiguration = configurationRepository.save(configurationToUpdate);
            return Optional.of(updatedConfiguration);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while activating subscription: " + e.getMessage());
        }
    }
}
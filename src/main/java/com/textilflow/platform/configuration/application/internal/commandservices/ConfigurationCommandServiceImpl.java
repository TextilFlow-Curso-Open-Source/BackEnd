package com.textilflow.platform.configuration.application.internal.commandservices;

import com.textilflow.platform.configuration.domain.model.aggregates.Configuration;
import com.textilflow.platform.configuration.domain.model.commands.CreateConfigurationCommand;
import com.textilflow.platform.configuration.domain.model.commands.UpdateConfigurationCommand;
import com.textilflow.platform.configuration.domain.model.events.ConfigurationCreatedEvent;
import com.textilflow.platform.configuration.domain.model.events.ConfigurationUpdatedEvent;
import com.textilflow.platform.configuration.domain.services.ConfigurationCommandService;
import com.textilflow.platform.configuration.infrastructure.persistence.jpa.repositories.ConfigurationRepository;
import com.textilflow.platform.configuration.application.internal.outboundservices.acl.ExternalIamService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Configuration command service implementation
 */
@Service
public class ConfigurationCommandServiceImpl implements ConfigurationCommandService {

    private final ConfigurationRepository configurationRepository;
    private final ExternalIamService externalIamService;

    public ConfigurationCommandServiceImpl(ConfigurationRepository configurationRepository,
                                           ExternalIamService externalIamService) {
        this.configurationRepository = configurationRepository;
        this.externalIamService = externalIamService;
    }

    @Override
    public Optional<Configuration> handle(CreateConfigurationCommand command) {
        System.out.println("=== DEBUG: Iniciando creación de configuración ===");
        System.out.println("UserId: " + command.userId());
        System.out.println("Language: " + command.language());
        System.out.println("ViewMode: " + command.viewMode());
        System.out.println("SubscriptionPlan: " + command.subscriptionPlan());

        // Validate user exists
        System.out.println("=== Validando si usuario existe ===");
        boolean userExists = externalIamService.userExists(command.userId());
        System.out.println("Usuario existe: " + userExists);

        if (!userExists) {
            System.out.println("ERROR: Usuario no existe");
            throw new IllegalArgumentException("User with ID " + command.userId() + " does not exist");
        }

        // Check if configuration already exists for this user
        System.out.println("=== Verificando configuración existente ===");
        Optional<Configuration> existingConfig = configurationRepository.findByUserId_Value(command.userId());
        System.out.println("Configuración existente: " + existingConfig.isPresent());

        if (existingConfig.isPresent()) {
            System.out.println("ERROR: Configuración ya existe");
            throw new IllegalArgumentException("Configuration already exists for user " + command.userId());
        }

        System.out.println("=== Creando configuración ===");

        // Create configuration
        var configuration = new Configuration(
                command.userId(),
                command.language(),
                command.viewMode(),
                command.subscriptionPlan()
        );

        System.out.println("=== Guardando configuración ===");
        var savedConfiguration = configurationRepository.save(configuration);
        System.out.println("Configuración guardada con ID: " + savedConfiguration.getId());

        // TODO: Publish domain events when event system is implemented
        // var event = new ConfigurationCreatedEvent(...)
        // eventPublisher.publish(event);

        return Optional.of(savedConfiguration);
    }

    @Override
    public Optional<Configuration> handle(UpdateConfigurationCommand command) {
        var configurationOpt = configurationRepository.findById(command.configurationId());

        if (configurationOpt.isEmpty()) {
            return Optional.empty();
        }

        var configuration = configurationOpt.get();

        // Update settings
        configuration.updateSettings(command.language(), command.viewMode());

        // Update subscription if provided
        if (command.subscriptionPlan() != null) {
            configuration.updateSubscriptionPlan(command.subscriptionPlan());
        }

        // Check and downgrade subscription if expired
        configuration.checkAndDowngradeSubscription();

        var savedConfiguration = configurationRepository.save(configuration);

        // TODO: Publish domain events when event system is implemented
        // var event = new ConfigurationUpdatedEvent(...)
        // eventPublisher.publish(event);

        return Optional.of(savedConfiguration);
    }
}
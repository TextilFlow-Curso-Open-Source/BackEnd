package com.textilflow.platform.configuration.application.acl;

import com.textilflow.platform.configuration.domain.model.queries.GetConfigurationByUserIdQuery;
import com.textilflow.platform.configuration.domain.services.ConfigurationQueryService;
import com.textilflow.platform.configuration.interfaces.acl.ConfigurationContextFacade;
import org.springframework.stereotype.Service;

/**
 * Configuration context facade implementation
 */
@Service
public class ConfigurationContextFacadeImpl implements ConfigurationContextFacade {

    private final ConfigurationQueryService configurationQueryService;

    public ConfigurationContextFacadeImpl(ConfigurationQueryService configurationQueryService) {
        this.configurationQueryService = configurationQueryService;
    }

    @Override
    public boolean hasConfiguration(Long userId) {
        return configurationQueryService.handle(new GetConfigurationByUserIdQuery(userId)).isPresent();
    }

    @Override
    public String getUserLanguage(Long userId) {
        var configuration = configurationQueryService.handle(new GetConfigurationByUserIdQuery(userId));
        return configuration.map(config -> config.getLanguage().getValue()).orElse("es");
    }

    @Override
    public String getUserTheme(Long userId) {
        var configuration = configurationQueryService.handle(new GetConfigurationByUserIdQuery(userId));
        return configuration.map(config -> config.getViewMode().getValue()).orElse("auto");
    }

    @Override
    public String getSubscriptionPlan(Long userId) {
        var configuration = configurationQueryService.handle(new GetConfigurationByUserIdQuery(userId));
        return configuration.map(config -> config.getSubscriptionPlan().getValue()).orElse("basic");
    }

    @Override
    public boolean hasActiveSubscription(Long userId) {
        var configuration = configurationQueryService.handle(new GetConfigurationByUserIdQuery(userId));
        if (configuration.isEmpty()) {
            return false;
        }

        var config = configuration.get();
        return !config.isSubscriptionExpired();
    }
}
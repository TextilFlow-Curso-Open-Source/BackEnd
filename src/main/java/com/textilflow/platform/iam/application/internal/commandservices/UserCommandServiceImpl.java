package com.textilflow.platform.iam.application.internal.commandservices;

import com.textilflow.platform.iam.domain.model.aggregates.User;
import com.textilflow.platform.iam.domain.model.commands.SignInCommand;
import com.textilflow.platform.iam.domain.model.commands.SignUpCommand;
import com.textilflow.platform.iam.domain.model.commands.UpdateUserRoleCommand;
import com.textilflow.platform.iam.domain.model.commands.UpdateUserDataCommand; // ✅ AGREGAR ESTE IMPORT
import com.textilflow.platform.iam.domain.model.events.UserRegisteredEvent;
import com.textilflow.platform.iam.domain.model.events.UserRoleUpdatedEvent;
import com.textilflow.platform.iam.domain.services.UserCommandService;
import com.textilflow.platform.iam.infrastructure.hashing.HashingService;
import com.textilflow.platform.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.textilflow.platform.iam.infrastructure.tokens.TokenService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * User command service implementation
 */
@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final ApplicationEventPublisher eventPublisher;

    public UserCommandServiceImpl(UserRepository userRepository,
                                  HashingService hashingService,
                                  TokenService tokenService,
                                  ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<User> handle(SignUpCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new RuntimeException("Email already exists");
        }

        var encodedPassword = hashingService.encode(command.password());
        var user = new User(
                command.name(),
                command.email(),
                encodedPassword,
                command.country(),
                command.city(),
                command.address(),
                command.phone(),
                command.role()
        );

        var savedUser = userRepository.save(user);

        // Publish domain event for user registration
        var userRegisteredEvent = new UserRegisteredEvent(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getCountry(),
                savedUser.getCity(),
                savedUser.getAddress(),
                savedUser.getPhone()
        );
        eventPublisher.publishEvent(userRegisteredEvent);

        return Optional.of(savedUser);
    }

    @Override
    public Optional<ImmutablePair<User, String>> handle(SignInCommand command) {
        var user = userRepository.findByEmail(command.email());
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        if (!hashingService.matches(command.password(), user.get().getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        var token = tokenService.generateToken(user.get().getEmail());
        return Optional.of(ImmutablePair.of(user.get(), token));
    }

    @Override
    public Optional<User> handle(UpdateUserRoleCommand command) {
        var user = userRepository.findById(command.userId());
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        var oldRole = user.get().getRole();
        user.get().updateRole(command.newRole());
        var updatedUser = userRepository.save(user.get());

        // Publish domain event for role update
        var userRoleUpdatedEvent = new UserRoleUpdatedEvent(
                updatedUser.getId(),
                oldRole,
                command.newRole()
        );
        eventPublisher.publishEvent(userRoleUpdatedEvent);

        return Optional.of(updatedUser);
    }

    @Override
    public Optional<User> handle(UpdateUserDataCommand command) {
        var user = userRepository.findById(command.userId());
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        var userEntity = user.get();
        userEntity.setName(command.name());
        userEntity.setEmail(command.email());
        userEntity.setCountry(command.country());
        userEntity.setCity(command.city());
        userEntity.setAddress(command.address());
        userEntity.setPhone(command.phone());

        var updatedUser = userRepository.save(userEntity);
        return Optional.of(updatedUser);
    }
}

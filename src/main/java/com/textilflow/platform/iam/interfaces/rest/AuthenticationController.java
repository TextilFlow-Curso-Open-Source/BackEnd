package com.textilflow.platform.iam.interfaces.rest;

import com.textilflow.platform.iam.domain.services.UserCommandService;
import com.textilflow.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.textilflow.platform.iam.interfaces.rest.resources.SignInResource;
import com.textilflow.platform.iam.interfaces.rest.resources.SignUpResource;
import com.textilflow.platform.iam.interfaces.rest.resources.UserResource;
import com.textilflow.platform.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.textilflow.platform.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.textilflow.platform.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import com.textilflow.platform.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication controller for user registration and authentication
 */
@RestController
@RequestMapping(value = "/api/v1/authentication", produces = "application/json")
@Tag(name = "Authentication", description = "Authentication Endpoints")
public class AuthenticationController {

    private final UserCommandService userCommandService;

    public AuthenticationController(UserCommandService userCommandService) {
        this.userCommandService = userCommandService;
    }

    /**
     * Register a new user
     * @param signUpResource the sign up resource
     * @return the created user resource
     */
    @PostMapping("/sign-up")
    public ResponseEntity<UserResource> signUp(@RequestBody SignUpResource signUpResource) {
        var signUpCommand = SignUpCommandFromResourceAssembler.toCommandFromResource(signUpResource);
        var user = userCommandService.handle(signUpCommand);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user.get());
        return new ResponseEntity<>(userResource, HttpStatus.CREATED);
    }

    /**
     * Authenticate a user
     * @param signInResource the sign in resource
     * @return the authenticated user resource with token
     */
    @PostMapping("/sign-in")
    public ResponseEntity<AuthenticatedUserResource> signIn(@RequestBody SignInResource signInResource) {
        var signInCommand = SignInCommandFromResourceAssembler.toCommandFromResource(signInResource);
        var authenticatedUser = userCommandService.handle(signInCommand);
        if (authenticatedUser.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        var user = authenticatedUser.get().getLeft();
        var token = authenticatedUser.get().getRight();
        var authenticatedUserResource = AuthenticatedUserResourceFromEntityAssembler
                .toResourceFromEntity(user, token);
        return ResponseEntity.ok(authenticatedUserResource);
    }
}
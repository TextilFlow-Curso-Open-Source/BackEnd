package com.textilflow.platform.iam.infrastructure.configuration;

import com.textilflow.platform.iam.infrastructure.hashing.BCryptHashingService;
import com.textilflow.platform.iam.infrastructure.tokens.JwtTokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Security configuration for TextilFlow Platform
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final BCryptHashingService hashingService;
    private final JwtTokenService tokenService;

    public SecurityConfiguration(BCryptHashingService hashingService, JwtTokenService tokenService) {
        this.hashingService = hashingService;
        this.tokenService = tokenService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // OPTIONS requests para CORS - DEBE IR PRIMERO
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Endpoints de documentación - ANTES del filtro JWT
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/webjars/**", "/swagger-resources/**").permitAll()

                        // Endpoints de monitoreo
                        .requestMatchers("/actuator/**").permitAll()

                        // Endpoints de autenticación - ANTES del filtro JWT
                        .requestMatchers("/api/v1/authentication/**").permitAll()

                        // Endpoints protegidos - DESPUÉS del filtro JWT
                        .requestMatchers("/api/v1/users/**").authenticated()
                        .requestMatchers("/api/v1/profiles/**").authenticated()
                        .requestMatchers("/api/v1/businessmen/**").authenticated()
                        .requestMatchers("/api/v1/suppliers/**").authenticated()

                        // Cualquier otra petición debe estar autenticada
                        .anyRequest().authenticated()
                )
                // EL FILTRO JWT SE AGREGA DESPUÉS DE DEFINIR LAS REGLAS
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public OncePerRequestFilter jwtAuthenticationFilter() {
        return new OncePerRequestFilter() {

            @Override
            protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
                String path = request.getRequestURI();
                String method = request.getMethod();

                // NO aplicar el filtro JWT a estos endpoints
                boolean shouldSkip = path.startsWith("/api/v1/authentication/") ||
                        path.startsWith("/swagger-ui/") ||
                        path.startsWith("/v3/api-docs/") ||
                        path.equals("/swagger-ui.html") ||
                        path.startsWith("/webjars/") ||
                        path.startsWith("/swagger-resources/") ||
                        "OPTIONS".equals(method);

                // Log para debugging
                if (shouldSkip) {
                    System.out.println("Skipping JWT filter for: " + method + " " + path);
                }

                return shouldSkip;
            }

            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {

                String authHeader = request.getHeader("Authorization");

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);

                    try {
                        if (tokenService.validateToken(token)) {
                            String email = tokenService.getEmailFromToken(token);

                            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                                UsernamePasswordAuthenticationToken auth =
                                        new UsernamePasswordAuthenticationToken(email, null, List.of());
                                SecurityContextHolder.getContext().setAuthentication(auth);
                            }
                        }
                    } catch (Exception e) {
                        // Token inválido, continuar sin autenticación
                    }
                }

                filterChain.doFilter(request, response);
            }
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
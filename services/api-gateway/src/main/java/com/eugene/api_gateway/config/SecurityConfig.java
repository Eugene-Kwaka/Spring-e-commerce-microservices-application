package com.eugene.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity // Enables Spring Security for web applications using the WebFlux framework.
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity) {
        serverHttpSecurity
                // Disables Cross-Site Request Forgery (CSRF) protection.
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                // Begins the configuration for authorization rules for different paths.
                .authorizeExchange(exchange -> exchange
                        // All paths with from the "/eureka/**" path are allowed without authentication.
                        .pathMatchers("(/eureka/**)")
                        .permitAll()
                        // All other paths require authentication.
                        .anyExchange()
                        .authenticated()
                )
                // Configures the application as an OAuth2 resource server that validates JWT tokens.
                // It uses default settings which connect to the Keycloak server specified in the application.yml file.
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        // Builds and returns the configured security filter chain.
        return serverHttpSecurity.build();
    }
}

package com.eventsphere.gateway;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Frontend is served from this origin.
        configuration.setAllowedOrigins(
                List.of("http://localhost:8084")
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setExposedHeaders(
                List.of("Authorization")
        );

        // We are using Authorization header / JWT,
        // not browser cookies.
        configuration.setAllowCredentials(false);

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            CorsConfigurationSource corsConfigurationSource) {

        return http
                // Disable CSRF because this is a stateless REST API.
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // Enable CORS using our configuration above.
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource)
                )

                // The custom AuthGlobalFilter handles JWT authentication.
                .authorizeExchange(exchange -> exchange

                        // Browser CORS preflight must be allowed.
                        .pathMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        // Auth endpoints are public.
                        .pathMatchers(
                                "/api/auth/**"
                        ).permitAll()

                        // All other authentication decisions are handled
                        // by AuthGlobalFilter.
                        .anyExchange().permitAll()
                )

                .build();
    }
}
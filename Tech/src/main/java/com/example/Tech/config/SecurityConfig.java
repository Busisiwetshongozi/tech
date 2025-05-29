package com.example.Tech.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {}) // Enable CORS with default settings (configured elsewhere)
                .csrf(csrf -> csrf.disable()) // ✅ Use lambda to disable CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/protected/user-info",
                                "/api/users/login",
                                "/api/auth/register",
                                "/api/auth/me",
                                "/api/users/**",
                                "/api/orders",
                                "/api/orders/create",
                                "/api/orders/user",
                                "/api/products/**",
                                "/api/categories/**",
                                "/api/reviews/create",
                                "/api/payfast/**",
                                "/api/orders/{id}/initiate-payment",
                                "/api/products/{id}/discount",
                                "/api/reviews/{productId}/reviews").permitAll()
                        .anyRequest().denyAll()
                );

        return http.build();
    }
}

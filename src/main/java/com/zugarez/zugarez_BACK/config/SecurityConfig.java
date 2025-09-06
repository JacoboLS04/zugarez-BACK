package com.zugarez.zugarez_BACK.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain monitoringSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/monitoring/**", "/actuator/**") // Maneja solo estas rutas
            .authorizeHttpRequests()
                .requestMatchers("/monitoring/health").permitAll() // Permitir acceso público
                .anyRequest().authenticated() // Proteger el resto de las rutas en /monitoring/**
            .and()
            .csrf().disable(); // Deshabilitar CSRF para simplificar pruebas

        return http.build();
    }
}

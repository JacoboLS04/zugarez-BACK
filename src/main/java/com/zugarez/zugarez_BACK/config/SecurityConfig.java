package com.zugarez.zugarez_BACK.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests()
                // Permitir acceso público a estos endpoints
                .requestMatchers("/monitoring/health", "/actuator/**").permitAll()
                // Mantener los puntos de acceso existentes
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated() // Proteger el resto de los endpoints
            .and()
            .csrf().disable(); // Deshabilitar CSRF para simplificar pruebas

        return http.build();
    }
}

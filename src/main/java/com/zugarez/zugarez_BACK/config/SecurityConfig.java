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
            .securityMatcher("/actuator/**", "/monitoring/**", "/", "/auth/**", "/static/**") // Incluye rutas del frontend y autenticación
            .authorizeHttpRequests()
                .requestMatchers("/actuator/prometheus", "/monitoring/health").permitAll() // Permitir acceso público al monitoreo
                .requestMatchers("/", "/auth/**", "/static/**").permitAll() // Permitir acceso público al frontend y autenticación
                .anyRequest().authenticated() // Proteger el resto de las rutas
            .and()
            .csrf().disable(); // Deshabilitar CSRF para simplificar pruebas

        return http.build();
    }
}

package com.zugarez.zugarez_BACK.security.config;

import com.zugarez.zugarez_BACK.security.jwt.JwtEntryPoint;
import com.zugarez.zugarez_BACK.security.jwt.JwtFilter;
import com.zugarez.zugarez_BACK.security.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class MainSecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtEntryPoint jwtEntryPoint;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        AuthenticationManager authenticationManager = builder.build();
        http.authenticationManager(authenticationManager);

        http
            .securityMatcher("/api/**") // Maneja solo las rutas /api/**
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll() // Acceso público
                .requestMatchers("/api/admin/**").hasRole("ADMIN") // Solo para ADMIN
                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN") // Para USER y ADMIN
                .anyRequest().authenticated() // Proteger el resto de las rutas en /api/**
            )
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
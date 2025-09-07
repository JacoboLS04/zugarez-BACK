package com.zugarez.zugarez_BACK.security.jwt;

import com.zugarez.zugarez_BACK.security.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        // Omitir autenticación JWT para endpoints de Actuator y API de Prometheus
        String requestPath = req.getServletPath();
        if (shouldNotFilter(req)) {
            System.out.println("=== JWT FILTER ===");
            System.out.println("Omitiendo JWT Filter para endpoint permitido: " + req.getRequestURL());
            chain.doFilter(req, res);
            return;
        }

        String token = getToken(req);
        System.out.println("=== JWT FILTER ===");
        System.out.println("Request URL: " + req.getRequestURL());
        System.out.println("Authorization Header: " + req.getHeader("Authorization"));
        System.out.println("Token extraído: " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));
        
        try {
            if (token != null && jwtProvider.validateToken(token)) {
                System.out.println("Token válido, obteniendo username...");
                String username = jwtProvider.getUsernameFromToken(token);
                System.out.println("Username del token: " + username);
                
                UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
                System.out.println("UserDetails cargado: " + userDetails.getUsername());
                
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                System.out.println("Autenticación establecida correctamente");
            } else {
                System.out.println("Token nulo o inválido");
            }
        } catch (UsernameNotFoundException e) {
            System.out.println("Usuario no encontrado: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error en JWT Filter: " + e.getMessage());
        }
        chain.doFilter(req, res);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/actuator/") || 
               path.startsWith("/api/v1/");
    }

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer "))
            return header.replace("Bearer ", "");
        return null;
    }
}


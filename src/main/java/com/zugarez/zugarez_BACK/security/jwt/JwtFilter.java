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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Security filter for processing JWT authentication on incoming requests.
 * Extracts and validates JWT tokens, setting authentication in the security context.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;

    /**
     * Filters incoming HTTP requests for JWT authentication.
     * Skips Actuator and public endpoints.
     * @param req the HTTP request
     * @param res the HTTP response
     * @param chain the filter chain
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an input or output error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        String requestPath = req.getServletPath();
        
        // Omitir autenticación JWT para endpoints públicos
        if (requestPath.startsWith("/actuator/") || requestPath.startsWith("/auth/")) {
            chain.doFilter(req, res);
            return;
        }

        String token = getToken(req);
        
        try {
            if (token != null && jwtProvider.validateToken(token)) {
                String username = jwtProvider.getUsernameFromToken(token);
                UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
                
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                
                logger.debug("Autenticación establecida para: {}", username);
            }
        } catch (Exception e) {
            logger.error("Error en JWT Filter: {}", e.getMessage());
        }
        
        chain.doFilter(req, res);
    }

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer "))
            return header.substring(7);
        return null;
    }
}

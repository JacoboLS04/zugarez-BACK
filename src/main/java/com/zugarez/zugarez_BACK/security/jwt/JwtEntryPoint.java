package com.zugarez.zugarez_BACK.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zugarez.zugarez_BACK.global.dto.MessageDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Entry point for handling unauthorized access attempts with JWT.
 * Sends a JSON response with an error message when authentication fails.
 */
@Component
public class JwtEntryPoint implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(JwtEntryPoint.class);

    /**
     * Handles unauthorized access by sending a JSON error response.
     * @param req the HTTP request
     * @param res the HTTP response
     * @param e the authentication exception
     * @throws IOException if an input or output error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException e) throws IOException, ServletException {
        logger.error("Token no encontrado o inválido");
        MessageDto dto = new MessageDto(HttpStatus.UNAUTHORIZED, "Token no encontrado o inválido");
        res.setContentType("application/json");
        res.setStatus(dto.getStatus().value());
        res.getWriter().write(new ObjectMapper().writeValueAsString(dto));
        res.getWriter().flush();
        res.getWriter().close();
    }
}
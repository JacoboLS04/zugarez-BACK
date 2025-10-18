package com.zugarez.zugarez_BACK.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zugarez.zugarez_BACK.security.entity.UserEntity;
import com.zugarez.zugarez_BACK.security.repository.UserEntityRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.Optional;

/**
 * Interceptor que actúa como middleware de autenticación JWT.
 * Añade request.setAttribute("authenticatedUser", user) cuando el token es válido.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserEntityRepository userRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            mapper.writeValue(response.getWriter(), Map.of("error", "Token no proporcionado"));
            return false;
        }
        String token = auth.substring(7).trim();
        if (!jwtProvider.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            mapper.writeValue(response.getWriter(), Map.of("error", "Token inválido o expirado"));
            return false;
        }
        Integer userId = jwtProvider.getUserIdFromToken(token);
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            mapper.writeValue(response.getWriter(), Map.of("error", "Token inválido o expirado"));
            return false;
        }
        Optional<UserEntity> uOpt = userRepository.findById(userId);
        if (uOpt.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json");
            mapper.writeValue(response.getWriter(), Map.of("error", "Usuario no encontrado"));
            return false;
        }
        UserEntity user = uOpt.get();
        if (!user.isVerified() && user.getDeactivatedAt() != null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            mapper.writeValue(response.getWriter(), Map.of("error", "Cuenta desactivada"));
            return false;
        }
        request.setAttribute("authenticatedUser", user);
        return true;
    }
}

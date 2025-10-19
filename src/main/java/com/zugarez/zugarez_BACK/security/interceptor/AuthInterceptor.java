package com.zugarez.zugarez_BACK.security.interceptor;

import com.zugarez.zugarez_BACK.security.entity.UserEntity;
import com.zugarez.zugarez_BACK.security.jwt.JwtProvider;
import com.zugarez.zugarez_BACK.security.repository.UserEntityRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Optional;

/**
 * Interceptor para validar el estado del usuario en cada petición autenticada.
 * Bloquea usuarios desactivados (baja voluntaria) antes de que lleguen al controlador.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserEntityRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            logger.info("=== AUTH INTERCEPTOR ===");
            logger.info("Request URL: {} | Method: {}", request.getRequestURL(), request.getMethod());
            
            String token = getTokenFromRequest(request);
            
            if (token == null) {
                logger.warn("Token ausente en la petición");
                return true; // Dejar pasar, Spring Security se encargará
            }
            
            logger.debug("Token extraído (longitud: {})", token.length());
            
            if (!jwtProvider.validateToken(token)) {
                logger.error("Token inválido o expirado");
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o expirado");
                return false;
            }
            
            logger.info("Token válido");
            String username = jwtProvider.getUsernameFromToken(token);
            
            if (username == null || username.trim().isEmpty()) {
                logger.error("Username extraído del token es nulo o vacío");
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
                return false;
            }
            
            logger.info("Username del token: {}", username);
            
            Optional<UserEntity> userOpt = userRepository.findByUsername(username);
            
            if (!userOpt.isPresent()) {
                logger.warn("Usuario no encontrado en BD: {} - Dejando pasar para que Spring Security maneje", username);
                return true; // Dejar que Spring Security maneje este caso
            }
            
            UserEntity user = userOpt.get();
            logger.info("Usuario encontrado: {} (ID: {})", user.getUsername(), user.getId());
            
            // Solo bloquear si el usuario está desactivado
            if (user.getDeactivatedAt() != null) {
                logger.warn("Usuario desactivado - Bloqueando acceso: {}", username);
                sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, 
                    String.format("{\"error\":\"Tu cuenta ha sido desactivada\",\"deactivatedAt\":\"%s\"}", 
                    user.getDeactivatedAt()));
                return false;
            }
            
            // Agregar el usuario al request para uso en controladores
            request.setAttribute("authenticatedUser", user);
            logger.info("Usuario autenticado correctamente y agregado al request");
            
            return true;
            
        } catch (Exception e) {
            logger.error("Error en AuthInterceptor: {}", e.getMessage(), e);
            try {
                sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno de autenticación");
            } catch (IOException ioException) {
                logger.error("Error al enviar respuesta de error: {}", ioException.getMessage());
            }
            return false;
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        if (response.isCommitted()) {
            logger.warn("Response ya fue enviada, no se puede modificar");
            return;
        }
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Escapar comillas en el mensaje si es necesario
        String jsonMessage = message.startsWith("{") ? message : "{\"error\":\"" + message.replace("\"", "\\\"") + "\"}";
        response.getWriter().write(jsonMessage);
        response.getWriter().flush();
    }
}

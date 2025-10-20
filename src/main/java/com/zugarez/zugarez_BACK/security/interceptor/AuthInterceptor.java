package com.zugarez.zugarez_BACK.security.interceptor;

import com.zugarez.zugarez_BACK.security.entity.UserEntity;
import com.zugarez.zugarez_BACK.security.repository.UserEntityRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private UserEntityRepository userRepository;

    /**
     * Pre-handles the request to validate user authentication and status.
     * Checks if the user is deactivated and blocks access if necessary.
     * @param request HTTP request
     * @param response HTTP response
     * @param handler handler
     * @return true if the request should proceed, false otherwise
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            // Obtener autenticación del SecurityContext (ya establecida por JwtFilter)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                logger.debug("Sin autenticación en SecurityContext - permitiendo que Spring Security maneje");
                return true;
            }
            
            Object principal = authentication.getPrincipal();
            if (!(principal instanceof UserDetails)) {
                logger.debug("Principal no es UserDetails - permitiendo acceso");
                return true;
            }
            
            String username = ((UserDetails) principal).getUsername();
            logger.debug("Verificando estado del usuario: {}", username);
            
            Optional<UserEntity> userOpt = userRepository.findByUsername(username);
            
            if (!userOpt.isPresent()) {
                logger.warn("Usuario autenticado no encontrado en BD: {}", username);
                return true; // Usuario válido en JWT pero no en BD (caso raro)
            }
            
            UserEntity user = userOpt.get();
            
            // Solo bloquear si el usuario está desactivado
            if (user.getDeactivatedAt() != null) {
                logger.warn("Usuario desactivado intentando acceder - Bloqueando: {}", username);
                sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, 
                    String.format("{\"error\":\"Tu cuenta ha sido desactivada\",\"deactivatedAt\":\"%s\"}", 
                    user.getDeactivatedAt()));
                return false;
            }
            
            // Agregar el usuario al request para uso en controladores
            request.setAttribute("authenticatedUser", user);
            logger.debug("Usuario verificado y agregado al request: {}", username);
            
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
    
    /**
     * Sends an error response with the specified status and message.
     * @param response HTTP response
     * @param status HTTP status code
     * @param message Error message
     * @throws IOException if writing the response fails
     */
    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        if (response.isCommitted()) {
            logger.warn("Response ya fue enviada, no se puede modificar");
            return;
        }
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(message);
        response.getWriter().flush();
    }
}

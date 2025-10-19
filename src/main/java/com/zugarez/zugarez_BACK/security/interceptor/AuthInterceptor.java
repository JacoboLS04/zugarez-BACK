package com.zugarez.zugarez_BACK.security.interceptor;

import com.zugarez.zugarez_BACK.security.entity.UserEntity;
import com.zugarez.zugarez_BACK.security.jwt.JwtProvider;
import com.zugarez.zugarez_BACK.security.repository.UserEntityRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

/**
 * Interceptor para validar el estado del usuario en cada petición autenticada.
 * Bloquea usuarios desactivados (baja voluntaria) antes de que lleguen al controlador.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserEntityRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Obtener el token del header
        String token = getTokenFromRequest(request);
        
        if (token != null && jwtProvider.validateToken(token)) {
            String username = jwtProvider.getUsernameFromToken(token);
            Optional<UserEntity> userOpt = userRepository.findByUsername(username);
            
            if (userOpt.isPresent()) {
                UserEntity user = userOpt.get();
                
                // Bloquear usuarios desactivados (baja voluntaria)
                if (user.getDeactivatedAt() != null) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Tu cuenta ha sido desactivada\",\"deactivatedAt\":\"" + user.getDeactivatedAt() + "\"}");
                    return false;
                }
                
                // Agregar el usuario autenticado a la request para uso posterior
                request.setAttribute("authenticatedUser", user);
            }
        }
        
        return true;
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

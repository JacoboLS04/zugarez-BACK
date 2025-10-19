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
        System.out.println("=== AUTH INTERCEPTOR ===");
        System.out.println("Request URL: " + request.getRequestURL());
        System.out.println("Method: " + request.getMethod());
        
        String token = getTokenFromRequest(request);
        System.out.println("Token extraído: " + (token != null ? "Presente (longitud: " + token.length() + ")" : "Ausente"));
        
        if (token != null && jwtProvider.validateToken(token)) {
            System.out.println("✅ Token válido");
            String username = jwtProvider.getUsernameFromToken(token);
            System.out.println("Username del token: " + username);
            
            Optional<UserEntity> userOpt = userRepository.findByUsername(username);
            
            if (userOpt.isPresent()) {
                UserEntity user = userOpt.get();
                System.out.println("✅ Usuario encontrado: " + user.getUsername() + " (ID: " + user.getId() + ")");
                
                if (user.getDeactivatedAt() != null) {
                    System.err.println("❌ Usuario desactivado - Bloqueando acceso");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Tu cuenta ha sido desactivada\",\"deactivatedAt\":\"" + user.getDeactivatedAt() + "\"}");
                    return false;
                }
                
                // ✅ IMPORTANTE: Agregar el usuario al request
                request.setAttribute("authenticatedUser", user);
                System.out.println("✅ Usuario agregado al request como 'authenticatedUser'");
            } else {
                System.err.println("❌ Usuario no encontrado en BD: " + username);
            }
        } else {
            System.err.println("❌ Token inválido o ausente");
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

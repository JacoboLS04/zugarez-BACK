package com.zugarez.zugarez_BACK.config;

import com.zugarez.zugarez_BACK.security.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * Web MVC configuration for the application.
 * Registers authentication interceptors and configures CORS mappings.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    /**
     * Registers interceptors for request processing.
     * Applies authentication interceptor to protected routes, excluding public endpoints.
     * @param registry the interceptor registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/users/**", "/api/unsubscribe")
                .excludePathPatterns("/auth/**", "/public/**");
    }

    /**
     * Configures CORS (Cross-Origin Resource Sharing) mappings.
     * Allows cross-origin requests from any origin with common HTTP methods.
     * @param registry the CORS registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*") // ajustar en producción
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
                .allowedHeaders("*");
    }
}

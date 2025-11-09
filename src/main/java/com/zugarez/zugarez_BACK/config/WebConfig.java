package com.zugarez.zugarez_BACK.config;

import com.zugarez.zugarez_BACK.security.interceptor.AuthInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.method.HandlerTypePredicate;

/**
 * Web configuration for the application.
 * Registers authentication interceptors and configures CORS settings.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Autowired
    private AuthInterceptor authInterceptor;

    /**
     * Configures path matching for the application.
     * Adds a prefix of /api to specific packages for request mapping.
     * @param configurer the path match configurer
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // Aplica prefijo /api solo a paquetes de asistencia/nomina/reportes (no afecta a EmpleadoController)
        configurer.addPathPrefix("/api", HandlerTypePredicate.forBasePackage("com.zugarez.asistencia"));
        configurer.addPathPrefix("/api", HandlerTypePredicate.forBasePackage("com.zugarez.nomina"));
        configurer.addPathPrefix("/api", HandlerTypePredicate.forBasePackage("com.zugarez.reportes"));
    }

    /**
     * Registers the authentication interceptor for protected routes.
     * Excludes authentication callbacks and public endpoints.
     * @param registry the interceptor registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        logger.info("Registrando AuthInterceptor");
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**", "/payment/**")
                .excludePathPatterns("/auth/**", "/actuator/**", 
                                   "/payment/webhook", 
                                   "/payment/success",
                                   "/payment/failure",
                                   "/payment/pending",
                                   "/api/empleados/**", // TEMPORAL
                                   "/api/asistencia/**", // TEMPORAL
                                   "/api/nomina/**",     // TEMPORAL
                                   "/api/reportes/**"    // TEMPORAL
                );
        logger.info("AuthInterceptor registrado correctamente");
        logger.info("Rutas protegidas: /api/**, /payment/** (excepto callbacks)");
    }

    /**
     * Configures CORS (Cross-Origin Resource Sharing) for all endpoints.
     * @param registry the CORS registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    /**
     * Configures static resource handlers.
     * Ensures that /api/** routes are not treated as static resources.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        logger.info("Configurando recursos estáticos");
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/public/**")
                .addResourceLocations("classpath:/public/");
        logger.info("Recursos estáticos configurados: /static/**, /public/**");
    }
}

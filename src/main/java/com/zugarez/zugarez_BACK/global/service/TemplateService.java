package com.zugarez.zugarez_BACK.global.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Service for loading HTML templates for email and web responses.
 * Provides methods to retrieve verification success, error, and already-verified pages.
 */
@Service
public class TemplateService {

    /**
     * Loads the verification success page template.
     * @return HTML content of the verification success page
     */
    public String getVerificationSuccessPage() {
        return loadTemplate("templates/verification-success.html");
    }

    /**
     * Loads the verification error page template.
     * @return HTML content of the verification error page
     */
    public String getVerificationErrorPage() {
        return loadTemplate("templates/verification-error.html");
    }

    /**
     * Loads the already-verified page template.
     * @return HTML content of the already-verified page
     */
    public String getVerificationAlreadyPage() {
        return loadTemplate("templates/verification-already.html");
    }

    /**
     * Loads an HTML template from the classpath.
     * @param templatePath Path to the template file
     * @return HTML content as a string, or a basic fallback page if loading fails
     */
    private String loadTemplate(String templatePath) {
        try {
            ClassPathResource resource = new ClassPathResource(templatePath);
            Path path = resource.getFile().toPath();
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            // Si falla cargar el template, devolver una página básica
            return """
                <!DOCTYPE html>
                <html>
                <head><title>Verification</title></head>
                <body>
                    <h1>Verificación procesada</h1>
                    <p><a href="http://localhost:3000">Ir al sitio web</a></p>
                </body>
                </html>
                """;
        }
    }
}

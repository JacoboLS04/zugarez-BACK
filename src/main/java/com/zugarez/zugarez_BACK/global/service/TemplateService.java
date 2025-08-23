package com.zugarez.zugarez_BACK.global.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class TemplateService {

    public String getVerificationSuccessPage() {
        return loadTemplate("templates/verification-success.html");
    }

    public String getVerificationErrorPage() {
        return loadTemplate("templates/verification-error.html");
    }

    public String getVerificationAlreadyPage() {
        return loadTemplate("templates/verification-already.html");
    }

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

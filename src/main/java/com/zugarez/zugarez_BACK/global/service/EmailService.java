package com.zugarez.zugarez_BACK.global.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String token) {
        try {
            System.out.println("[EmailService] Enviando correo de verificación a: " + to);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject("🔐 Verifica tu cuenta en Zugarez");
            
            String verificationUrl = "http://localhost:8080/auth/verify?token=" + token;
            
            String htmlContent = """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Verifica tu cuenta en Zugarez</title>
                </head>
                <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f5f7fa; line-height: 1.6;">
                    <table cellpadding="0" cellspacing="0" border="0" width="100%%" style="background-color: #f5f7fa; min-height: 100vh;">
                        <tr>
                            <td align="center" valign="top" style="padding: 40px 20px;">
                                <table cellpadding="0" cellspacing="0" border="0" width="600" style="max-width: 600px; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08); overflow: hidden;">
                                    <!-- Header -->
                                    <tr>
                                        <td style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 40px 30px; text-align: center;">
                                            <div style="font-size: 32px; margin-bottom: 10px;">🔐</div>
                                            <h1 style="color: #ffffff; margin: 0; font-size: 28px; font-weight: 600;">¡Hola! 👋</h1>
                                            <p style="color: #e8f0fe; margin: 10px 0 0 0; font-size: 18px; font-weight: 300;">¡Bienvenido/a a Zugarez! 🎉</p>
                                        </td>
                                    </tr>
                                    
                                    <!-- Content -->
                                    <tr>
                                        <td style="padding: 40px 30px;">
                                            <p style="color: #374151; margin: 0 0 25px 0; font-size: 16px;">
                                                Gracias por registrarte en nuestro sistema. Para completar tu registro y activar tu cuenta, necesitamos verificar tu dirección de correo electrónico.
                                            </p>
                                            
                                            <div style="background-color: #f8fafc; border: 2px dashed #e5e7eb; border-radius: 8px; padding: 20px; margin: 30px 0; text-align: center;">
                                                <p style="color: #6b7280; margin: 0 0 20px 0; font-size: 14px; font-weight: 500;">👆 Haz clic en el siguiente enlace para verificar tu cuenta:</p>
                                                
                                                <a href="%s" style="display: inline-block; background: linear-gradient(135deg, #10b981 0%%, #059669 100%%); color: #ffffff; text-decoration: none; padding: 14px 32px; border-radius: 8px; font-weight: 600; font-size: 16px; margin: 10px 0; transition: transform 0.2s ease; box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);">
                                                    ✅ Verificar mi cuenta
                                                </a>
                                            </div>
                                            
                                            <!-- Warning Box -->
                                            <div style="background-color: #fef3c7; border-left: 4px solid #f59e0b; border-radius: 6px; padding: 16px; margin: 30px 0;">
                                                <h3 style="color: #92400e; margin: 0 0 12px 0; font-size: 16px; font-weight: 600; display: flex; align-items: center;">
                                                    ⚠️ IMPORTANTE:
                                                </h3>
                                                <ul style="color: #78350f; margin: 0; padding-left: 20px; font-size: 14px;">
                                                    <li style="margin-bottom: 8px;">Este enlace es válido por tiempo limitado</li>
                                                    <li style="margin-bottom: 8px;">No compartas este enlace con nadie</li>
                                                    <li style="margin-bottom: 0;">Si no solicitaste esta cuenta, ignora este mensaje</li>
                                                </ul>
                                            </div>
                                            
                                            <!-- Benefits -->
                                            <div style="background-color: #f0f9ff; border-radius: 8px; padding: 25px; margin: 30px 0;">
                                                <h3 style="color: #0369a1; margin: 0 0 20px 0; font-size: 18px; font-weight: 600;">Una vez verificada tu cuenta, podrás:</h3>
                                                <div style="color: #0c4a6e;">
                                                    <p style="margin: 0 0 12px 0; display: flex; align-items: center; font-size: 15px;">
                                                        <span style="color: #10b981; font-weight: bold; margin-right: 8px; font-size: 18px;">✅</span>
                                                        Iniciar sesión en el sistema
                                                    </p>
                                                    <p style="margin: 0 0 12px 0; display: flex; align-items: center; font-size: 15px;">
                                                        <span style="color: #10b981; font-weight: bold; margin-right: 8px; font-size: 18px;">✅</span>
                                                        Acceder a todas las funcionalidades
                                                    </p>
                                                    <p style="margin: 0; display: flex; align-items: center; font-size: 15px;">
                                                        <span style="color: #10b981; font-weight: bold; margin-right: 8px; font-size: 18px;">✅</span>
                                                        Gestionar tus productos
                                                    </p>
                                                </div>
                                            </div>
                                            
                                            <p style="color: #6b7280; margin: 30px 0 0 0; font-size: 14px; text-align: center;">
                                                Si tienes algún problema, no dudes en contactarnos.
                                            </p>
                                        </td>
                                    </tr>
                                    
                                    <!-- Footer -->
                                    <tr>
                                        <td style="background-color: #f8fafc; padding: 30px; text-align: center; border-top: 1px solid #e5e7eb;">
                                            <p style="color: #4b5563; margin: 0 0 8px 0; font-size: 16px; font-weight: 600;">
                                                ¡Gracias por elegir Zugarez! 🚀
                                            </p>
                                            <p style="color: #6b7280; margin: 0; font-size: 14px;">
                                                ---<br>
                                                El equipo de Zugarez<br>
                                                <span style="font-weight: 600; color: #4b5563;">Sistema de Gestión 2025</span>
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(verificationUrl);
            
            helper.setText(htmlContent, true); // true indica que es contenido HTML
            
            mailSender.send(message);
            System.out.println("[EmailService] Correo HTML enviado correctamente a: " + to);
        } catch (Exception e) {
            System.out.println("[EmailService] Error al enviar correo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

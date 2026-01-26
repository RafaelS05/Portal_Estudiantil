package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.ResetPasswordRequest;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.PasswordResetRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetService {

    private final PasswordResetRepository resetRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public PasswordResetService(PasswordResetRepository resetRepository,
            JavaMailSender mailSender,
            PasswordEncoder passwordEncoder) {
        this.resetRepository = resetRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    public void requestPasswordReset(String email) {
        ResetPasswordRequest request = resetRepository.requestReset(email);

        if (request.getExists() == 0) {
            return;
        }

        sendResetEmail(email, request.getToken());

    }

    public ResetPasswordRequest validateToken(String token) {
        return resetRepository.validateToken(token);
    }

    public boolean resetPassword(String token, String newPassword) {
        String hashedPassword = passwordEncoder.encode(newPassword);
        return resetRepository.resetPassword(token, hashedPassword);
    }

    private void sendResetEmail(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Recuperación de Contraseña - Portal Estudiantil");

            String resetUrl = baseUrl + "/resetContrasenna/reset-password?token=" + token;
            
            String emailBody = String.format(
                    "Hola,\n\n"
                    + "Hemos recibido una solicitud para restablecer tu contraseña en el Portal Estudiantil.\n\n"
                    + "Para restablecer tu contraseña, haz clic en el siguiente enlace:\n"
                    + "%s\n\n"
                    + "Este enlace es válido por 30 minutos.\n\n"
                    + "Si no solicitaste este cambio, puedes ignorar este correo de forma segura.\n\n"
                    + "Saludos,\n"
                    + "Escuela Neftalí Villalobos Gutiérrez",
                    resetUrl
            );

            message.setText(emailBody);
            mailSender.send(message);

        } catch (Exception e) {
            System.err.println("Error enviando email: " + e.getMessage());
            throw new RuntimeException("No se pudo enviar el email de recuperación");
        }
    }

    public boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            return false;
        }

        long lowercase = password.chars().filter(Character::isLowerCase).count();
        if (lowercase < 4) {
            return false;
        }

        long digits = password.chars().filter(Character::isDigit).count();
        if (digits < 3) {
            return false;
        }

        return true;
    }

}

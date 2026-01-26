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

    public boolean requestPasswordReset(String email) {
        try {
            ResetPasswordRequest request = resetRepository.requestReset(email);

            if (request.getExists() == null || request.getExists() == 0) {
                return true;
            }

            // DEV: log temporal
            System.out.println("DEV TOKEN: " + request.getToken());

            sendResetEmail(email, request.getToken());
            return true;

        } catch (Exception e) {
            System.err.println("Error en requestPasswordReset: " + e.getMessage());
            return false;
        }
    }

    public ResetPasswordRequest validateToken(String token) {
        return resetRepository.validateToken(token);
    }

    public boolean resetPassword(String token, String newPassword) {
        String hashedPassword = passwordEncoder.encode(newPassword);
        return resetRepository.resetPassword(token, hashedPassword);
    }

    private void sendResetEmail(String toEmail, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Recuperación de Contraseña - Portal Estudiantil");

        String resetUrl = baseUrl + "/resetContrasenna/reset-password?token=" + token;

        message.setText(
                "Hola,\n\n"
                + "Recibimos una solicitud para restablecer tu contraseña.\n\n"
                + "Enlace:\n" + resetUrl + "\n\n"
                + "Si no solicitaste esto, ignora el correo.\n\n"
                + "Saludos."
        );

        mailSender.send(message);
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

package PortalEstudiantil.ProyectoPortalEstudiantil.Security;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Correo;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Credencial;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.CorreoRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.CredencialRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginFailureHandler.class);

    // HU.13.2: bloqueo temporal tras 3 intentos fallidos
    private static final int MAX_INTENTOS = 3;
    private static final int MINUTOS_BLOQUEO = 15;

    private final CredencialRepository credencialRepository;
    private final CorreoRepository correoRepository;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public LoginFailureHandler(CredencialRepository credencialRepository,
                               CorreoRepository correoRepository,
                               JavaMailSender mailSender) {
        this.credencialRepository = credencialRepository;
        this.correoRepository = correoRepository;
        this.mailSender = mailSender;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        org.springframework.security.core.AuthenticationException exception)
            throws IOException, ServletException {

        // La cuenta ya estaba bloqueada: no incrementar el contador
        if (exception instanceof LockedException) {
            response.sendRedirect("/login?bloqueado");
            return;
        }

        String email = request.getParameter("email");
        boolean bloqueadaAhora = false;

        if (email != null && !email.isBlank()) {
            try {
                bloqueadaAhora = registrarIntentoFallido(email.trim());
            } catch (Exception e) {
                log.error("Error registrando intento fallido para {}: {}", email, e.getMessage());
            }
        }

        response.sendRedirect(bloqueadaAhora ? "/login?bloqueado" : "/login?error");
    }

    /**
     * Incrementa el contador de intentos fallidos y bloquea la cuenta
     * temporalmente al alcanzar el máximo. Retorna true si la cuenta
     * quedó bloqueada en este intento.
     */
    private boolean registrarIntentoFallido(String email) {
        Correo correo = correoRepository.findByCorreo(email);
        if (correo == null || correo.getIdCorreo() == null) {
            return false; // correo no registrado: no hay cuenta que bloquear
        }

        Credencial credencial = credencialRepository.findByIdCorreoFk(correo.getIdCorreo());
        if (credencial == null) {
            return false;
        }

        int intentos = (credencial.getIntentosFallidos() == null ? 0 : credencial.getIntentosFallidos()) + 1;
        credencial.setIntentosFallidos(intentos);
        credencial.setFechaModificacion(LocalDateTime.now());
        credencial.setModificadoPor("SISTEMA");

        boolean bloquear = intentos >= MAX_INTENTOS;
        if (bloquear) {
            credencial.setBloqueadoHasta(LocalDateTime.now().plusMinutes(MINUTOS_BLOQUEO));
            credencial.setIntentosFallidos(0); // reiniciar contador para el siguiente ciclo
            credencial.setAccion("BLOQUEO_POR_INTENTOS_FALLIDOS");
        } else {
            credencial.setAccion("INTENTO_FALLIDO_" + intentos);
        }

        credencialRepository.save(credencial);

        if (bloquear) {
            log.warn("Cuenta bloqueada por {} minutos tras {} intentos fallidos: {}",
                    MINUTOS_BLOQUEO, MAX_INTENTOS, email);
            enviarNotificacionBloqueo(email);
        }

        return bloquear;
    }

    private void enviarNotificacionBloqueo(String email) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Cuenta bloqueada temporalmente - Portal Estudiantil");
            message.setText(
                    "Hola,\n\n"
                    + "Tu cuenta ha sido bloqueada temporalmente por " + MINUTOS_BLOQUEO
                    + " minutos debido a " + MAX_INTENTOS + " intentos fallidos de inicio de sesión.\n\n"
                    + "Si no fuiste tú, te recomendamos restablecer tu contraseña "
                    + "usando la opción '¿Olvidó su contraseña?'.\n\n"
                    + "Saludos,\nPortal Estudiantil"
            );
            mailSender.send(message);
        } catch (Exception e) {
            log.error("No se pudo enviar la notificación de bloqueo a {}: {}", email, e.getMessage());
        }
    }
}

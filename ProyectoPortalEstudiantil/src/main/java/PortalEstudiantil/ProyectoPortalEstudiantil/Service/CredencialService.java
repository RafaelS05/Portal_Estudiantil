package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Correo;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Credencial;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.CorreoRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.CredencialRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class CredencialService {

    private static final Logger log = LoggerFactory.getLogger(CredencialService.class);
    private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%&*";
    private static final int LONGITUD_PASSWORD_TEMPORAL = 12;

    private final CredencialRepository credencialRepository;
    private final CorreoRepository correoRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public CredencialService(
            CredencialRepository credencialRepository,
            CorreoRepository correoRepository,
            JavaMailSender mailSender
    ) {
        this.credencialRepository = credencialRepository;
        this.correoRepository = correoRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    
     //Genera una contraseña temporal aleatoria segura
     
    public String generarPasswordTemporal() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(LONGITUD_PASSWORD_TEMPORAL);

        for (int i = 0; i < LONGITUD_PASSWORD_TEMPORAL; i++) {
            int index = random.nextInt(CARACTERES.length());
            password.append(CARACTERES.charAt(index));
        }

        return password.toString();
    }

    
     //Encripta una contraseña usando BCrypt
    
    public String encriptarPassword(String passwordPlano) {
        return passwordEncoder.encode(passwordPlano);
    }

    /**
     * Crea credencial para un usuario con contraseña temporal
     * Retorna la contraseña en texto plano para enviarla al usuario
     */
    @Transactional
    public String crearCredencialTemporal(Long idUsuario, String correoLogin, String creadoPor) {
        
        // Validar que el correo existe
        Correo correo = correoRepository.findByCorreo(correoLogin);
        if (correo == null) {
            throw new IllegalArgumentException("No se encontró el correo: " + correoLogin);
        }

        // Validar que no exista ya una credencial para este usuario
        if (credencialRepository.existsByIdUsuarioFk(idUsuario)) {
            log.warn("Ya existe una credencial para el usuario ID: {}", idUsuario);
            throw new IllegalArgumentException("El usuario ya tiene credenciales registradas");
        }

        // Validar que no exista ya una credencial para este correo
        if (credencialRepository.existsByIdCorreoFk(correo.getIdCorreo())) {
            log.warn("Ya existe una credencial para el correo: {}", correoLogin);
            throw new IllegalArgumentException("El correo ya tiene credenciales registradas");
        }

        // Generar contraseña temporal
        String passwordTemporal = generarPasswordTemporal();
        String passwordHash = encriptarPassword(passwordTemporal);

        // Insertar credencial
        credencialRepository.insertarCredencial(
                passwordHash,
                idUsuario,
                correo.getIdCorreo(),
                1L, // Estado activo
                creadoPor != null ? creadoPor : "SISTEMA"
        );

        log.info("Credencial temporal creada para usuario ID: {} con correo: {}", idUsuario, correoLogin);

        // Retornar la contraseña en texto plano para mostrarla al usuario
        return passwordTemporal;
    }


     //Verifica si un usuario ya tiene credencial

    public boolean tieneCredencial(Long idUsuario) {
        return credencialRepository.existsByIdUsuarioFk(idUsuario);
    }

    /**
     * TC-11 (HU.1.2): Crea la credencial temporal y envía las credenciales
     * únicas (nombre de usuario = correo de login único + contraseña temporal)
     * al correo del nuevo usuario.
     */
    @Transactional
    public String crearCredencialTemporalYNotificar(Long idUsuario, String correoLogin, String creadoPor) {
        String passwordTemporal = crearCredencialTemporal(idUsuario, correoLogin, creadoPor);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(correoLogin);
            message.setSubject("Bienvenido al Portal Estudiantil - Credenciales de acceso");
            message.setText(
                    "Hola,\n\n"
                    + "Tu cuenta en el Portal Estudiantil fue creada exitosamente.\n\n"
                    + "Credenciales de acceso:\n"
                    + "  Usuario: " + correoLogin + "\n"
                    + "  Contraseña temporal: " + passwordTemporal + "\n\n"
                    + "Por seguridad, cambia tu contraseña después del primer inicio de sesión.\n\n"
                    + "Saludos,\nPortal Estudiantil"
            );
            mailSender.send(message);
            log.info("Credenciales enviadas al correo: {}", correoLogin);
        } catch (Exception e) {
            log.error("No se pudieron enviar las credenciales a {}: {}", correoLogin, e.getMessage());
            // No fallar la creación si el correo no se puede enviar:
            // la contraseña temporal igualmente se muestra al administrador.
        }

        return passwordTemporal;
    }

    /**
     * HU.12.1: Restablecimiento manual de contraseña por un administrador.
     * Genera una contraseña temporal, la envía al correo de login del usuario
     * y registra el evento en los campos de auditoría de la credencial.
     */
    @Transactional
    public void restablecerPasswordManual(Long idUsuario, String modificadoPor) {

        Credencial credencial = credencialRepository.findByIdUsuarioFk(idUsuario);
        if (credencial == null) {
            throw new IllegalArgumentException("El usuario no tiene credenciales registradas");
        }

        Correo correoLogin = correoRepository.findByUsuario_IdUsuarioAndEsLogin(idUsuario, "S");
        if (correoLogin == null || correoLogin.getCorreo() == null) {
            throw new IllegalArgumentException("El usuario no tiene un correo de login registrado");
        }

        // Generar y guardar la contraseña temporal
        String passwordTemporal = generarPasswordTemporal();
        credencial.setPasswordHash(encriptarPassword(passwordTemporal));
        credencial.setIntentosFallidos(0);
        credencial.setBloqueadoHasta(null);
        credencial.setFechaModificacion(LocalDateTime.now());
        credencial.setModificadoPor(modificadoPor != null ? modificadoPor : "ADMIN");
        credencial.setAccion("RESET_MANUAL_ADMIN"); // auditoría del evento
        credencialRepository.save(credencial);

        // Enviar la contraseña temporal al correo del usuario
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(correoLogin.getCorreo());
        message.setSubject("Restablecimiento de Contraseña - Portal Estudiantil");
        message.setText(
                "Hola,\n\n"
                + "Un administrador ha restablecido tu contraseña.\n\n"
                + "Tu contraseña temporal es: " + passwordTemporal + "\n\n"
                + "Por seguridad, te recomendamos cambiarla después de iniciar sesión.\n\n"
                + "Saludos,\nPortal Estudiantil"
        );
        mailSender.send(message);

        log.info("Contraseña restablecida manualmente por {} para usuario ID: {}", modificadoPor, idUsuario);
    }
}
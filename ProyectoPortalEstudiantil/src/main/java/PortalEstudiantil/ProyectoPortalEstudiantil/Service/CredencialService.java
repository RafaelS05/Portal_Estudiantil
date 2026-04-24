package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Correo;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.CorreoRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.CredencialRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
public class CredencialService {

    private static final Logger log = LoggerFactory.getLogger(CredencialService.class);
    private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%&*";
    private static final int LONGITUD_PASSWORD_TEMPORAL = 12;

    private final CredencialRepository credencialRepository;
    private final CorreoRepository correoRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public CredencialService(
            CredencialRepository credencialRepository,
            CorreoRepository correoRepository
    ) {
        this.credencialRepository = credencialRepository;
        this.correoRepository = correoRepository;
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
}
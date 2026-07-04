package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Usuario;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.*;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InformacionPersonalService {

    private final UsuarioRepository usuarioRepo;
    private final TelefonoRepository telefonoRepo;
    private final CorreoRepository correoRepo;
    private final DireccionRepository direccionRepo;
    private final ProvinciaRepository provinciaRepo;
    private final CantonRepository cantonRepo;
    private final DistritoRepository distritoRepo;
    private final EncargadoEstudianteRepository encargadoEstudianteRepo; // ← AGREGADO
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public InformacionPersonalService(
            UsuarioRepository usuarioRepo,
            TelefonoRepository telefonoRepo,
            CorreoRepository correoRepo,
            DireccionRepository direccionRepo,
            ProvinciaRepository provinciaRepo,
            CantonRepository cantonRepo,
            DistritoRepository distritoRepo,
            EncargadoEstudianteRepository encargadoEstudianteRepo, // ← AGREGADO
            JavaMailSender mailSender
    ) {
        this.mailSender = mailSender;
        this.usuarioRepo = usuarioRepo;
        this.telefonoRepo = telefonoRepo;
        this.correoRepo = correoRepo;
        this.direccionRepo = direccionRepo;
        this.provinciaRepo = provinciaRepo;
        this.cantonRepo = cantonRepo;
        this.distritoRepo = distritoRepo;
        this.encargadoEstudianteRepo = encargadoEstudianteRepo; // ← AGREGADO
    }

    // ==========================
    // OBTENER DATOS
    // ==========================
    public Usuario obtenerUsuario(Long idUsuario) {
        return usuarioRepo.findById(idUsuario).orElse(null);
    }

    public Telefono obtenerTelefono(Long idUsuario) {
        return telefonoRepo.findByUsuario_IdUsuario(idUsuario);
    }

    public Correo obtenerCorreoLogin(Long idUsuario) {
        return correoRepo.findByUsuario_IdUsuarioAndEsLogin(idUsuario, "S");
    }

    public Direccion obtenerDireccion(Long idUsuario) {
        return direccionRepo.findByUsuario_IdUsuario(idUsuario);
    }

    // ← AGREGADO: obtener ID de usuario a partir del email del login
    public Long obtenerIdUsuarioPorEmail(String email) {
        Correo correo = correoRepo.findByCorreo(email);
        return correo.getUsuario().getIdUsuario();
    }

    // ← AGREGADO: obtener hijos vinculados si el usuario es Encargado
    public List<EncargadoEstudiante> obtenerHijosVinculados(Long idEncargado) {
        return encargadoEstudianteRepo.listarActivosPorEncargado(idEncargado);
    }

    // ==========================
    // ACTUALIZAR INFORMACIÓN
    // ==========================
    @Transactional
    public void actualizarInformacion(
            Usuario usuarioForm,
            String correoTxt,
            String numeroTxt,
            Long idProvincia,
            Long idCanton,
            Long idDistrito,
            String otrasSenas
    ) {
        // ==========================
        // USUARIO
        // ==========================
        Usuario usuario = usuarioRepo
                .findById(usuarioForm.getIdUsuario())
                .orElseThrow();

        usuario.setNombre(usuarioForm.getNombre());
        usuario.setPrimerApellido(usuarioForm.getPrimerApellido());
        usuario.setSegundoApellido(usuarioForm.getSegundoApellido());

        // ==========================
        // CORREO
        // ==========================
        Correo correo = correoRepo.findByUsuario_IdUsuarioAndEsLogin(usuario.getIdUsuario(), "S");
        if (correo != null) {
            correoRepo.modificarCorreo(
                    correo.getIdCorreo(),
                    correoTxt,
                    "S",
                    usuario.getIdUsuario()
            );
        }

        // ==========================
        // TELÉFONO
        // ==========================
        Telefono telefono = telefonoRepo.findByUsuario_IdUsuario(usuario.getIdUsuario());
        if (telefono == null) {
            telefonoRepo.insertarTelefono(numeroTxt, usuario.getIdUsuario(), 1L);
        } else {
            telefonoRepo.modificarTelefono(telefono.getIdTelefono(), numeroTxt, usuario.getIdUsuario());
        }

        // ==========================
        // DIRECCIÓN
        // ==========================
        // TC-8 (HU.11.1): la ubicación es opcional. Solo se guarda si el usuario
        // proporcionó datos; antes se intentaba insertar con NULLs y la transacción
        // completa fallaba, obligando a ingresar la ubicación en cada edición.
        boolean hayUbicacion = idProvincia != null || idCanton != null || idDistrito != null
                || (otrasSenas != null && !otrasSenas.isBlank());

        if (hayUbicacion) {
            Direccion direccion = direccionRepo.findByUsuario_IdUsuario(usuario.getIdUsuario());

            if (direccion == null) {
                direccionRepo.insertarDireccion(
                        otrasSenas,
                        usuario.getIdUsuario(),
                        idProvincia,
                        idCanton,
                        idDistrito,
                        1L
                );
            } else {
                direccionRepo.modificarDireccion(
                        direccion.getIdDireccion(),
                        otrasSenas,
                        usuario.getIdUsuario(),
                        idProvincia != null ? idProvincia
                                : (direccion.getProvincia() != null ? direccion.getProvincia().getIdProvincia() : null),
                        idCanton != null ? idCanton
                                : (direccion.getCanton() != null ? direccion.getCanton().getIdCanton() : null),
                        idDistrito != null ? idDistrito
                                : (direccion.getDistrito() != null ? direccion.getDistrito().getIdDistrito() : null)
                );
            }
        }

        // Notificación de confirmación al nuevo correo (HU.11.1)
        enviarConfirmacionActualizacion(correoTxt);
    }

    private void enviarConfirmacionActualizacion(String correoDestino) {
        if (correoDestino == null || correoDestino.isBlank()) {
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(correoDestino);
            message.setSubject("Datos actualizados - Portal Estudiantil");
            message.setText(
                    "Hola,\n\n"
                    + "Tus datos personales fueron actualizados correctamente.\n\n"
                    + "Si no realizaste este cambio, contacta al administrador.\n\n"
                    + "Saludos,\nPortal Estudiantil"
            );
            mailSender.send(message);
        } catch (Exception e) {
            // No fallar la actualización si el correo no se puede enviar
            System.err.println("No se pudo enviar la confirmación de actualización: " + e.getMessage());
        }
    }

    // ==========================
    // LISTAR UBICACIONES
    // ==========================
    public List<Provincia> listarProvincias() {
        return provinciaRepo.findAll();
    }

    public List<Canton> listarCantonesPorProvincia(Long idProvincia) {
        return cantonRepo.findByProvincia_IdProvincia(idProvincia);
    }

    public List<Distrito> listarDistritosPorCanton(Long idCanton) {
        return distritoRepo.findByCanton_IdCanton(idCanton);
    }
}

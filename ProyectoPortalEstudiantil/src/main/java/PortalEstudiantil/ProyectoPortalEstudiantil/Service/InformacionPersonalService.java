package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.*;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.*;
import jakarta.transaction.Transactional;
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

    public InformacionPersonalService(
            UsuarioRepository usuarioRepo,
            TelefonoRepository telefonoRepo,
            CorreoRepository correoRepo,
            DireccionRepository direccionRepo,
            ProvinciaRepository provinciaRepo,
            CantonRepository cantonRepo,
            DistritoRepository distritoRepo
    ) {
        this.usuarioRepo = usuarioRepo;
        this.telefonoRepo = telefonoRepo;
        this.correoRepo = correoRepo;
        this.direccionRepo = direccionRepo;
        this.provinciaRepo = provinciaRepo;
        this.cantonRepo = cantonRepo;
        this.distritoRepo = distritoRepo;
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
                    correo.getIdCorreo(), // id_correo
                    correoTxt, // correo nuevo
                    "S", // es_login
                    usuario.getIdUsuario() // id_usuario_fk
            );
        }

        // ==========================
        // TELÉFONO
        // ==========================
        Telefono telefono = telefonoRepo.findByUsuario_IdUsuario(usuario.getIdUsuario());
        if (telefono == null) {
            // Insertar nuevo teléfono usando SP
            telefonoRepo.insertarTelefono(numeroTxt, usuario.getIdUsuario(), 1L); // estado activo
        } else {
            // Modificar teléfono usando SP
            telefonoRepo.modificarTelefono(telefono.getIdTelefono(), numeroTxt, usuario.getIdUsuario());
        }

        // ==========================
        // DIRECCIÓN
        // ==========================
        Direccion direccion = direccionRepo.findByUsuario_IdUsuario(usuario.getIdUsuario());

        if (direccion == null) {
            // Insertar nueva dirección usando SP
            direccionRepo.insertarDireccion(
                    otrasSenas,
                    usuario.getIdUsuario(),
                    idProvincia,
                    idCanton,
                    idDistrito,
                    1L // activo
            );
        } else {
            // Modificar existente usando SP
            direccionRepo.modificarDireccion(
                    direccion.getIdDireccion(),
                    otrasSenas,
                    usuario.getIdUsuario(),
                    idProvincia,
                    idCanton,
                    idDistrito
            );
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

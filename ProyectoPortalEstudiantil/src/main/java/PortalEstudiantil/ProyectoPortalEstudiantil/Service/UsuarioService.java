package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Usuario;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Telefono;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Correo;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.UsuarioRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.TelefonoRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.CorreoRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.EncargadoEstudianteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private static final Long TIPO_ADMIN = 1L;
    private static final Long TIPO_ESTUDIANTE = 3L;
    private static final Long TIPO_ENCARGADO = 4L;

    private static final Long ESTADO_ACTIVO = 1L;
    private static final Long ESTADO_INACTIVO = 2L;

    private final UsuarioRepository usuarioRepository;
    private final TelefonoRepository telefonoRepository;
    private final CorreoRepository correoRepository;
    private final EncargadoEstudianteRepository encargadoEstudianteRepository;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            TelefonoRepository telefonoRepository,
            CorreoRepository correoRepository,
            EncargadoEstudianteRepository encargadoEstudianteRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.telefonoRepository = telefonoRepository;
        this.correoRepository = correoRepository;
        this.encargadoEstudianteRepository = encargadoEstudianteRepository;
    }

    // ==================== CONTACTO ====================

    public Telefono obtenerTelefono(Long idUsuario) {
        return telefonoRepository.findByUsuario_IdUsuario(idUsuario);
    }

    public Correo obtenerCorreoLogin(Long idUsuario) {
        return correoRepository.findByUsuario_IdUsuarioAndEsLogin(idUsuario, "S");
    }

    /**
     * Verifica si un correo ya existe en otro usuario (para evitar duplicados)
     */
    private boolean correoExisteEnOtroUsuario(String correo, Long idUsuarioActual) {
        try {
            Correo correoExistente = correoRepository.findByCorreo(correo);
            if (correoExistente == null) {
                return false; // No existe, OK
            }
            // Existe: verificar si es del mismo usuario o de otro
            return !correoExistente.getUsuario().getIdUsuario().equals(idUsuarioActual);
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public void actualizarContacto(Long idUsuario, String telefonoNumero, String correoLogin) {

        // VALIDAR TELÉFONO
        if (telefonoNumero != null && !telefonoNumero.trim().isEmpty()) {
            Telefono tel = obtenerTelefono(idUsuario);
            if (tel != null && tel.getIdTelefono() != null) {
                telefonoRepository.modificarTelefono(tel.getIdTelefono(), telefonoNumero.trim(), idUsuario);
            } else {
                telefonoRepository.insertarTelefono(telefonoNumero.trim(), idUsuario, 1L);
            }
        }

        // VALIDAR CORREO
        if (correoLogin != null && !correoLogin.trim().isEmpty()) {
            
            // ✅ VALIDACIÓN: Verificar si el correo ya existe en otro usuario
            if (correoExisteEnOtroUsuario(correoLogin.trim(), idUsuario)) {
                throw new IllegalArgumentException(
                    "El correo '" + correoLogin.trim() + "' ya está registrado en otro usuario. " +
                    "Por favor, usa un correo diferente."
                );
            }

            Correo c = obtenerCorreoLogin(idUsuario);
            if (c != null && c.getIdCorreo() != null) {
                // Actualizar correo existente
                try {
                    correoRepository.modificarCorreo(c.getIdCorreo(), correoLogin.trim(), "S", idUsuario);
                } catch (Exception e) {
                    // Capturar error de constraint
                    if (e.getMessage().contains("Duplicate entry") || 
                        e.getMessage().contains("CORREO_TB_CORREO_UQ")) {
                        throw new IllegalArgumentException(
                            "El correo '" + correoLogin.trim() + "' ya está en uso. " +
                            "Por favor, usa un correo diferente."
                        );
                    }
                    throw e;
                }
            } else {
                // Insertar nuevo correo
                try {
                    correoRepository.insertarCorreo(correoLogin.trim(), "S", idUsuario, 1L);
                } catch (Exception e) {
                    // Capturar error de constraint
                    if (e.getMessage().contains("Duplicate entry") || 
                        e.getMessage().contains("CORREO_TB_CORREO_UQ")) {
                        throw new IllegalArgumentException(
                            "El correo '" + correoLogin.trim() + "' ya está en uso. " +
                            "Por favor, usa un correo diferente."
                        );
                    }
                    throw e;
                }
            }
        }
    }

    // ==================== CRUD ====================

    @Transactional
    public Long crearUsuario(String nombre, String primerApellido, String segundoApellido,
                             Long idTipoUsuario, Long idEstado) {

        if (nombre == null || nombre.trim().isEmpty()) throw new IllegalArgumentException("El nombre es obligatorio");
        if (primerApellido == null || primerApellido.trim().isEmpty()) throw new IllegalArgumentException("El primer apellido es obligatorio");
        if (idTipoUsuario == null) throw new IllegalArgumentException("El tipo de usuario es obligatorio");
        if (idEstado == null) throw new IllegalArgumentException("El estado es obligatorio");

        Long nuevoId = usuarioRepository.insertarUsuarioRetornaId(
                nombre.trim(),
                primerApellido.trim(),
                segundoApellido != null ? segundoApellido.trim() : "",
                idTipoUsuario,
                idEstado
        );

        if (nuevoId == null || nuevoId == 0) {
            throw new RuntimeException("El stored procedure no retornó un ID válido");
        }

        log.info("Usuario creado exitosamente con ID: {}", nuevoId);
        return nuevoId;
    }

    @Transactional
    public Long crearUsuario(Usuario usuario) {
        return crearUsuario(
                usuario.getNombre(),
                usuario.getPrimerApellido(),
                usuario.getSegundoApellido(),
                usuario.getIdTipoUsuarioFk(),
                usuario.getIdEstadoFk()
        );
    }

    /**
     * Verifica si un correo ya está registrado en el sistema.
     */
    public boolean correoYaRegistrado(String correo) {
        if (correo == null || correo.isBlank()) {
            return false;
        }
        return correoRepository.findByCorreo(correo.trim()) != null;
    }

    /**
     * TC-10 (HU.1.1): Crea el usuario junto con su contacto en UNA sola
     * transacción, validando el correo duplicado ANTES de insertar.
     * Antes, el usuario se creaba primero y al fallar el correo duplicado
     * quedaba un usuario huérfano ("en blanco") en la base de datos.
     */
    @Transactional
    public Long crearUsuarioConContacto(Usuario usuario, String telefonoNumero, String correoLogin) {

        if (correoLogin == null || correoLogin.trim().isEmpty()) {
            throw new IllegalArgumentException("El correo de login es obligatorio");
        }

        if (correoYaRegistrado(correoLogin)) {
            throw new IllegalArgumentException(
                    "El correo '" + correoLogin.trim() + "' ya está registrado en otro usuario. "
                    + "Por favor, usa un correo diferente. No se creó el usuario.");
        }

        Long idNuevo = crearUsuario(usuario);
        actualizarContacto(idNuevo, telefonoNumero, correoLogin);
        return idNuevo;
    }

    @Transactional
    public void actualizarUsuario(Long idUsuario, String nombre, String primerApellido,
                                  String segundoApellido, Long idTipoUsuario) {

        if (!usuarioRepository.existsById(idUsuario)) throw new RuntimeException("No existe un usuario con ID: " + idUsuario);
        if (nombre == null || nombre.trim().isEmpty()) throw new IllegalArgumentException("El nombre es obligatorio");
        if (primerApellido == null || primerApellido.trim().isEmpty()) throw new IllegalArgumentException("El primer apellido es obligatorio");

        usuarioRepository.modificarUsuario(
                idUsuario,
                nombre.trim(),
                primerApellido.trim(),
                segundoApellido != null ? segundoApellido.trim() : "",
                idTipoUsuario
        );

        log.info("Usuario ID {} actualizado exitosamente", idUsuario);
    }

    @Transactional
    public void actualizarUsuario(Usuario usuario) {
        if (usuario.getIdUsuario() == null) throw new IllegalArgumentException("El ID del usuario es obligatorio para actualizar");
        actualizarUsuario(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getPrimerApellido(),
                usuario.getSegundoApellido(),
                usuario.getIdTipoUsuarioFk()
        );
    }

    // ==================== ESTADO (CON RESTRICCIONES) ====================

    @Transactional
    public void cambiarEstadoUsuario(Long idUsuario, Long idEstado) {

        if (!usuarioRepository.existsById(idUsuario)) {
            throw new RuntimeException("No existe un usuario con ID: " + idUsuario);
        }
        if (idEstado == null) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }

        // Solo validamos restricciones si se quiere poner INACTIVO
        if (ESTADO_INACTIVO.equals(idEstado)) {

            Usuario usuario = obtenerUsuario(idUsuario);
            Long tipo = usuario.getIdTipoUsuarioFk();

            // Regla 1: No desactivar el único admin activo
            if (TIPO_ADMIN.equals(tipo)) {
                long adminsActivos = usuarioRepository.countByIdTipoUsuarioFkAndIdEstadoFk(TIPO_ADMIN, ESTADO_ACTIVO);
                if (adminsActivos <= 1) {
                    throw new IllegalArgumentException(
                        "No se puede desactivar el único administrador activo del sistema. " +
                        "Debe existir al menos un administrador activo."
                    );
                }
            }

            // Regla 2: Estudiante con relación activa => NO desactivar
            if (TIPO_ESTUDIANTE.equals(tipo)) {
                long rel = encargadoEstudianteRepository.countRelacionesActivasPorEstudiante(idUsuario);
                if (rel > 0) {
                    throw new IllegalArgumentException(
                        "No se puede desactivar este estudiante porque tiene " + rel + 
                        " encargado(s) vinculado(s). " +
                        "Primero desvincule los encargados."
                    );
                }
            }

            // Regla 3: Encargado con relación activa => NO desactivar
            if (TIPO_ENCARGADO.equals(tipo)) {
                long rel = encargadoEstudianteRepository.countRelacionesActivasPorEncargado(idUsuario);
                if (rel > 0) {
                    throw new IllegalArgumentException(
                        "No se puede desactivar este encargado porque está vinculado a " + rel + 
                        " estudiante(s). " +
                        "Primero desvincule los estudiantes."
                    );
                }
            }
        }

        usuarioRepository.cambiarEstadoUsuario(idUsuario, idEstado);

        log.info("Usuario ID {} estado cambiado a {}", idUsuario,
                ESTADO_ACTIVO.equals(idEstado) ? "ACTIVO" : "INACTIVO");
    }

    @Transactional
    public void activarUsuario(Long idUsuario) {
        cambiarEstadoUsuario(idUsuario, ESTADO_ACTIVO);
    }

    @Transactional
    public void desactivarUsuario(Long idUsuario) {
        cambiarEstadoUsuario(idUsuario, ESTADO_INACTIVO);
    }

    @Transactional
    public void eliminarUsuario(Long idUsuario) {
        // eliminación lógica = desactivar (aplica mismas reglas)
        desactivarUsuario(idUsuario);
        log.info("Usuario ID {} desactivado (eliminación lógica)", idUsuario);
    }

    // ==================== CONSULTAS ====================

    public Usuario obtenerUsuario(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));
    }

    public List<Usuario> obtenerTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) throw new IllegalArgumentException("El término de búsqueda es obligatorio");
        return usuarioRepository.findByNombreContaining(nombre.trim());
    }

    public List<Usuario> buscarPorTipoUsuario(Long idTipoUsuario) {
        if (idTipoUsuario == null) throw new IllegalArgumentException("El ID del tipo de usuario es obligatorio");
        return usuarioRepository.findByIdTipoUsuarioFk(idTipoUsuario);
        
    }
    
    public List<Usuario> buscarPorTipoYEstado(Long idTipoUsuario, Long idEstado) {
    if (idTipoUsuario == null) throw new IllegalArgumentException("El tipo de usuario es obligatorio");
    if (idEstado == null) throw new IllegalArgumentException("El estado es obligatorio");
    return usuarioRepository.findByIdTipoUsuarioFkAndIdEstadoFk(idTipoUsuario, idEstado);
}

    public List<Usuario> buscarPorEstado(Long idEstado) {
        if (idEstado == null) throw new IllegalArgumentException("El ID del estado es obligatorio");
        return usuarioRepository.findByIdEstadoFk(idEstado);
    }

    public List<Usuario> buscarPorNombreCompleto(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) throw new IllegalArgumentException("El nombre completo es obligatorio");
        return usuarioRepository.findByNombreCompletoContaining(nombreCompleto.trim());
    }

    public long contarTotalUsuarios() {
        return usuarioRepository.count();
    }

    public long contarUsuariosPorEstado(Long idEstado) {
        return usuarioRepository.countByIdEstadoFk(idEstado);
    }

    public long contarUsuariosPorTipo(Long idTipoUsuario) {
        return usuarioRepository.countByIdTipoUsuarioFk(idTipoUsuario);
    }
}
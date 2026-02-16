package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Usuario;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.*;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class EstudianteService {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final TipoUsuarioRepository tipoUsuarioRepository;
    private final EncargadoEstudianteRepository encargadoEstudianteRepository;
   
    private final TelefonoRepository telefonoRepository;
    private final CorreoRepository correoRepository;

    public EstudianteService(
            UsuarioService usuarioService,
            UsuarioRepository usuarioRepository,
            TipoUsuarioRepository tipoUsuarioRepository,
            EncargadoEstudianteRepository encargadoEstudianteRepository,

            TelefonoRepository telefonoRepository,
            CorreoRepository correoRepository
    ) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.tipoUsuarioRepository = tipoUsuarioRepository;
        this.encargadoEstudianteRepository = encargadoEstudianteRepository;
      
        this.telefonoRepository = telefonoRepository;
        this.correoRepository = correoRepository;
    }

    public Long obtenerTipoId(String nombreTipo) {
        TipoUsuario tipo = tipoUsuarioRepository.findByNombreIgnoreCase(nombreTipo)
                .orElseThrow(() -> new RuntimeException("No existe el tipo de usuario: " + nombreTipo));
        return tipo.getIdTipoUsuario();
    }

    // Listado normal (solo usuarios)
    public List<Usuario> listarEstudiantes() {
        Long idTipoEstudiante = obtenerTipoId("Estudiante");
        return usuarioRepository.findByIdTipoUsuarioFk(idTipoEstudiante);
    }

    // Listado completo para la vista (tel/correo/encargado)
    public List<UsuarioRepository.EstudianteListadoRow> listarEstudiantesVista() {
        Long idTipoEstudiante = obtenerTipoId("Estudiante");
        return usuarioRepository.listarEstudiantesConContactoYEncargado(idTipoEstudiante);
    }

    public List<UsuarioRepository.EstudianteListadoRow> buscarEstudiantesVista(String busqueda) {
        Long idTipoEstudiante = obtenerTipoId("Estudiante");
        if (busqueda == null || busqueda.trim().isEmpty()) {
            return usuarioRepository.listarEstudiantesConContactoYEncargado(idTipoEstudiante);
        }
        return usuarioRepository.buscarEstudiantesConContacto(idTipoEstudiante, busqueda.trim());
    }

    public List<Usuario> listarEncargados() {
        Long idTipoEncargado = obtenerTipoId("Encargado");
        return usuarioRepository.findByIdTipoUsuarioFk(idTipoEncargado);
    }

    public List<Usuario> buscarEncargadosPorCorreo(String correo) {
        Long idTipoEncargado = obtenerTipoId("Encargado");
        return usuarioRepository.buscarEncargadosPorCorreo(correo, idTipoEncargado);
    }

    @Transactional
    public Long crearEstudiante(Usuario estudiante) {
        estudiante.setIdTipoUsuarioFk(obtenerTipoId("Estudiante"));
        if (estudiante.getIdEstadoFk() == null) estudiante.setIdEstadoFk(1L);
        return usuarioService.crearUsuario(estudiante);
    }

    @Transactional
    public void actualizarEstudiante(Long idEstudiante, Usuario estudiante) {
        Long idTipoEstudiante = obtenerTipoId("Estudiante");
        usuarioService.actualizarUsuario(
                idEstudiante,
                estudiante.getNombre(),
                estudiante.getPrimerApellido(),
                estudiante.getSegundoApellido(),
                idTipoEstudiante
        );
    }

    public Usuario obtenerEstudiante(Long id) {
        Usuario u = usuarioService.obtenerUsuario(id);
        Long idTipoEstudiante = obtenerTipoId("Estudiante");
        if (!Objects.equals(u.getIdTipoUsuarioFk(), idTipoEstudiante)) {
            throw new RuntimeException("El usuario no es estudiante.");
        }
        return u;
    }

    // CONTACTO
    public Telefono obtenerTelefono(Long idUsuario) {
        return telefonoRepository.findByUsuario_IdUsuario(idUsuario);
    }

    public Correo obtenerCorreoLogin(Long idUsuario) {
        return correoRepository.findByUsuario_IdUsuarioAndEsLogin(idUsuario, "S");
    }

    @Transactional
    public void actualizarContacto(Long idEstudiante, String telefonoNumero, String correoLogin) {

        if (telefonoNumero != null && !telefonoNumero.trim().isEmpty()) {
            Telefono tel = obtenerTelefono(idEstudiante);
            if (tel != null && tel.getIdTelefono() != null) {
                telefonoRepository.modificarTelefono(tel.getIdTelefono(), telefonoNumero.trim(), idEstudiante);
            } else {
                telefonoRepository.insertarTelefono(telefonoNumero.trim(), idEstudiante, 1L);
            }
        }

        if (correoLogin != null && !correoLogin.trim().isEmpty()) {
            Correo c = obtenerCorreoLogin(idEstudiante);
            if (c != null && c.getIdCorreo() != null) {
                correoRepository.modificarCorreo(c.getIdCorreo(), correoLogin.trim(), "S", idEstudiante);
            } else {
                correoRepository.insertarCorreo(correoLogin.trim(), "S", idEstudiante, 1L);
            }
        }
    }

  

    public List<EncargadoEstudiante> listarEncargadosDeEstudiante(Long idEstudiante) {
        return encargadoEstudianteRepository.listarActivosPorEstudiante(idEstudiante);
    }

    public List<Map<String, Object>> listarEncargadosVista(Long idEstudiante) {
        List<EncargadoEstudiante> relaciones = listarEncargadosDeEstudiante(idEstudiante);
        List<Map<String, Object>> out = new ArrayList<>();

        for (EncargadoEstudiante r : relaciones) {
            Long idEnc = r.getIdUsuarioEncargadoFk();
            Usuario encargado = usuarioService.obtenerUsuario(idEnc);

            Map<String, Object> row = new HashMap<>();
            row.put("idRelacion", r.getIdEncargadoEstudiante());
            row.put("parentesco", r.getParentesco());
            row.put("idEncargado", idEnc);

            String nombreEnc = encargado.getNombre() + " " + encargado.getPrimerApellido()
                    + (encargado.getSegundoApellido() != null ? " " + encargado.getSegundoApellido() : "");
            row.put("nombreEncargado", nombreEnc);

            out.add(row);
        }
        return out;
    }

    // ✅ NUEVO: lista de estudiantes para la vista del ENCARGADO
    public List<Map<String, Object>> listarEstudiantesVistaPorEncargado(Long idEncargado) {
        List<EncargadoEstudiante> relaciones = encargadoEstudianteRepository.listarActivosPorEncargado(idEncargado);
        List<Map<String, Object>> out = new ArrayList<>();

        for (EncargadoEstudiante r : relaciones) {
            Long idEst = r.getIdUsuarioEstudianteFk();
            Usuario estudiante = usuarioService.obtenerUsuario(idEst);

            Map<String, Object> row = new HashMap<>();
            row.put("idRelacion", r.getIdEncargadoEstudiante());
            row.put("parentesco", r.getParentesco());

            row.put("idEstudiante", idEst);

            String nombreEst = estudiante.getNombre() + " " + estudiante.getPrimerApellido()
                    + (estudiante.getSegundoApellido() != null ? " " + estudiante.getSegundoApellido() : "");
            row.put("nombreEstudiante", nombreEst);

            out.add(row);
        }
        return out;
    }

    // VÍNCULO ENCARGADO
    @Transactional
    public void vincularEncargado(Long idEstudiante, Long idEncargado, String parentesco) {
        if (idEstudiante == null || idEncargado == null) {
            throw new IllegalArgumentException("Debe seleccionar estudiante y encargado.");
        }
        if (parentesco == null || parentesco.trim().isEmpty()) {
            throw new IllegalArgumentException("El parentesco es obligatorio.");
        }

        long rel = encargadoEstudianteRepository.countRelacionActivaEntre(idEstudiante, idEncargado);
        if (rel > 0) {
            throw new IllegalArgumentException("Este encargado ya está vinculado a este estudiante.");
        }

        encargadoEstudianteRepository.insertarRelacion(parentesco.trim(), idEstudiante, idEncargado, 1L);
    }

    @Transactional
    public void desactivarRelacionEncargado(Long idRelacion) {
        encargadoEstudianteRepository.cambiarEstadoRelacion(idRelacion, 2L);
    }
}

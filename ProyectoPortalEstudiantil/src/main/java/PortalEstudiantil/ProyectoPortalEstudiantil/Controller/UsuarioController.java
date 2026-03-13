package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.TipoUsuario;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Usuario;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.TipoUsuarioRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.CredencialService;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.EstudianteService;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private static final Long TIPO_ESTUDIANTE = 3L;
    private static final Long TIPO_ENCARGADO = 4L;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private TipoUsuarioRepository tipoUsuarioRepository;

    @Autowired
    private CredencialService credencialService;

    private Long obtenerTipoId(String nombreTipo) {
        return tipoUsuarioRepository.findByNombreIgnoreCase(nombreTipo)
                .map(TipoUsuario::getIdTipoUsuario)
                .orElse(null);
    }

    // ==================== LISTADO ====================
    @GetMapping({"", "/"})
    public String listarUsuarios(
            @RequestParam(value = "busqueda", required = false) String busqueda,
            @RequestParam(value = "tipoUsuario", required = false) String tipoUsuario,
            @RequestParam(value = "estado", required = false) Long estado,
            Model model) {

        List<Usuario> usuarios;
        Long idTipoUsuario = null;

        if (tipoUsuario != null && !tipoUsuario.trim().isEmpty()) {
            idTipoUsuario = obtenerTipoId(tipoUsuario);
        }

        if (idTipoUsuario != null && estado != null) {
            usuarios = usuarioService.buscarPorTipoUsuario(idTipoUsuario).stream()
                    .filter(u -> u.getIdEstadoFk() != null && u.getIdEstadoFk().equals(estado))
                    .collect(Collectors.toList());
        } else if (idTipoUsuario != null) {
            usuarios = usuarioService.buscarPorTipoUsuario(idTipoUsuario);
        } else if (estado != null) {
            usuarios = usuarioService.buscarPorEstado(estado);
        } else {
            usuarios = usuarioService.obtenerTodosUsuarios();
        }

        if (busqueda != null && !busqueda.trim().isEmpty()) {
            String b = busqueda.trim().toLowerCase();
            usuarios = usuarios.stream()
                    .filter(u -> {
                        String nombreCompleto = (n(u.getNombre()) + " " + n(u.getPrimerApellido()) + " " + n(u.getSegundoApellido()))
                                .trim().toLowerCase();

                        boolean matchNombre = nombreCompleto.contains(b);

                        boolean matchCorreo = false;
                        if (u.getCorreos() != null && !u.getCorreos().isEmpty()) {
                            matchCorreo = u.getCorreos().stream()
                                    .anyMatch(c -> c.getCorreo() != null && c.getCorreo().toLowerCase().contains(b));
                        }

                        return matchNombre || matchCorreo;
                    })
                    .collect(Collectors.toList());
        }

        List<TipoUsuario> tiposUsuario = tipoUsuarioRepository.findAll();

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("total", usuarios.size());
        model.addAttribute("totalUsuarios", usuarioService.contarTotalUsuarios());
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("tipoUsuario", tipoUsuario);
        model.addAttribute("tiposUsuario", tiposUsuario);
        model.addAttribute("estado", estado);

        return "usuarios/listado";
    }

    // ==================== FORMULARIOS ====================
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("tiposUsuario", tipoUsuarioRepository.findAll());
        model.addAttribute("titulo", "Nuevo Usuario");
        model.addAttribute("accion", "/usuarios/guardar");
        model.addAttribute("telefonoNumero", "");
        model.addAttribute("correoLogin", "");
        model.addAttribute("esEstudiante", false);
        model.addAttribute("encargadosVista", List.of());
        return "usuarios/formulario";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(id);

            model.addAttribute("usuario", usuario);
            model.addAttribute("tiposUsuario", tipoUsuarioRepository.findAll());

            model.addAttribute("telefonoNumero",
                    usuarioService.obtenerTelefono(id) != null
                    ? usuarioService.obtenerTelefono(id).getNumero()
                    : "");

            model.addAttribute("correoLogin",
                    usuarioService.obtenerCorreoLogin(id) != null
                    ? usuarioService.obtenerCorreoLogin(id).getCorreo()
                    : "");

            model.addAttribute("titulo", "Editar Usuario");
            model.addAttribute("accion", "/usuarios/actualizar/" + id);

            boolean esEstudiante = usuario.getIdTipoUsuarioFk() != null
                    && usuario.getIdTipoUsuarioFk().equals(TIPO_ESTUDIANTE);

            model.addAttribute("esEstudiante", esEstudiante);
            model.addAttribute("encargadosVista",
                    esEstudiante ? estudianteService.listarEncargadosVista(id) : List.of());

            return "usuarios/formulario";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Usuario no encontrado");
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/usuarios";
        }
    }

    // ==================== VER DETALLE ====================
    @GetMapping("/ver/{id}")
    public String verUsuario(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(id);

            boolean esEstudiante = usuario.getIdTipoUsuarioFk() != null
                    && usuario.getIdTipoUsuarioFk().equals(TIPO_ESTUDIANTE);

            boolean esEncargado = usuario.getIdTipoUsuarioFk() != null
                    && usuario.getIdTipoUsuarioFk().equals(TIPO_ENCARGADO);

            model.addAttribute("usuario", usuario);
            model.addAttribute("telefono", usuarioService.obtenerTelefono(id));
            model.addAttribute("correo", usuarioService.obtenerCorreoLogin(id));
            model.addAttribute("esEstudiante", esEstudiante);
            model.addAttribute("esEncargado", esEncargado);

            if (esEstudiante) {
                model.addAttribute("encargadosVista", estudianteService.listarEncargadosVista(id));
            } else {
                model.addAttribute("encargadosVista", List.of());
            }

            if (esEncargado) {
                model.addAttribute("estudiantesVista", estudianteService.listarEstudiantesVistaPorEncargado(id));
            } else {
                model.addAttribute("estudiantesVista", List.of());
            }

            return "usuarios/detalle";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Usuario no encontrado");
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/usuarios";
        }
    }

    // ==================== GUARDAR / ACTUALIZAR ====================
    @PostMapping("/guardar")
    public String guardarUsuario(
            @ModelAttribute Usuario usuario,
            @RequestParam(value = "telefonoNumero", required = false) String telefonoNumero,
            @RequestParam(value = "correoLogin", required = false) String correoLogin,
            RedirectAttributes redirectAttributes) {

        try {
            // 1. Crear el usuario
            Long idNuevo = usuarioService.crearUsuario(usuario);
            
            // 2. Actualizar contacto (teléfono y correo)
            usuarioService.actualizarContacto(idNuevo, telefonoNumero, correoLogin);

            // 3. ✅ NUEVO: Generar credencial automáticamente
            String passwordTemporal = null;
            try {
                passwordTemporal = credencialService.crearCredencialTemporal(
                        idNuevo, 
                        correoLogin, 
                        "SISTEMA"
                );
            } catch (Exception e) {
                // Si falla la creación de credencial, registrar pero no fallar todo
                redirectAttributes.addFlashAttribute("advertencia", 
                    "Usuario creado pero hubo un error al generar la credencial: " + e.getMessage());
            }

            // 4. Mensaje de éxito con contraseña temporal
            if (passwordTemporal != null) {
                redirectAttributes.addFlashAttribute("mensaje", 
                    "Usuario creado exitosamente");
                redirectAttributes.addFlashAttribute("passwordTemporal", passwordTemporal);
                redirectAttributes.addFlashAttribute("correoUsuario", correoLogin);
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("mensaje", 
                    "Usuario creado exitosamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            }
            
            return "redirect:/usuarios";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", 
                "Error al crear usuario: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/usuarios/nuevo";
        }
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarUsuario(
            @PathVariable Long id,
            @ModelAttribute Usuario usuario,
            @RequestParam(value = "telefonoNumero", required = false) String telefonoNumero,
            @RequestParam(value = "correoLogin", required = false) String correoLogin,
            RedirectAttributes redirectAttributes) {

        try {
            usuario.setIdUsuario(id);
            usuarioService.actualizarUsuario(usuario);
            usuarioService.actualizarContacto(id, telefonoNumero, correoLogin);

            redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/usuarios/ver/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar usuario: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/usuarios/editar/" + id;
        }
    }

    // ==================== ENCARGADOS ====================
    @GetMapping("/{id}/encargados/nuevo")
    public String nuevoEncargadoForm(
            @PathVariable Long id,
            @RequestParam(value = "correo", required = false) String correo,
            Model model,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioService.obtenerUsuario(id);

        boolean esEstudiante = usuario.getIdTipoUsuarioFk() != null
                && usuario.getIdTipoUsuarioFk().equals(TIPO_ESTUDIANTE);

        if (!esEstudiante) {
            redirectAttributes.addFlashAttribute("mensaje", "Solo los usuarios tipo Estudiante pueden vincular encargados");
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/usuarios/editar/" + id;
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("correo", correo);
        model.addAttribute("encargados",
                (correo != null && !correo.isBlank())
                ? estudianteService.buscarEncargadosPorCorreo(correo)
                : estudianteService.listarEncargados());
        model.addAttribute("accion", "/usuarios/" + id + "/encargados/nuevo");

        return "usuarios/nuevo-encargado";
    }

    @PostMapping("/{id}/encargados/nuevo")
    public String vincularEncargado(
            @PathVariable Long id,
            @RequestParam Long idEncargado,
            @RequestParam String parentesco,
            RedirectAttributes redirectAttributes) {

        try {
            Usuario usuario = usuarioService.obtenerUsuario(id);
            boolean esEstudiante = usuario.getIdTipoUsuarioFk() != null
                    && usuario.getIdTipoUsuarioFk().equals(TIPO_ESTUDIANTE);

            if (!esEstudiante) {
                redirectAttributes.addFlashAttribute("mensaje", "Solo los usuarios tipo Estudiante pueden vincular encargados");
                redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
                return "redirect:/usuarios/editar/" + id;
            }

            estudianteService.vincularEncargado(id, idEncargado, parentesco);
            redirectAttributes.addFlashAttribute("mensaje", "Encargado vinculado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }

        return "redirect:/usuarios/editar/" + id;
    }

    @PostMapping("/encargados/desactivar/{idRelacion}")
    public String desactivarEncargado(
            @PathVariable Long idRelacion,
            @RequestParam Long idUsuario,
            RedirectAttributes redirectAttributes) {

        try {
            estudianteService.desactivarRelacionEncargado(idRelacion);
            redirectAttributes.addFlashAttribute("mensaje", "Encargado desvinculado");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }

        return "redirect:/usuarios/editar/" + idUsuario;
    }

    // ==================== ACTIVAR / DESACTIVAR / ELIMINAR ====================
    @PostMapping("/activar/{id}")
    public String activarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.activarUsuario(id);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario activado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al activar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }
        return "redirect:/usuarios/ver/" + id;
    }

    @PostMapping("/desactivar/{id}")
    public String desactivarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.desactivarUsuario(id);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario desactivado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al desactivar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }
        return "redirect:/usuarios/ver/" + id;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.eliminarUsuario(id);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }
        return "redirect:/usuarios";
    }

    private String n(String s) {
        return s == null ? "" : s;
    }
}
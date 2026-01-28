package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Usuario;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/")
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.obtenerTodosUsuarios();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", usuarioService.contarTotalUsuarios());
        model.addAttribute("pageTitle", "Gestión de Usuarios"); // IMPORTANTE
        return "usuarios/listado";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("titulo", "Nuevo Usuario");
        model.addAttribute("accion", "/usuarios/guardar");
        model.addAttribute("pageTitle", "Nuevo Usuario");
        return "usuarios/formulario";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(id);
            model.addAttribute("usuario", usuario);
            model.addAttribute("titulo", "Editar Usuario");
            model.addAttribute("accion", "/usuarios/actualizar/" + id);
            return "usuarios/formulario";
        } catch (Exception e) {
            return "redirect:/usuarios/?error=Usuario no encontrado";
        }
    }

    @GetMapping("/ver/{id}")
    public String verUsuario(@PathVariable Long id, Model model) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(id);
            model.addAttribute("usuario", usuario);
            return "usuarios/detalle";
        } catch (Exception e) {
            return "redirect:/usuarios/?error=Usuario no encontrado";
        }
    }

    @PostMapping("/guardar")
    public String guardarUsuario(
            @ModelAttribute Usuario usuario,
            RedirectAttributes redirectAttributes) {

        try {
            Long idNuevo = usuarioService.crearUsuario(usuario);
            redirectAttributes.addFlashAttribute("mensaje",
                    "Usuario creado exitosamente con ID: " + idNuevo);
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/usuarios/";
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
            RedirectAttributes redirectAttributes) {

        try {
            usuario.setIdUsuario(id);
            usuarioService.actualizarUsuario(usuario);
            redirectAttributes.addFlashAttribute("mensaje",
                    "Usuario actualizado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/usuarios/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje",
                    "Error al actualizar usuario: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/usuarios/editar/" + id;
        }
    }

    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstadoUsuario(
            @PathVariable Long id,
            @RequestParam Long idEstado,
            RedirectAttributes redirectAttributes) {

        try {
            usuarioService.cambiarEstadoUsuario(id, idEstado);
            String estado = idEstado == 1L ? "activado" : "desactivado";
            redirectAttributes.addFlashAttribute("mensaje",
                    "Usuario " + estado + " exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "info");
            return "redirect:/usuarios/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje",
                    "Error al cambiar estado: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/usuarios/";
        }
    }

    @PostMapping("/activar/{id}")
    public String activarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return cambiarEstadoUsuario(id, 1L, redirectAttributes);
    }

    @PostMapping("/desactivar/{id}")
    public String desactivarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return cambiarEstadoUsuario(id, 2L, redirectAttributes);
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.eliminarUsuario(id);
            redirectAttributes.addFlashAttribute("mensaje",
                    "Usuario eliminado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "warning");
            return "redirect:/usuarios/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje",
                    "Error al eliminar usuario: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/usuarios/";
        }
    }

    @GetMapping("/buscar")
    public String buscarUsuarios(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long tipoUsuario,
            Model model) {

        List<Usuario> usuarios;

        if (nombre != null && !nombre.trim().isEmpty()) {
            usuarios = usuarioService.buscarPorNombre(nombre);
            model.addAttribute("criterioBusqueda", "Nombre: " + nombre);
        } else if (tipoUsuario != null) {
            usuarios = usuarioService.buscarPorTipoUsuario(tipoUsuario);
            model.addAttribute("criterioBusqueda", "Tipo de usuario: " + tipoUsuario);
        } else {
            usuarios = usuarioService.obtenerTodosUsuarios();
        }

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalResultados", usuarios.size());
        return "usuarios/listado";
    }

    @GetMapping("/activos")
    public String listarActivos(Model model) {
        List<Usuario> usuarios = usuarioService.obtenerUsuariosActivos();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("criterioBusqueda", "Usuarios Activos");
        model.addAttribute("totalResultados", usuarios.size());
        return "usuarios/listado";
    }

    @GetMapping("/inactivos")
    public String listarInactivos(Model model) {
        List<Usuario> usuarios = usuarioService.obtenerUsuariosInactivos();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("criterioBusqueda", "Usuarios Inactivos");
        model.addAttribute("totalResultados", usuarios.size());
        return "usuarios/listado";
    }
}

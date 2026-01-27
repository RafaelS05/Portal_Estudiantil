package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Usuario;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.TipoUsuario;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Estado;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    // Página principal - Listar todos los usuarios
    @GetMapping
    public String gestionUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.obtenerTodosUsuarios();
        List<TipoUsuario> tiposUsuario = usuarioService.obtenerTodosTiposUsuario();
        List<Estado> estados = usuarioService.obtenerTodosEstados();
        
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("tiposUsuario", tiposUsuario);
        model.addAttribute("estados", estados);
        model.addAttribute("pageTitle", "Gestión de Usuarios");
        
        return "usuarios";
    }
    
    // Crear usuario
    @PostMapping("/crear")
    public String crearUsuario(
            @RequestParam String nombre,
            @RequestParam String primerApellido,
            @RequestParam(required = false) String segundoApellido,
            @RequestParam Long idTipoUsuario,
            @RequestParam Long idEstado) {
        
        usuarioService.crearUsuario(nombre, primerApellido, segundoApellido, idTipoUsuario, idEstado);
        return "redirect:/usuarios?success=true";
    }
    
    // Eliminar usuario
    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return "redirect:/usuarios?success=true";
    }
    
    // Cambiar estado de usuario por ID de estado
    @GetMapping("/cambiar-estado/{idUsuario}/{idEstado}")
    public String cambiarEstadoUsuario(
            @PathVariable Long idUsuario,
            @PathVariable Long idEstado) {
        
        usuarioService.cambiarEstadoUsuario(idUsuario, idEstado);
        return "redirect:/usuarios?success=true";
    }
    
    // Activar usuario (busca automáticamente el estado "Activo")
    @GetMapping("/activar/{id}")
    public String activarUsuario(@PathVariable Long id) {
        try {
            usuarioService.activarUsuario(id);
            return "redirect:/usuarios?success=true";
        } catch (Exception e) {
            return "redirect:/usuarios?error=No se pudo activar el usuario. Asegúrate de que existe el estado 'Activo' en la BD";
        }
    }
    
    // Desactivar usuario (busca automáticamente el estado "Inactivo")
    @GetMapping("/desactivar/{id}")
    public String desactivarUsuario(@PathVariable Long id) {
        try {
            usuarioService.desactivarUsuario(id);
            return "redirect:/usuarios?success=true";
        } catch (Exception e) {
            return "redirect:/usuarios?error=No se pudo desactivar el usuario. Asegúrate de que existe el estado 'Inactivo' en la BD";
        }
    }
    
    // Buscar usuarios
    @GetMapping("/buscar")
    public String buscarUsuarios(@RequestParam(required = false) String busqueda, Model model) {
        List<Usuario> usuarios;
        
        if (busqueda == null || busqueda.trim().isEmpty()) {
            usuarios = usuarioService.obtenerTodosUsuarios();
        } else {
            usuarios = usuarioService.buscarUsuarios(busqueda);
        }
        
        List<TipoUsuario> tiposUsuario = usuarioService.obtenerTodosTiposUsuario();
        List<Estado> estados = usuarioService.obtenerTodosEstados();
        
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("tiposUsuario", tiposUsuario);
        model.addAttribute("estados", estados);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("pageTitle", "Búsqueda: " + (busqueda != null ? busqueda : "Todos los usuarios"));
        
        return "usuarios";
    }
    
    // Ver detalle de usuario (opcional)
    @GetMapping("/detalle/{id}")
    public String verDetalleUsuario(@PathVariable Long id, Model model) {
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
            model.addAttribute("usuario", usuario);
            model.addAttribute("pageTitle", "Detalle: " + usuario.getNombreCompleto());
            return "detalle-usuario"; // Necesitarías crear esta vista
        } catch (Exception e) {
            return "redirect:/usuarios?error=Usuario no encontrado";
        }
    }
}
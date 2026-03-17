package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Evento;
import PortalEstudiantil.ProyectoPortalEstudiantil.Security.PortalUserDetails;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.NoticiaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class NoticiaController {

    private final NoticiaService noticiaService;

    public NoticiaController(NoticiaService noticiaService) {
        this.noticiaService = noticiaService;
    }

    // =========================================================
    // PANTALLA DE INICIO — todos los usuarios la ven
    // =========================================================

    // Esta es la pantalla principal del portal.
    // Reemplaza el "Bienvenido al Portal" por el feed de noticias.
    // El badge del topbar también se alimenta desde aquí.
    @GetMapping("/")
    public String inicio(Model model,
            @AuthenticationPrincipal PortalUserDetails userDetails) {

        model.addAttribute("noticias", noticiaService.listarParaInicio());

        // Para el badge: contar noticias nuevas desde el último login.
        // Si el usuario aún no tiene último login (primer ingreso), se muestran las de los últimos 7 días.
        LocalDate desdeLogin = null; // aquí se podría obtener del perfil si se guarda en sesión
        long noticiasNuevas = noticiaService.contarNoticiasNuevas(desdeLogin);
        model.addAttribute("noticiasNuevas", noticiasNuevas);
        model.addAttribute("pageTitle", "Inicio");

        return "index";
    }

    // =========================================================
    // PANEL DE GESTIÓN — solo Administrador
    // =========================================================

    // Lista todas las noticias para que el admin las gestione.
    @GetMapping("/noticias")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String gestion(Model model) {
        model.addAttribute("noticias", noticiaService.listarParaAdmin());
        model.addAttribute("pageTitle", "Noticias y Anuncios");
        return "noticias/gestion";
    }

    // Muestra el formulario vacío para crear una nueva noticia.
    @GetMapping("/noticias/nuevo")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String formularioNuevo(Model model) {
        model.addAttribute("evento", new Evento());
        model.addAttribute("pageTitle", "Nueva Noticia");
        return "noticias/formulario";
    }

    // Muestra el formulario con los datos de una noticia existente para editarla.
    @GetMapping("/noticias/editar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String formularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("evento", noticiaService.obtenerPorId(id));
        model.addAttribute("pageTitle", "Editar Noticia");
        return "noticias/formulario";
    }

    // Recibe el formulario y crea o actualiza según si el ID viene o no.
    @PostMapping("/noticias/guardar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String guardar(@ModelAttribute Evento evento,
            @AuthenticationPrincipal PortalUserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            if (evento.getIdEvento() == null) {
                noticiaService.crear(evento, userDetails.getUsername());
                redirectAttributes.addFlashAttribute("mensajeExito", "Noticia creada exitosamente.");
            } else {
                noticiaService.actualizar(evento, userDetails.getUsername());
                redirectAttributes.addFlashAttribute("mensajeExito", "Noticia actualizada exitosamente.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al guardar: " + e.getMessage());
        }
        return "redirect:/noticias";
    }

    // Activa una noticia (estado 1).
    @PostMapping("/noticias/activar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String activar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            noticiaService.cambiarEstado(id, 1L);
            redirectAttributes.addFlashAttribute("mensajeExito", "Noticia activada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error: " + e.getMessage());
        }
        return "redirect:/noticias";
    }

    // Desactiva una noticia (estado 2).
    @PostMapping("/noticias/desactivar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String desactivar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            noticiaService.cambiarEstado(id, 2L);
            redirectAttributes.addFlashAttribute("mensajeExito", "Noticia desactivada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error: " + e.getMessage());
        }
        return "redirect:/noticias";
    }
}
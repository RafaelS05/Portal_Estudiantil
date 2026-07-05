package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Evento;
import PortalEstudiantil.ProyectoPortalEstudiantil.Security.PortalUserDetails;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.EventoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    // =========================================================
    // GESTIÓN (solo Administrador)
    // =========================================================

    // Lista todos los eventos para que el admin los gestione.
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String gestion(Model model) {
        model.addAttribute("eventos", eventoService.listarParaAdmin());
        model.addAttribute("pageTitle", "Gestión de Eventos");
        return "eventos/gestion";
    }

    // Formulario para crear un nuevo evento.
    @GetMapping("/nuevo")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String formularioNuevo(Model model) {
        if (!model.containsAttribute("evento")) {
            model.addAttribute("evento", new Evento());
        }
        model.addAttribute("pageTitle", "Nuevo Evento");
        return "eventos/formulario";
    }

    // Formulario con datos existentes para editar.
    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String formularioEditar(@PathVariable Long id, Model model) {
        if (!model.containsAttribute("evento")) {
            model.addAttribute("evento", eventoService.obtenerPorId(id));
        }
        model.addAttribute("pageTitle", "Editar Evento");
        return "eventos/formulario";
    }

    // Guarda: crea si no tiene ID, actualiza si tiene ID.
    @PostMapping("/guardar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String guardar(@ModelAttribute Evento evento,
            @AuthenticationPrincipal PortalUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        String errorValidacion = null;

        // La fecha de inicio no puede ser anterior a hoy
        // (solo aplica al crear un evento nuevo; al editar uno ya existente
        // se permite conservar su fecha original)
        if (evento.getIdEvento() == null
                && evento.getFechaInicio() != null
                && evento.getFechaInicio().isBefore(LocalDate.now())) {
            errorValidacion = "La fecha de inicio no puede ser anterior a hoy.";
        }

        // La fecha de fin no puede ser anterior a la de inicio
        if (errorValidacion == null
                && evento.getFechaFin() != null && evento.getFechaInicio() != null
                && evento.getFechaFin().isBefore(evento.getFechaInicio())) {
            errorValidacion = "La fecha de fin no puede ser anterior a la fecha de inicio.";
        }

        if (errorValidacion != null) {
            redirectAttributes.addFlashAttribute("mensajeError", errorValidacion);
            redirectAttributes.addFlashAttribute("evento", evento);
            return (evento.getIdEvento() == null)
                    ? "redirect:/eventos/nuevo"
                    : "redirect:/eventos/editar/" + evento.getIdEvento();
        }

        try {
            if (evento.getIdEvento() == null) {
                eventoService.crear(evento, userDetails.getUsername());
                redirectAttributes.addFlashAttribute("mensajeExito", "Evento creado exitosamente.");
            } else {
                eventoService.actualizar(evento, userDetails.getUsername());
                redirectAttributes.addFlashAttribute("mensajeExito", "Evento actualizado exitosamente.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al guardar: " + e.getMessage());
        }
        return "redirect:/eventos";
    }

    // Activa un evento (estado 1 = ACTIVO → aparece en el calendario).
    @PostMapping("/activar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String activar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventoService.cambiarEstado(id, 1L);
            redirectAttributes.addFlashAttribute("mensajeExito", "Evento activado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error: " + e.getMessage());
        }
        return "redirect:/eventos";
    }

    // Desactiva un evento (estado 2 = INACTIVO → desaparece del calendario).
    @PostMapping("/desactivar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String desactivar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventoService.cambiarEstado(id, 2L);
            redirectAttributes.addFlashAttribute("mensajeExito", "Evento desactivado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error: " + e.getMessage());
        }
        return "redirect:/eventos";
    }
}
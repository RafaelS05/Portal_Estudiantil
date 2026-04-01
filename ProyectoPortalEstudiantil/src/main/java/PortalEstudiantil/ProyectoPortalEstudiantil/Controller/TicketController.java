package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Ticket;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.TicketEvaluacionSoporte;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Usuario;
import PortalEstudiantil.ProyectoPortalEstudiantil.Security.PortalUserDetails;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.TicketAdjuntoService;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.TicketCategoriaService;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.TicketEvaluacionService;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.TicketService;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.UsuarioService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/soporte")
public class TicketController {

    @Autowired
    private TicketService ticketService;
    @Autowired
    private TicketCategoriaService categoriaService;
    @Autowired
    private TicketAdjuntoService adjuntoService;
    @Autowired
    private TicketEvaluacionService evaluacionService;
    @Autowired
    private UsuarioService usuarioService;

    // LISTADO — admin ve todos, los demás solo los suyos
    private String nombreCompleto(Long idUsuario) {
        Usuario u = usuarioService.obtenerUsuario(idUsuario);
        return (u.getNombre() + " "
                + u.getPrimerApellido() + " "
                + (u.getSegundoApellido() != null ? u.getSegundoApellido() : "")).trim();
    }

    private Map<Long, String> mapaCategoriasActivas() {
        return categoriaService.listarActivas().stream()
                .collect(Collectors.toMap(
                        c -> c.getIdCategoriaTicket(),
                        c -> c.getNombre()
                ));
    }

    @GetMapping({"", "/"})
    public String listar(
            @RequestParam(required = false) Long idEstado,
            @RequestParam(required = false) Long idCategoria,
            Model model,
            @AuthenticationPrincipal PortalUserDetails userDetails) {

        boolean esAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));

        List<Ticket> tickets;
        if (esAdmin) {
            tickets = ticketService.filtrarAdmin(idEstado, idCategoria);
        } else {
            tickets = ticketService.filtrarUsuario(userDetails.getIdUsuario(), idEstado, idCategoria);
        }

        Map<Long, String> nombresUsuarios = tickets.stream()
                .map(Ticket::getIdUsuarioReportaFk)
                .distinct()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> nombreCompleto(id)
                ));

        model.addAttribute("tickets",          tickets);
        model.addAttribute("categorias",        categoriaService.listarActivas());
        model.addAttribute("nombresUsuarios",   nombresUsuarios);
        model.addAttribute("nombresCategorias", mapaCategoriasActivas());
        model.addAttribute("esAdmin",           esAdmin);
        model.addAttribute("idEstado",          idEstado);
        model.addAttribute("idCategoria",       idCategoria);
        model.addAttribute("pageTitle",         "Soporte Técnico");

        return "soporte/listado";
    }

    @GetMapping("/{id}")
    public String detalle(
            @PathVariable Long id,
            Model model,
            @AuthenticationPrincipal PortalUserDetails userDetails) {

        Ticket ticket = ticketService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado: " + id));

        boolean esAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
        Long idUsuario  = userDetails.getIdUsuario();

        model.addAttribute("ticket",            ticket);
        model.addAttribute("adjuntos",          adjuntoService.listarPorTicket(id));
        model.addAttribute("evaluacion",        evaluacionService.buscarPorTicket(id).orElse(null));
        model.addAttribute("categorias",        categoriaService.listarActivas());
        model.addAttribute("nombresCategorias", mapaCategoriasActivas());
        model.addAttribute("nombreReporta",     nombreCompleto(ticket.getIdUsuarioReportaFk()));
        model.addAttribute("esAdmin",           esAdmin);
        model.addAttribute("idUsuario",         idUsuario);
        model.addAttribute("yaEvaluo",          evaluacionService.yaEvaluo(idUsuario, id));
        model.addAttribute("pageTitle",         "Detalle de Ticket #" + id);

        return "soporte/detalle";
    }

    @PostMapping("/insertar")
    public String insertar(
            @ModelAttribute Ticket ticket,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal PortalUserDetails userDetails) {
        try {
            ticket.setIdUsuarioReportaFk(userDetails.getIdUsuario());
            ticketService.insertar(ticket);
            redirectAttributes.addFlashAttribute("mensajeExito", "Incidencia registrada exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al registrar: " + e.getMessage());
        }
        return "redirect:/soporte";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(
            @PathVariable Long id,
            @RequestParam Long idEstado,
            RedirectAttributes redirectAttributes) {
        try {
            ticketService.cambiarEstado(id, idEstado);
            redirectAttributes.addFlashAttribute("mensajeExito", "Estado actualizado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al cambiar estado: " + e.getMessage());
        }
        return "redirect:/soporte/" + id;
    }

    @PostMapping("/{id}/modificar")
    public String modificar(
            @PathVariable Long id,
            @ModelAttribute Ticket ticket,
            RedirectAttributes redirectAttributes) {
        try {
            ticketService.modificar(id, ticket);
            redirectAttributes.addFlashAttribute("mensajeExito", "Ticket modificado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al modificar: " + e.getMessage());
        }
        return "redirect:/soporte/" + id;
    }

    @PostMapping("/{id}/cerrar")
    public String cerrar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            ticketService.cambiarEstado(id, 4L);
            redirectAttributes.addFlashAttribute("mensajeExito", "Ticket cerrado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al cerrar: " + e.getMessage());
        }
        return "redirect:/soporte/" + id;
    }

    @PostMapping("/{id}/confirmar")
    public String confirmarResolucion(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            ticketService.cambiarEstado(id, 5L);
            redirectAttributes.addFlashAttribute("mensajeExito", "Resolución confirmada. ¡Gracias por su respuesta!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al confirmar: " + e.getMessage());
        }
        return "redirect:/soporte/" + id;
    }

    @PostMapping("/{id}/evaluar")
    public String evaluar(
            @PathVariable Long id,
            @ModelAttribute TicketEvaluacionSoporte evaluacion,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal PortalUserDetails userDetails) {
        try {
            evaluacion.setIdUsuarioFk(userDetails.getIdUsuario());
            evaluacion.setIdTicketFk(id);
            evaluacionService.insertar(evaluacion);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Gracias por evaluar el servicio!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al evaluar: " + e.getMessage());
        }
        return "redirect:/soporte/" + id;
    }
}
package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Aula;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Periodo;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.PeriodoRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.AulaService;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.PeriodoService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/gestionAcademica/periodo")
public class PeriodoController {
    
        @Autowired
    private PeriodoService periodoService;

    @GetMapping
    public String listaPeriodos(Model model) {
        List<PeriodoRepository.PeriodoRow> periodos = periodoService.listarResumen();
        Periodo periodoActual = periodoService.obtenerPeriodoActual();

        model.addAttribute("periodos", periodos);
        model.addAttribute("periodoActual", periodoActual);
        model.addAttribute("totalActivos", periodoService.contarActivos());
        model.addAttribute("paginaActual", "gestion-academica");
        model.addAttribute("tituloPagina", "Gestión Académica");
        return "periodos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoPeriodo (Model model) {
        model.addAttribute("periodo", new Periodo());
        model.addAttribute("modoEdicion", false);
        model.addAttribute("paginaActual", "gestion-academica");
        model.addAttribute("tituloPagina", "Gestión Académica");
        return "periodos/formulario";
    }

    @PostMapping("/guardar")
    public String guardarPeriodo (@RequestParam String nombre,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                          RedirectAttributes flash) {
        try {
            periodoService.insertar(nombre, fechaInicio, fechaFin);
            flash.addFlashAttribute("mensajeExito", "Período creado correctamente.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
            return "redirect:/gestion-academica/periodos/nuevo";
        }
        return "redirect:/gestion-academica/periodos";
    }

    @GetMapping("/editar/{id}")
    public String editarPeriodo (@PathVariable Long id, Model model, RedirectAttributes flash) {
        Periodo periodo = periodoService.buscarPorId(id);
        if (periodo == null) {
            flash.addFlashAttribute("mensajeError", "Período no encontrado.");
            return "redirect:/gestion-academica/periodos";
        }
        model.addAttribute("periodo", periodo);
        model.addAttribute("modoEdicion", true);
        model.addAttribute("paginaActual", "gestion-academica");
        model.addAttribute("tituloPagina", "Gestión Académica");
        return "periodos/formulario";
    }

    @PostMapping("/actualizar")
    public String actualizarPeriodo (@RequestParam Long idPeriodo,
                             @RequestParam String nombre,
                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                             RedirectAttributes flash) {
        try {
            periodoService.modificar(idPeriodo, nombre, fechaInicio, fechaFin);
            flash.addFlashAttribute("mensajeExito", "Período actualizado correctamente.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
            return "redirect:/gestion-academica/periodos/editar/" + idPeriodo;
        }
        return "redirect:/gestion-academica/periodos";
    }

    @PostMapping("/cambiar-estado")
    public String cambiarEstadoPerido (@RequestParam Long idPeriodo,
                                @RequestParam Long idEstado,
                                RedirectAttributes flash) {
        periodoService.cambiarEstado(idPeriodo, idEstado);
        String msg = idEstado == 1L ? "Período activado." : "Período desactivado.";
        flash.addFlashAttribute("mensajeExito", msg);
        return "redirect:/gestion-academica/periodos";
    }

}

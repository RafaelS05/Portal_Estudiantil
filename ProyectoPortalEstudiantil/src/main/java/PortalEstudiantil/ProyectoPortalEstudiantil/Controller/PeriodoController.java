package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Service.PeriodoService;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/gestionAcademica/periodo")
public class PeriodoController {

    @Autowired
    private PeriodoService periodoService;

    @PostMapping("/guardar")
    public String guardar(
            @RequestParam String nombre,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            RedirectAttributes flash) {
        try {
            periodoService.insertar(nombre, fechaInicio, fechaFin);
            flash.addFlashAttribute("mensajeExito", "Período creado correctamente.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/gestionAcademica?tab=periodos";
    }

    @PostMapping("/actualizar")
    public String actualizar(
            @RequestParam Long idPeriodo,
            @RequestParam String nombre,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            RedirectAttributes flash) {
        try {
            periodoService.modificar(idPeriodo, nombre, fechaInicio, fechaFin);
            flash.addFlashAttribute("mensajeExito", "Período actualizado correctamente.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/gestionAcademica?tab=periodos";
    }

    @PostMapping("/cambiar-estado")
    public String cambiarEstado(
            @RequestParam Long idPeriodo,
            @RequestParam Long idEstado,
            RedirectAttributes flash) {
        periodoService.cambiarEstado(idPeriodo, idEstado);
        flash.addFlashAttribute("mensajeExito",
                idEstado == 1L ? "Período activado." : "Período desactivado.");
        return "redirect:/gestionAcademica?tab=periodos";
    }
}

package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.AsistenciaRepository.AsistenciaListadoRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.AsistenciaRepository.PaseListaRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.AsistenciaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/asistencias")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    public AsistenciaController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    // ══════════════════════════════════════════════════════════════════
    // LISTADO PRINCIPAL
    // ══════════════════════════════════════════════════════════════════
    @GetMapping({"", "/"})
    public String listar(
            @RequestParam(value = "idSeccionMateria", required = false) Long idSeccionMateria,
            @RequestParam(value = "fecha", required = false) String fechaStr,
            Model model) {

        LocalDate fecha = parseFecha(fechaStr);
        List<AsistenciaListadoRow> asistencias = asistenciaService.listar(idSeccionMateria, fecha);

        long totalPresentes = asistencias.stream().filter(a -> Long.valueOf(1L).equals(a.getIdEstadoFk())).count();
        long totalAusentes = asistencias.stream().filter(a -> Long.valueOf(2L).equals(a.getIdEstadoFk())).count();
        long totalJustificados = asistencias.stream().filter(a -> Long.valueOf(3L).equals(a.getIdEstadoFk())).count();

        model.addAttribute("asistencias", asistencias);
        model.addAttribute("total", asistencias.size());
        model.addAttribute("totalPresentes", totalPresentes);
        model.addAttribute("totalAusentes", totalAusentes);
        model.addAttribute("totalJustificados", totalJustificados);
        model.addAttribute("idSeccionMateria", idSeccionMateria);
        model.addAttribute("fecha", fechaStr);

        return "asistencias/listado";
    }

    // ══════════════════════════════════════════════════════════════════
    // PASE DE LISTA – Formulario
    // ══════════════════════════════════════════════════════════════════
    @GetMapping("/paseLista")
    public String formularioPaseLista(
            @RequestParam(value = "idSeccionMateria", required = false) Long idSeccionMateria,
            @RequestParam(value = "fecha", required = false) String fechaStr,
            Model model,
            RedirectAttributes redirectAttributes) {

        model.addAttribute("idSeccionMateria", idSeccionMateria);
        model.addAttribute("fecha", fechaStr);

        if (idSeccionMateria != null && fechaStr != null && !fechaStr.isBlank()) {
            try {
                LocalDate fecha = LocalDate.parse(fechaStr);
                List<PaseListaRow> registros = asistenciaService.cargarPaseLista(idSeccionMateria, fecha);
                model.addAttribute("registros", registros);
            } catch (DateTimeParseException e) {
                model.addAttribute("mensajeError", "Formato de fecha inválido. Use AAAA-MM-DD.");
            } catch (Exception e) {
                model.addAttribute("mensajeError", "Error al cargar estudiantes: " + e.getMessage());
            }
        }

        return "asistencias/paseLista";
    }

    // ══════════════════════════════════════════════════════════════════
    // PASE DE LISTA – Guardar
    // ══════════════════════════════════════════════════════════════════
    @PostMapping("/paseLista/guardar")
    public String guardarPaseLista(
            @RequestParam("idSeccionMateria") Long idSeccionMateria,
            @RequestParam("fecha") String fechaStr,
            @RequestParam Map<String, String> allParams,
            RedirectAttributes redirectAttributes) {

        try {
            LocalDate fecha = LocalDate.parse(fechaStr);

            // Parsear parámetros con patrón "estado_{idMatricula}"
            Map<Long, Long> estadosPorMatricula = new HashMap<>();
            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                if (entry.getKey().startsWith("estado_")) {
                    Long idMatricula = Long.parseLong(entry.getKey().substring(7));
                    Long idEstado = Long.parseLong(entry.getValue());
                    estadosPorMatricula.put(idMatricula, idEstado);
                }
            }

            asistenciaService.guardarPaseLista(idSeccionMateria, fecha, estadosPorMatricula);

            redirectAttributes.addFlashAttribute("mensajeExito", "Pase de lista guardado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al guardar el pase de lista: " + e.getMessage());
        }

        return "redirect:/asistencias";
    }

    // ══════════════════════════════════════════════════════════════════
    // JUSTIFICAR AUSENCIA
    // ══════════════════════════════════════════════════════════════════
    @PostMapping("/justificar/{id}")
    public String justificar(
            @PathVariable Long id,
            @RequestParam(value = "idSeccionMateria", required = false) Long idSeccionMateria,
            RedirectAttributes redirectAttributes) {

        try {
            asistenciaService.justificar(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Asistencia justificada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al justificar: " + e.getMessage());
        }

        String redirect = "/asistencias";
        if (idSeccionMateria != null) {
            redirect += "?idSeccionMateria=" + idSeccionMateria;
        }
        return "redirect:" + redirect;
    }

    // ══════════════════════════════════════════════════════════════════
    // CAMBIAR ESTADO (modal)
    // ══════════════════════════════════════════════════════════════════
    @PostMapping("/cambiarEstado/{id}")
    public String cambiarEstado(
            @PathVariable Long id,
            @RequestParam("idEstado") Long idEstado,
            @RequestParam(value = "idSeccionMateria", required = false) Long idSeccionMateria,
            RedirectAttributes redirectAttributes) {

        try {
            asistenciaService.cambiarEstado(id, idEstado);
            redirectAttributes.addFlashAttribute("mensajeExito", "Estado actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al cambiar estado: " + e.getMessage());
        }

        String redirect = "/asistencias";
        if (idSeccionMateria != null) {
            redirect += "?idSeccionMateria=" + idSeccionMateria;
        }
        return "redirect:" + redirect;
    }

    // ══════════════════════════════════════════════════════════════════
    // UTIL
    // ══════════════════════════════════════════════════════════════════
    private LocalDate parseFecha(String fechaStr) {
        if (fechaStr == null || fechaStr.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(fechaStr);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}

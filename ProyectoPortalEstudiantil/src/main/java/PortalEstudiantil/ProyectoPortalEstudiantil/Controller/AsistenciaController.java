package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.AsistenciaRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.AsistenciaRepository.HijoDropdownRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.AsistenciaRepository.HistorialAsistenciaHijoRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.AsistenciaRepository.AsistenciaListadoRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.AsistenciaRepository.PaseListaRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.AsistenciaRepository.SeccionMateriaDropdownRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Security.PortalUserDetails;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.AsistenciaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/asistencias")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    public AsistenciaController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    // ══════════════════════════════════════════════════════════════════
    // LISTADO PRINCIPAL (filtrado por docente si el rol es PROFESOR)
    // ══════════════════════════════════════════════════════════════════
    @GetMapping({"", "/"})
    public String listar(
            @RequestParam(required = false) Long idSeccionMateria,
            @RequestParam(value = "fecha", required = false) String fechaStr,
            Model model,
            @AuthenticationPrincipal PortalUserDetails userDetails) { // ← AGREGADO

        // Determinar si es docente y obtener su ID
        boolean esProfesor = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PROFESOR"));

        Long idDocente = null;
        if (esProfesor) {
            idDocente = asistenciaService.obtenerIdUsuarioPorEmail(userDetails.getUsername());
        }

        LocalDate fecha = parseFecha(fechaStr);
        List<AsistenciaListadoRow> asistencias = asistenciaService.listar(idSeccionMateria, fecha, idDocente); // ← ACTUALIZADO

        long totalPresentes = asistencias.stream().filter(a -> Long.valueOf(7L).equals(a.getIdEstadoFk())).count();
        long totalAusentes = asistencias.stream().filter(a -> Long.valueOf(8L).equals(a.getIdEstadoFk())).count();
        long totalJustificados = asistencias.stream().filter(a -> Long.valueOf(10L).equals(a.getIdEstadoFk())).count();

        model.addAttribute("asistencias", asistencias);
        model.addAttribute("total", asistencias.size());
        model.addAttribute("totalPresentes", totalPresentes);
        model.addAttribute("totalAusentes", totalAusentes);
        model.addAttribute("totalJustificados", totalJustificados);
        model.addAttribute("idSeccionMateria", idSeccionMateria);
        model.addAttribute("fecha", fechaStr);
        model.addAttribute("pageTitle", "Asistencias");

        return "asistencias/listado";
    }

    // ══════════════════════════════════════════════════════════════════
    // AJAX – Secciones-Materia para el dropdown del modal
    // ══════════════════════════════════════════════════════════════════
    @GetMapping("/seccionesMateria")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> obtenerSeccionesMateria(
            @AuthenticationPrincipal PortalUserDetails userDetails) {

        boolean esProfesor = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PROFESOR"));

        Long idDocente = null;
        if (esProfesor) {
            idDocente = asistenciaService.obtenerIdUsuarioPorEmail(userDetails.getUsername());
        }

        List<SeccionMateriaDropdownRow> rows
                = asistenciaService.obtenerSeccionesMateria(idDocente, !esProfesor);

        List<Map<String, Object>> result = rows.stream().map(r -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", r.getIdSeccionMateria());
            map.put("nombre", r.getNombreMateria() + " \u2013 Sección " + r.getNumeroSeccion());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // ══════════════════════════════════════════════════════════════════
    // AJAX – Estudiantes para el modal del pase de lista
    // ══════════════════════════════════════════════════════════════════
    @GetMapping("/paseLista/estudiantes")
    @ResponseBody
    public ResponseEntity<?> cargarEstudiantesAjax(
            @RequestParam Long idSeccionMateria,
            @RequestParam String fecha) {

        try {
            LocalDate fechaDate = LocalDate.parse(fecha);
            List<PaseListaRow> rows = asistenciaService.cargarPaseLista(idSeccionMateria, fechaDate);

            List<Map<String, Object>> result = rows.stream().map(r -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("idMatricula", r.getIdMatriculaFk());
                map.put("nombre", r.getNombreEstudiante());
                map.put("materia", r.getNombreMateria());
                map.put("seccion", r.getNumeroSeccion());
                map.put("idAsistencia", r.getIdAsistencia());
                map.put("idEstado", r.getIdEstadoFk());
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // PASE DE LISTA – Guardar (responde JSON para el modal AJAX)
    // ══════════════════════════════════════════════════════════════════
    @PostMapping("/paseLista/guardar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> guardarPaseLista(
            @RequestParam("idSeccionMateria") Long idSeccionMateria,
            @RequestParam("fecha") String fechaStr,
            @RequestParam Map<String, String> allParams) {

        try {
            LocalDate fecha = LocalDate.parse(fechaStr);

            Map<Long, Long> estadosPorMatricula = new HashMap<>();
            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                if (entry.getKey().startsWith("estado_")) {
                    Long idMatricula = Long.parseLong(entry.getKey().substring(7));
                    Long idEstado = Long.parseLong(entry.getValue());
                    estadosPorMatricula.put(idMatricula, idEstado);
                }
            }

            asistenciaService.guardarPaseLista(idSeccionMateria, fecha, estadosPorMatricula);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "mensaje", "Pase de lista guardado exitosamente"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "mensaje", e.getMessage()
            ));
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // JUSTIFICAR AUSENCIA
    // ══════════════════════════════════════════════════════════════════
    @PostMapping("/justificar/{id}")
    public String justificar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            asistenciaService.justificar(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Asistencia justificada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al justificar: " + e.getMessage());
        }
        return "redirect:/asistencias";
    }

    // ══════════════════════════════════════════════════════════════════
    // CAMBIAR ESTADO (desde el mini-modal de edición)
    // ══════════════════════════════════════════════════════════════════
    @PostMapping("/cambiarEstado/{id}")
    public String cambiarEstado(
            @PathVariable Long id,
            @RequestParam("idEstado") Long idEstado,
            RedirectAttributes redirectAttributes) {

        try {
            asistenciaService.cambiarEstado(id, idEstado);
            redirectAttributes.addFlashAttribute("mensajeExito", "Estado actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al cambiar estado: " + e.getMessage());
        }
        return "redirect:/asistencias";
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

    // ══════════════════════════════════════════════════════════════════
    // HISTORIAL DE ASISTENCIA – ENCARGADO
    // ══════════════════════════════════════════════════════════════════
    @GetMapping("/historial")
    public String historialHijo(
            @RequestParam(required = false) Long idEstudiante,
            Model model,
            @AuthenticationPrincipal PortalUserDetails userDetails) {

        Long idEncargado = asistenciaService.obtenerIdUsuarioPorEmail(userDetails.getUsername());

        List<AsistenciaRepository.HijoDropdownRow> hijos
                = asistenciaService.obtenerHijosDeEncargado(idEncargado);

        List<AsistenciaRepository.HistorialAsistenciaHijoRow> historial
                = asistenciaService.obtenerHistorialHijo(idEncargado, idEstudiante);

        model.addAttribute("hijos", hijos);
        model.addAttribute("historial", historial);
        model.addAttribute("idEstudiante", idEstudiante);
        model.addAttribute("pageTitle", "Historial de Asistencia");

        return "asistencias/historial";
    }
}

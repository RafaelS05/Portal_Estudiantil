package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.ReporteAcademico;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.ReporteAcademicoRepository.ReporteListadoRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.ReporteAcademicoRepository.ReporteDetalleRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.ReporteAcademicoRepository.EstudianteDisponibleRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Security.PortalUserDetails;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.ReporteAcademicoCorreoService;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.ReporteAcademicoPdfService;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.ReporteAcademicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/reportes")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class ReporteAcademicoController {

    @Autowired private ReporteAcademicoService        reporteService;
    @Autowired private ReporteAcademicoPdfService     pdfService;
    @Autowired private ReporteAcademicoCorreoService  correoService;

    // ==================== LISTADO ====================

    @GetMapping({"", "/"})
    public String listarReportes(
            @RequestParam(value = "busqueda", required = false) String busqueda,
            Model model) {

        List<ReporteListadoRow> reportes = (busqueda != null && !busqueda.trim().isEmpty())
                ? reporteService.buscarReportes(busqueda.trim())
                : reporteService.listarReportesActivos();

        long totalBoletas     = reportes.stream()
                .filter(r -> "BOLETA".equals(r.getNombreTipoReporte())).count();
        long totalHistoriales = reportes.stream()
                .filter(r -> "HISTORIAL".equals(r.getNombreTipoReporte())).count();

        model.addAttribute("reportes",         reportes);
        model.addAttribute("total",            reportes.size());
        model.addAttribute("totalBoletas",     totalBoletas);
        model.addAttribute("totalHistoriales", totalHistoriales);
        model.addAttribute("busqueda",         busqueda);
        model.addAttribute("pageTitle",        "Reportes Académicos");
        return "reporteacademico/Listado";
    }

    // ==================== VER DETALLE ====================

    @GetMapping("/ver/{id}")
    public String verReporte(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            List<ReporteDetalleRow> detalle = reporteService.obtenerDetalle(id);

            if (detalle == null || detalle.isEmpty()) {
                ra.addFlashAttribute("mensaje",     "Reporte no encontrado");
                ra.addFlashAttribute("tipoMensaje", "danger");
                return "redirect:/reportes";
            }

            ReporteDetalleRow cabecera = detalle.get(0);

            // Correo del estudiante (primera fila que lo traiga, si existe en la proyección)
            // Lo obtenemos buscando en el listado de disponibles por nombre
            String correoEstudiante = obtenerCorreoEstudiante(cabecera.getNombreEstudiante());

            model.addAttribute("detalle",           detalle);
            model.addAttribute("reporte",           reporteService.obtenerReporte(id));
            model.addAttribute("nombreEstudiante",  cabecera.getNombreEstudiante());
            model.addAttribute("nombreSeccion",     cabecera.getNombreSeccion());
            model.addAttribute("nombrePeriodo",     cabecera.getNombrePeriodo());
            model.addAttribute("tipoReporte",       cabecera.getNombreTipoReporte());
            model.addAttribute("promedio",          cabecera.getPromedioPonderado());
            model.addAttribute("fechaReporte",      cabecera.getFechaCreacionReporte());
            model.addAttribute("correoEstudiante",  correoEstudiante);
            model.addAttribute("pageTitle",         "Detalle de Reporte");
            return "reporteacademico/Detalle";

        } catch (Exception e) {
            ra.addFlashAttribute("mensaje",     "Error al cargar reporte: " + e.getMessage());
            ra.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/reportes";
        }
    }

    // ==================== DESCARGAR PDF ====================

    @GetMapping("/pdf/{id}")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Long id) {
        try {
            List<ReporteDetalleRow> detalle = reporteService.obtenerDetalle(id);
            if (detalle == null || detalle.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            byte[] pdf = pdfService.generarPdf(detalle);

            ReporteDetalleRow cab = detalle.get(0);
            String nombre = cab.getNombreEstudiante() != null
                    ? cab.getNombreEstudiante().replaceAll("\\s+", "_") : "boleta";
            String filename = "boleta_" + nombre + "_" + id + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==================== ENVIAR POR CORREO ====================

    @PostMapping("/enviar/{id}")
    public String enviarPorCorreo(
            @PathVariable Long id,
            @RequestParam String correoDestino,
            RedirectAttributes ra) {

        try {
            List<ReporteDetalleRow> detalle = reporteService.obtenerDetalle(id);
            if (detalle == null || detalle.isEmpty()) {
                ra.addFlashAttribute("mensaje",     "Reporte no encontrado");
                ra.addFlashAttribute("tipoMensaje", "danger");
                return "redirect:/reportes";
            }

            boolean enviado = correoService.enviarBoleta(detalle, correoDestino);

            if (enviado) {
                ra.addFlashAttribute("mensaje",
                        "Boleta enviada exitosamente a " + correoDestino);
                ra.addFlashAttribute("tipoMensaje", "success");
            } else {
                ra.addFlashAttribute("mensaje",
                        "No se pudo enviar el correo. Verifique la configuración de email.");
                ra.addFlashAttribute("tipoMensaje", "danger");
            }

        } catch (Exception e) {
            ra.addFlashAttribute("mensaje",     "Error al enviar: " + e.getMessage());
            ra.addFlashAttribute("tipoMensaje", "danger");
        }

        return "redirect:/reportes/ver/" + id;
    }

    // ==================== FORMULARIO NUEVO ====================

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(
            @RequestParam(value = "busquedaEstudiante", required = false) String busquedaEstudiante,
            Model model) {

        List<EstudianteDisponibleRow> estudiantes = List.of();
        String errorBusqueda = null;

        if (busquedaEstudiante != null && !busquedaEstudiante.trim().isEmpty()) {
            try {
                estudiantes = reporteService.buscarEstudiantes(busquedaEstudiante.trim());
                if (estudiantes.isEmpty()) errorBusqueda = "No se encontraron estudiantes con ese nombre.";
            } catch (Exception e) {
                errorBusqueda = e.getMessage();
            }
        }

        model.addAttribute("estudiantes",        estudiantes);
        model.addAttribute("busquedaEstudiante", busquedaEstudiante);
        model.addAttribute("errorBusqueda",      errorBusqueda);
        model.addAttribute("reporte",            new ReporteAcademico());
        model.addAttribute("titulo",             "Nueva Boleta de Calificaciones");
        model.addAttribute("accion",             "/reportes/guardar");
        model.addAttribute("urlBusqueda",        "/reportes/nuevo");
        model.addAttribute("btnTexto",           "Guardar");
        model.addAttribute("pageTitle",          "Nueva Boleta");
        return "reporteacademico/Formulario";
    }

    // ==================== FORMULARIO EDITAR ====================

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            ReporteAcademico reporte = reporteService.obtenerReporte(id);

            List<EstudianteDisponibleRow> estudiantes = List.of();
            try {
                List<ReporteDetalleRow> detalle = reporteService.obtenerDetalle(id);
                if (!detalle.isEmpty()) {
                    estudiantes = reporteService.buscarEstudiantes(detalle.get(0).getNombreEstudiante());
                }
            } catch (Exception ignored) {}

            model.addAttribute("reporte",     reporte);
            model.addAttribute("estudiantes", estudiantes);
            model.addAttribute("titulo",      "Editar Boleta de Calificaciones");
            model.addAttribute("accion",      "/reportes/actualizar/" + id);
            model.addAttribute("urlBusqueda", "/reportes/editar/" + id);
            model.addAttribute("btnTexto",    "Actualizar");
            model.addAttribute("pageTitle",   "Editar Boleta");
            return "reporteacademico/Formulario";

        } catch (Exception e) {
            ra.addFlashAttribute("mensaje",     "Reporte no encontrado");
            ra.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/reportes";
        }
    }

    // ==================== GUARDAR ====================

    @PostMapping("/guardar")
    public String guardarReporte(
            @RequestParam Long idMatricula,
            @RequestParam Long idPeriodo,
            @RequestParam Long idTipoReporte,
            @RequestParam(required = false) BigDecimal promedioPonderado,
            @AuthenticationPrincipal PortalUserDetails usuario,
            RedirectAttributes ra) {

        try {
            reporteService.crearReporte(idMatricula, idPeriodo, idTipoReporte,
                    promedioPonderado, usuario.getIdCredencial());
            ra.addFlashAttribute("mensaje",     "Boleta creada exitosamente");
            ra.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/reportes";

        } catch (Exception e) {
            ra.addFlashAttribute("mensaje",     "Error al crear boleta: " + e.getMessage());
            ra.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/reportes/nuevo";
        }
    }

    // ==================== ACTUALIZAR ====================

    @PostMapping("/actualizar/{id}")
    public String actualizarReporte(
            @PathVariable Long id,
            @RequestParam Long idMatricula,
            @RequestParam Long idPeriodo,
            @RequestParam Long idTipoReporte,
            @RequestParam(required = false) BigDecimal promedioPonderado,
            @AuthenticationPrincipal PortalUserDetails usuario,
            RedirectAttributes ra) {

        try {
            reporteService.modificarReporte(id, idMatricula, idPeriodo, idTipoReporte,
                    promedioPonderado, usuario.getIdCredencial());
            ra.addFlashAttribute("mensaje",     "Boleta actualizada exitosamente");
            ra.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/reportes/ver/" + id;

        } catch (Exception e) {
            ra.addFlashAttribute("mensaje",     "Error al actualizar: " + e.getMessage());
            ra.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/reportes/editar/" + id;
        }
    }

    // ==================== ELIMINAR ====================

    @PostMapping("/eliminar/{id}")
    public String eliminarReporte(@PathVariable Long id, RedirectAttributes ra) {
        try {
            reporteService.eliminarReporte(id);
            ra.addFlashAttribute("mensaje",     "Boleta eliminada exitosamente");
            ra.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            ra.addFlashAttribute("mensaje",     "Error al eliminar: " + e.getMessage());
            ra.addFlashAttribute("tipoMensaje", "danger");
        }
        return "redirect:/reportes";
    }

    // ==================== HELPER PRIVADO ====================

    /**
     * Intenta obtener el correo del estudiante buscando por nombre.
     * Devuelve cadena vacía si no lo encuentra.
     */
    private String obtenerCorreoEstudiante(String nombreEstudiante) {
        if (nombreEstudiante == null || nombreEstudiante.isBlank()) return "";
        try {
            // Usa la primera parte del nombre para la búsqueda
            String busqueda = nombreEstudiante.split("\\s+")[0];
            return reporteService.buscarEstudiantes(busqueda)
                    .stream()
                    .filter(e -> nombreEstudiante.equals(e.getNombreEstudiante()))
                    .findFirst()
                    .map(e -> e.getCorreo() != null ? e.getCorreo() : "")
                    .orElse("");
        } catch (Exception e) {
            return "";
        }
    }
}
package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Periodo;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.Estadisticas.AlertaEstadisticaRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.Estadisticas.AsistenciaEstudianteRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.Estadisticas.CalificacionPorMateriaRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.Estadisticas.CalificacionPorSeccionRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.Estadisticas.KpiGeneralRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.EstadisticasService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/estadisticas")
public class EstadisticasController {

    private final EstadisticasService estadisticasService;

    public EstadisticasController(EstadisticasService estadisticasService) {
        this.estadisticasService = estadisticasService;
    }

    // =========================================================
    // PANEL ADMINISTRADOR / DIRECTOR
    // =========================================================

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR')")
    public String panelAdmin(Model model, RedirectAttributes redirectAttributes) {
        try {
            Periodo periodo = estadisticasService.obtenerPeriodoActual();
            Long idPeriodo  = periodo.getIdPeriodo();

            KpiGeneralRow                  kpi        = estadisticasService.obtenerKpiGeneral(idPeriodo);
            List<CalificacionPorSeccionRow> porSeccion = estadisticasService.obtenerCalificacionesPorSeccion(idPeriodo);
            List<CalificacionPorMateriaRow> porMateria = estadisticasService.obtenerCalificacionesPorMateria(idPeriodo);
            List<AlertaEstadisticaRow>      alertas    = estadisticasService.obtenerAlertas(idPeriodo);

            // Arrays JSON para Chart.js
            String seccionLabels    = toJsonStringArray(porSeccion.stream()
                    .map(CalificacionPorSeccionRow::getSeccion).toList());
            String seccionPromedios = toJsonDoubleArray(porSeccion.stream()
                    .map(r -> r.getPromedio() != null ? r.getPromedio() : 0.0).toList());

            String materiaLabels    = toJsonStringArray(porMateria.stream()
                    .map(CalificacionPorMateriaRow::getMateria).toList());
            String materiaPromedios = toJsonDoubleArray(porMateria.stream()
                    .map(r -> r.getPromedio() != null ? r.getPromedio() : 0.0).toList());

            model.addAttribute("periodo",          periodo);
            model.addAttribute("kpi",              kpi);
            model.addAttribute("porSeccion",       porSeccion);
            model.addAttribute("porMateria",       porMateria);
            model.addAttribute("alertas",          alertas);
            model.addAttribute("umbral",           estadisticasService.getUmbralCritico());
            model.addAttribute("seccionLabels",    seccionLabels);
            model.addAttribute("seccionPromedios", seccionPromedios);
            model.addAttribute("materiaLabels",    materiaLabels);
            model.addAttribute("materiaPromedios", materiaPromedios);

            return "estadisticas/admin";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al cargar estadísticas: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/";
        }
    }

    // =========================================================
    // PANEL ENCARGADO (PADRE DE FAMILIA)
    // =========================================================

    @GetMapping("/encargado")
    @PreAuthorize("hasRole('ENCARGADO')")
    public String panelEncargado(Authentication authentication,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            Periodo periodo = estadisticasService.obtenerPeriodoActual();
            Long idPeriodo  = periodo.getIdPeriodo();

            List<AsistenciaEstudianteRow> asistencias =
                    estadisticasService.obtenerAsistenciaHijos(authentication, idPeriodo);

            String hijosLabels      = toJsonStringArray(asistencias.stream()
                    .map(AsistenciaEstudianteRow::getNombreEstudiante).toList());
            String hijosAsistencia  = toJsonDoubleArray(asistencias.stream()
                    .map(r -> r.getPorcentajeAsistencia() != null ? r.getPorcentajeAsistencia() : 0.0).toList());

            model.addAttribute("periodo",         periodo);
            model.addAttribute("asistencias",     asistencias);
            model.addAttribute("hijosLabels",     hijosLabels);
            model.addAttribute("hijosAsistencia", hijosAsistencia);

            return "estadisticas/encargado";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al cargar estadísticas: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/";
        }
    }

    // =========================================================
    // REDIRECCIONADOR — link del sidebar /estadisticas
    // =========================================================

    @GetMapping({"", "/"})
    public String redirigir(Authentication authentication) {
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ENCARGADO"))) {
            return "redirect:/estadisticas/encargado";
        }
        return "redirect:/estadisticas/admin";
    }

    // =========================================================
    // UTILIDADES
    // =========================================================

    private String toJsonStringArray(List<String> list) {
        if (list == null || list.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append("\"").append(
                    list.get(i) == null ? "" : list.get(i).replace("\"", "'")
            ).append("\"");
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String toJsonDoubleArray(List<Double> list) {
        if (list == null || list.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
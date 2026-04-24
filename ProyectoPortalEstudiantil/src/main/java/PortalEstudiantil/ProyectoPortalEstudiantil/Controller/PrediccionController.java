package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.PeriodoRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Security.PortalUserDetails;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.PrediccionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/prediccion")
public class PrediccionController {

    private final PrediccionService service;
    private final PeriodoRepository periodoRepo;

    public PrediccionController(PrediccionService service,
                                PeriodoRepository periodoRepo) {
        this.service    = service;
        this.periodoRepo = periodoRepo;
    }

    // ENCARGADO — análisis del hijo seleccionado

    @GetMapping("/encargado")
    @PreAuthorize("hasRole('ENCARGADO')")
    public String vistaEncargado(
            @RequestParam(required = false) Long idMatricula,
            @RequestParam(required = false) Long idPeriodo,
            @AuthenticationPrincipal PortalUserDetails usuario,
            Model model) {

        Long idEncargado = usuario.getIdUsuario();
        List<Map<String, Object>> hijos = service.hijosDeEncargado(idEncargado);
        model.addAttribute("hijos", hijos);

        if (!hijos.isEmpty()) {
            // Seleccionar el primer hijo por defecto si no se especificó
            if (idMatricula == null) {
                idMatricula = toLong(hijos.get(0).get("idMatricula"));
                idPeriodo   = toLong(hijos.get(0).get("idPeriodo"));
            }

            // Si hay matrícula pero no período, buscarlo en la lista de hijos
            if (idPeriodo == null) {
                final Long idMat = idMatricula;
                idPeriodo = hijos.stream()
                        .filter(h -> toLong(h.get("idMatricula")).equals(idMat))
                        .map(h -> toLong(h.get("idPeriodo")))
                        .findFirst().orElse(null);
            }

            // Hijo seleccionado para mostrar en el perfil — se resuelve en Java, no en SpEL
            final Long idMatFinal = idMatricula;
            Map<String, Object> hijoSel = hijos.stream()
                    .filter(h -> toLong(h.get("idMatricula")).equals(idMatFinal))
                    .findFirst()
                    .orElse(hijos.get(0));
            model.addAttribute("hijoSel", hijoSel);

            if (idMatricula != null && idPeriodo != null) {
                BigDecimal promedio   = service.promedioGeneral(idMatricula, idPeriodo);
                String     nivel      = service.calcularNivel(promedio);
                String     mensaje    = service.mensajeAlerta(nivel, promedio);
                Map<String,Object> asistencia = service.resumenAsistencia(idMatricula, idPeriodo);
                int pctAsistencia     = service.porcentajeAsistencia(asistencia);

                model.addAttribute("calificaciones",  service.calificacionesPorMateria(idMatricula, idPeriodo));
                model.addAttribute("promedio",        promedio);
                model.addAttribute("nivelRiesgo",     nivel);
                model.addAttribute("mensajeAlerta",   mensaje);
                model.addAttribute("asistencia",      asistencia);
                model.addAttribute("pctAsistencia",   pctAsistencia);
            }
        }

        model.addAttribute("idMatriculaSel", idMatricula);
        model.addAttribute("idPeriodoSel",   idPeriodo);
        model.addAttribute("pageTitle",      "Análisis y Rendimiento");
        return "prediccion/VistaEncargado";
    }

    // DOCENTE — predicción de sus estudiantes

    @GetMapping("/docente")
    @PreAuthorize("hasRole('PROFESOR')")
    public String vistaDocente(
            @RequestParam(required = false) Long idPeriodo,
            @AuthenticationPrincipal PortalUserDetails usuario,
            Model model) {

        Long idDocente = usuario.getIdUsuario();

        if (idPeriodo == null) {
            var actual = periodoRepo.obtenerPeriodoActualEnCurso();
            if (actual != null) idPeriodo = actual.getIdPeriodo();
        }

        List<Map<String, Object>> estudiantes = idPeriodo != null
                ? service.prediccionDocente(idDocente, idPeriodo)
                : List.of();

        model.addAttribute("estudiantes",  estudiantes);
        model.addAttribute("periodos",     periodoRepo.listarResumen());
        model.addAttribute("idPeriodoSel", idPeriodo);
        model.addAttribute("totalAlto",    contarNivel(estudiantes, "ALTO"));
        model.addAttribute("totalMedio",   contarNivel(estudiantes, "MEDIO"));
        model.addAttribute("totalBajo",    contarNivel(estudiantes, "BAJO"));
        model.addAttribute("pageTitle",    "Análisis Predictivo");
        return "prediccion/VistaDocente";
    }

    // ADMIN — estadísticas globales

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String vistaAdmin(
            @RequestParam(required = false) Long idPeriodo,
            Model model) {

        if (idPeriodo == null) {
            var actual = periodoRepo.obtenerPeriodoActualEnCurso();
            if (actual != null) idPeriodo = actual.getIdPeriodo();
        }

        List<Map<String, Object>> estadisticas = idPeriodo != null
                ? service.estadisticasPorPeriodo(idPeriodo)
                : List.of();

        List<Map<String, Object>> estudiantes = idPeriodo != null
                ? service.prediccionAdmin(idPeriodo)
                : List.of();

        long totalAlto  = estadisticas.stream().mapToLong(e -> toLong(e.get("riesgoAlto"))).sum();
        long totalMedio = estadisticas.stream().mapToLong(e -> toLong(e.get("riesgoMedio"))).sum();
        long totalBajo  = estadisticas.stream().mapToLong(e -> toLong(e.get("riesgoBajo"))).sum();
        long totalSD    = estadisticas.stream().mapToLong(e -> toLong(e.get("sinCalificaciones"))).sum();

        model.addAttribute("estadisticas",  estadisticas);
        model.addAttribute("estudiantes",   estudiantes);
        model.addAttribute("periodos",      periodoRepo.listarResumen());
        model.addAttribute("idPeriodoSel",  idPeriodo);
        model.addAttribute("totalAlto",     totalAlto);
        model.addAttribute("totalMedio",    totalMedio);
        model.addAttribute("totalBajo",     totalBajo);
        model.addAttribute("totalSinDatos", totalSD);
        model.addAttribute("pageTitle",     "Análisis Predictivo");
        return "prediccion/VistaAdmin";
    }

    // PRIVADO

    private long contarNivel(List<Map<String, Object>> lista, String nivel) {
        return lista.stream().filter(e -> nivel.equals(e.get("nivelRiesgo"))).count();
    }

    private Long toLong(Object val) {
        if (val == null) return 0L;
        return ((Number) val).longValue();
    }
}
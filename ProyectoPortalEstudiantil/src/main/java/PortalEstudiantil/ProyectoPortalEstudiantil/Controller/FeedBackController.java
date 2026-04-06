package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.FeedBackRequest;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.FeedBackResumen;
import PortalEstudiantil.ProyectoPortalEstudiantil.Security.PortalUserDetails;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.FeedBackPDFService;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.FeedBackService;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.MateriaService;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.PeriodoService;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.SeccionMateriaService;
import com.lowagie.text.DocumentException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/feedback")
public class FeedBackController {

    @Autowired
    private FeedBackService feedBackService;
    @Autowired
    private FeedBackPDFService feedBackPDFService;
    @Autowired
    private SeccionMateriaService seccionMateriaService;
    @Autowired
    private MateriaService materiaService;
    @Autowired
    private PeriodoService periodoService;

    private static final DateTimeFormatter FMT_ARCHIVO
            = DateTimeFormatter.ofPattern("yyyyMMdd");

    @GetMapping("/feedBack360")
    public String vista(
            @RequestParam(required = false) Integer idSeccionmateria,
            @RequestParam(required = false) Integer idMateria,
            @RequestParam(required = false) Integer periodo1,
            @RequestParam(required = false) Integer periodo2,
            @AuthenticationPrincipal PortalUserDetails userDetails,
            Model model) {

        model.addAttribute("pageTitle", "Feedback 360°");

        String rol = userDetails.getRole();

        switch (rol) {
            case "PROFESOR" ->
                cargarDocente(userDetails, idSeccionmateria, model);
            case "ESTUDIANTE" ->
                cargarEstudiante(userDetails, idMateria, model);
            case "ENCARGADO" ->
                cargarEncargado(userDetails, idMateria, model);
            case "ADMINISTRADOR" ->
                cargarAdmin(periodo1, periodo2, model);
            default -> {
            }
        }

        model.addAttribute("rolActual", rol);

        return "feedback/feedBack360";
    }

    //PROFESOR
    private void cargarDocente(PortalUserDetails userDetails,
            Integer idSeccionmateria,
            Model model) {

        Long idDocente = userDetails.getIdUsuario();

        model.addAttribute("seccionMaterias",
                seccionMateriaService.listarResumenPorDocente(idDocente));

        model.addAttribute("idSeccionmateriaSeleccionada", idSeccionmateria);
        model.addAttribute("historial",
                feedBackService.listarPorDocente(idDocente));

        if (idSeccionmateria != null) {
            model.addAttribute("estudiantes",
                    feedBackService.listarEstudiantesConFeedback(idSeccionmateria));
        }
    }

    //ESTUDIANTE
    private void cargarEstudiante(PortalUserDetails userDetails,
            Integer idMateria,
            Model model) {

        Long idEstudiante = userDetails.getIdUsuario();

        List<FeedBackResumen> feedbacks
                = feedBackService.listarPorEstudiante(idEstudiante, idMateria);

        double promedio = calcularPromedio(feedbacks);

        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("idMateriaFiltro", idMateria);
        model.addAttribute("promedioGeneral", String.format("%.1f", promedio));
        model.addAttribute("totalEvaluaciones", feedbacks.size());

        model.addAttribute("materias", materiaService.listarActivas());
    }

    //ENCARGADO
    private void cargarEncargado(PortalUserDetails userDetails,
            Integer idMateria,
            Model model) {

        Long idEncargado = userDetails.getIdUsuario();

        List<FeedBackResumen> feedbacks
                = feedBackService.listarPorEncargado(idEncargado, idMateria);

        double promedio = calcularPromedio(feedbacks);

        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("idMateriaFiltro", idMateria);
        model.addAttribute("promedioGeneral", String.format("%.1f", promedio));
        model.addAttribute("totalEvaluaciones", feedbacks.size());

        model.addAttribute("materias", materiaService.listarActivas());
    }

    private void cargarAdmin(Integer periodo1, Integer periodo2, Model model) {

        List<FeedBackResumen> todos = feedBackService.listarTodos();
        List<FeedBackResumen> alertas = feedBackService.listarAlertas();
        double promGeneral = calcularPromedio(todos);

        model.addAttribute("todosFeedbacks", todos);
        model.addAttribute("totalEvaluaciones", todos.size());
        model.addAttribute("promedioGeneral", String.format("%.1f", promGeneral));
        model.addAttribute("promediosPorSeccion", feedBackService.promediosPorSeccion());
        model.addAttribute("promediosPorDocente", feedBackService.promediosPorDocente());
        model.addAttribute("alertas", alertas);
        model.addAttribute("totalAlertas", alertas.size());

        if (periodo1 != null && periodo2 != null && !periodo1.equals(periodo2)) {
            try {
                model.addAttribute("comparativa",
                        feedBackService.compararPeriodos(periodo1, periodo2));
                model.addAttribute("periodo1", periodo1);
                model.addAttribute("periodo2", periodo2);
            } catch (IllegalArgumentException e) {
                model.addAttribute("errorComparativa", e.getMessage());
            }
        }

        model.addAttribute("periodos", periodoService.listarResumen());
    }

    // POST /feedBack/registrar
    @PostMapping("/registrar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PROFESOR')")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registrar(
            @RequestBody FeedBackRequest request) {

        Map<String, Object> resp = new HashMap<>();
        try {
            feedBackService.registrarBatch(request);
            resp.put("ok", true);
            resp.put("mensaje", "Evaluaciones guardadas correctamente.");
            return ResponseEntity.ok(resp);

        } catch (IllegalArgumentException | IllegalStateException e) {
            resp.put("ok", false);
            resp.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(resp);

        } catch (Exception e) {
            resp.put("ok", false);
            resp.put("mensaje", "Error interno al guardar las evaluaciones.");
            return ResponseEntity.internalServerError().body(resp);
        }
    }

    // GET /feedBack/pdf
    @GetMapping("/pdf")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','ENCARGADO','ESTUDIANTE')")
    public ResponseEntity<byte[]> descargarPdf(
            @RequestParam(required = false) Integer idMateria,
            @AuthenticationPrincipal PortalUserDetails userDetails) {

        String rol = userDetails.getRole();
        Long idUsuario = userDetails.getIdUsuario();
        boolean esEncargado = "ENCARGADO".equals(rol);

        List<FeedBackResumen> feedbacks = esEncargado
                ? feedBackService.listarPorEncargado(idUsuario, idMateria)
                : feedBackService.listarPorEstudiante(idUsuario, idMateria);

        String nombreEstudiante = feedbacks.isEmpty()
                ? "Estudiante"
                : feedbacks.get(0).getNombreEstudiante();

        try {
            byte[] pdf = feedBackPDFService.generarReporteEstudiante(
                    feedbacks,
                    nombreEstudiante,
                    userDetails.getUsername(),
                    esEncargado);

            String archivo = String.format("Feedback360_%s_%s.pdf",
                    nombreEstudiante.replace(" ", "_"),
                    LocalDate.now().format(FMT_ARCHIVO));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + archivo + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);

        } catch (DocumentException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/pdf-admin")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<byte[]> descargarPdfAdmin(
            @AuthenticationPrincipal PortalUserDetails userDetails) {

        List<FeedBackResumen> feedbacks = feedBackService.listarTodos();
        List<FeedBackResumen> alertas = feedBackService.listarAlertas();

        try {
            byte[] pdf = feedBackPDFService.generarReporteAdmin(
                    feedbacks,
                    feedBackService.promediosPorSeccion(),
                    feedBackService.promediosPorDocente(),
                    alertas,
                    userDetails.getUsername());

            String archivo = "Feedback360_Admin_"
                    + LocalDate.now().format(FMT_ARCHIVO) + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + archivo + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);

        } catch (DocumentException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/alertas")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> alertasJson() {
        List<FeedBackResumen> alertas = feedBackService.listarAlertas();
        Map<String, Object> resp = new HashMap<>();
        resp.put("total", alertas.size());
        resp.put("alertas", alertas);
        return ResponseEntity.ok(resp);
    }

    // UTILIDAD
    private double calcularPromedio(List<FeedBackResumen> feedbacks) {
        return feedbacks.stream()
                .filter(f -> f.getCalificacion() != null)
                .mapToInt(FeedBackResumen::getCalificacion)
                .average()
                .orElse(0.0);
    }
}

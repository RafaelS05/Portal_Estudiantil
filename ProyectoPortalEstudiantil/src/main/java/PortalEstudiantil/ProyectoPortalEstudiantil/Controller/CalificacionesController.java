/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Calificaciones;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.EvaluacionRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.MatriculaRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.CalificacionesService;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author marjo
 */
@Controller
@RequestMapping("/calificaciones")
public class CalificacionesController {

    private final CalificacionesService calificacionesService;
    private final MatriculaRepository matriculaRepository;
    private final EvaluacionRepository evaluacionRepository;

    public CalificacionesController(CalificacionesService calificacionesService,
            MatriculaRepository matriculaRepository,
            EvaluacionRepository evaluacionRepository) {
        this.calificacionesService = calificacionesService;
        this.matriculaRepository = matriculaRepository;
        this.evaluacionRepository = evaluacionRepository;
    }

    @GetMapping("")
    public String listado() {

        Long seccionDefault = 1L;

        return "redirect:/calificaciones/paginado?seccion="
                + seccionDefault + "&pagina=1";
    }

    @GetMapping("/paginado")
    public String listadoPaginado(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Long seccion,
            @RequestParam(defaultValue = "1") int pagina,
            Model model) {

        int tamanoPagina = 10;

        Map<String, Object> resultado = calificacionesService.listarCalificacionesParaVistaPaginado(
                busqueda, seccion, pagina, tamanoPagina);

        model.addAttribute("calificaciones", resultado.get("calificaciones"));
        model.addAttribute("paginaActual", resultado.get("paginaActual"));
        model.addAttribute("totalPaginas", resultado.get("totalPaginas"));
        model.addAttribute("totalElementos", resultado.get("totalElementos"));
        model.addAttribute("tamanoPagina", resultado.get("tamanoPagina"));

        model.addAttribute("secciones", calificacionesService.obtenerTodasLasSecciones());
        model.addAttribute("busquedaActual", busqueda);
        model.addAttribute("seccionActual", seccion);
        model.addAttribute("pageTitle", "Calificaciones");

        return "calificaciones/listado";
    }

    @GetMapping("/nuevo")
    public String nuevaCalificacion(Model model) {
        model.addAttribute("calificacion", new Calificaciones());
        model.addAttribute("matriculas", matriculaRepository.findAll());
        model.addAttribute("evaluaciones", evaluacionRepository.findAll());
        model.addAttribute("pageTitle", "Nueva Calificación");

        return "calificaciones/modificar";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("calificacion") Calificaciones calificacion) {
        calificacionesService.guardarCalificaciones(calificacion);
        return "redirect:/calificaciones";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Long id) {
        calificacionesService.eliminar(id);
        return "redirect:/calificaciones";
    }

}

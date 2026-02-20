/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Calificaciones;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.EvaluacionRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.MatriculaRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.CalificacionesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String listado(Model model) {
        var lista = calificacionesService.listarCalificacionesParaVista();
        model.addAttribute("calificaciones", lista);
        return "calificaciones/listado";
    }

    @GetMapping("/nuevo")
    public String nuevaCalificacion(Model model) {
        model.addAttribute("calificacion", new Calificaciones());
        model.addAttribute("matriculas", matriculaRepository.findAll());
        model.addAttribute("evaluaciones", evaluacionRepository.findAll());

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

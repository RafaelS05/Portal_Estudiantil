/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Calificaciones;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.CalificacionesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    public CalificacionesController(CalificacionesService calificacionesService) {
        this.calificacionesService = calificacionesService;
    }

    @GetMapping("")
    public String listado(Model model) {
        var lista = calificacionesService.listarCalificacionesParaVista();
        model.addAttribute("calificaciones", lista);
        return "/calificaciones/listado";
    }

    @GetMapping("/nuevo")
    public String nuevaCalificacion(Model model) {
        model.addAttribute("calificacion", new Calificaciones());
        return "calificaciones/modificar";
    }

    @PostMapping("/guardar")
    public String guardar(Calificaciones calificacion) {
        calificacionesService.guardarCalificaciones(calificacion);
        return "redirect:/calificaciones/listado";
    }

    @GetMapping("/eliminar/{idCalificaciones}")
    public String eliminar(@PathVariable("idCalificaciones") Long idCalificaciones) {
        calificacionesService.eliminar(idCalificaciones);
        return "redirect:/calificaciones/listado";
    }
}

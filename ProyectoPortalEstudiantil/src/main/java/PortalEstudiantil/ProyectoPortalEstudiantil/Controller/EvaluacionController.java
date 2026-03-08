/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

/**
 *
 * @author marjo
 */

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Evaluacion;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.SeccionRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.EvaluacionService;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/evaluaciones")
public class EvaluacionController {

    private final EvaluacionService evaluacionService;
    private final SeccionRepository seccionRepository;

    public EvaluacionController(EvaluacionService evaluacionService,
                                SeccionRepository seccionRepository) {
        this.evaluacionService = evaluacionService;
        this.seccionRepository = seccionRepository;
    }
    
    @GetMapping("")
    public String listado(Model model) {
        var lista = evaluacionService.listarTodas();
        model.addAttribute("evaluaciones", lista);
        return "evaluaciones/listado";
    }

    @GetMapping("/paginado")
    public String listadoPaginado(
            @RequestParam(required = false) String busqueda,
            @RequestParam(defaultValue = "1") int pagina,
            Model model) {

        int tamanoPagina = 10;

        Map<String, Object> resultado = Map.of(
                "evaluaciones", evaluacionService.listarEvaluacionesPaginado(busqueda, pagina, tamanoPagina).getContent(),
                "paginaActual", pagina,
                "totalPaginas", evaluacionService.listarEvaluacionesPaginado(busqueda, pagina, tamanoPagina).getTotalPages(),
                "totalElementos", evaluacionService.listarEvaluacionesPaginado(busqueda, pagina, tamanoPagina).getTotalElements(),
                "tamanoPagina", tamanoPagina
        );

        model.addAttribute("evaluaciones", resultado.get("evaluaciones"));
        model.addAttribute("paginaActual", resultado.get("paginaActual"));
        model.addAttribute("totalPaginas", resultado.get("totalPaginas"));
        model.addAttribute("totalElementos", resultado.get("totalElementos"));
        model.addAttribute("tamanoPagina", resultado.get("tamanoPagina"));

        model.addAttribute("busquedaActual", busqueda);
        model.addAttribute("secciones", seccionRepository.findAll());

        return "evaluaciones/listado-paginado";
    }

    @GetMapping("/nuevo")
    public String nuevaEvaluacion(Model model) {
        model.addAttribute("evaluacion", new Evaluacion());
        model.addAttribute("secciones", seccionRepository.findAll());
        return "evaluaciones/modificar";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("evaluacion") Evaluacion evaluacion) {
        evaluacionService.crearEvaluacion(evaluacion);
        return "redirect:/evaluaciones";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable("id") Long id,
                             @ModelAttribute("evaluacion") Evaluacion evaluacion) {
        evaluacionService.actualizarEvaluacion(id, evaluacion);
        return "redirect:/evaluaciones";
    }

    @GetMapping("/estado/{id}")
    public String cambiarEstado(@PathVariable("id") Long id,
                                @RequestParam("estado") Long estado) {
        evaluacionService.cambiarEstado(id, estado);
        return "redirect:/evaluaciones";
    }
}

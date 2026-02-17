package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Aula;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.AulaService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/gestionAcademica/aula")
public class AulaController {

    @Autowired
    private AulaService aulaService;

    @GetMapping
    public String listaAula(Model model) {
        List<Aula> aulas = aulaService.listarTodas();
        model.addAttribute("aulas", aulas);
        model.addAttribute("totalActivas", aulaService.contarActivas());
        model.addAttribute("paginaActual", "gestion-academica");
        return "aulas/lista";
    }

    @GetMapping("/nuevo")
    public String nuevaAula(Model model) {
        model.addAttribute("aula", new Aula());
        model.addAttribute("modoEdicion", false);
        model.addAttribute("paginaActual", "gestion-academica");
        return "aulas/formulario";
    }

    @PostMapping("/guardar")
    public String guardarAula(@RequestParam String numero,
            RedirectAttributes flash) {
        try {
            aulaService.insertar(numero.trim().toUpperCase());
            flash.addFlashAttribute("mensajeExito", "Aula creada correctamente.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
            return "redirect:/gestion-academica/aulas/nuevo";
        }
        return "redirect:/gestion-academica/aulas";
    }

    @GetMapping("/editar/{id}")
    public String editarAula(@PathVariable Long id, Model model,
            RedirectAttributes flash) {
        Aula aula = aulaService.buscarPorId(id);
        if (aula == null) {
            flash.addFlashAttribute("mensajeError", "Aula no encontrada.");
            return "redirect:/gestion-academica/aulas";
        }
        model.addAttribute("aula", aula);
        model.addAttribute("modoEdicion", true);
        model.addAttribute("paginaActual", "gestion-academica");
        return "aulas/formulario";
    }

    @PostMapping("/actualizar")
    public String actualizarAula (@RequestParam Long idAula,
            @RequestParam String numero,
            RedirectAttributes flash) {
        try {
            aulaService.modificar(idAula, numero.trim().toUpperCase());
            flash.addFlashAttribute("mensajeExito", "Aula actualizada correctamente.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
            return "redirect:/gestion-academica/aulas/editar/" + idAula;
        }
        return "redirect:/gestion-academica/aulas";
    }

    @PostMapping("/cambiar-estado")
    public String cambiarEstadoAula(@RequestParam Long idAula,
            @RequestParam Long idEstado,
            RedirectAttributes flash) {
        aulaService.cambiarEstado(idAula, idEstado);
        String msg = idEstado == 1L ? "Aula activada." : "Aula desactivada.";
        flash.addFlashAttribute("mensajeExito", msg);
        return "redirect:/gestion-academica/aulas";
    }

}

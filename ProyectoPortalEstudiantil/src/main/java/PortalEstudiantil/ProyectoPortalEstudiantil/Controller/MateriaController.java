package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Materia;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.MateriaService;
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
@RequestMapping("/gestionAcademica/materia")
public class MateriaController {

    @Autowired
    private MateriaService materiaService;

    @GetMapping
    public String listaMateria(@RequestParam(required = false) String busqueda, Model model) {
        List<Materia> materias = materiaService.buscar(busqueda);
        model.addAttribute("materias", materias);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("totalActivas", materiaService.contarActivas());
        model.addAttribute("paginaActual", "gestion-academica");
        return "materias/lista";
    }

    @GetMapping("/nuevo")
    public String nuevaMateria(Model model) {
        model.addAttribute("materia", new Materia());
        model.addAttribute("modoEdicion", false);
        model.addAttribute("paginaActual", "gestion-academica");
        return "materias/formulario";
    }

    @PostMapping("/guardar")
    public String guardarMateria(@RequestParam String nombre,
            @RequestParam(required = false) String codigo,
            RedirectAttributes flash) {
        try {
            materiaService.insertar(nombre, codigo);
            flash.addFlashAttribute("mensajeExito", "Materia creada correctamente.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
            return "redirect:/gestion-academica/materias/nuevo";
        }
        return "redirect:/gestion-academica/materias";
    }

    @GetMapping("/editar/{id}")
    public String editarMateria(@PathVariable Long id, Model model,
            RedirectAttributes flash) {
        Materia materia = materiaService.buscarPorId(id);
        if (materia == null) {
            flash.addFlashAttribute("mensajeError", "Materia no encontrada.");
            return "redirect:/gestion-academica/materias";
        }
        model.addAttribute("materia", materia);
        model.addAttribute("modoEdicion", true);
        model.addAttribute("paginaActual", "gestion-academica");
        return "materias/formulario";
    }

    @PostMapping("/actualizar")
    public String actualizarMateria (@RequestParam Long idMateria,
            @RequestParam String nombre,
            @RequestParam(required = false) String codigo,
            RedirectAttributes flash) {
        try {
            materiaService.modificar(idMateria, nombre, codigo);
            flash.addFlashAttribute("mensajeExito", "Materia actualizada correctamente.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
            return "redirect:/gestion-academica/materias/editar/" + idMateria;
        }
        return "redirect:/gestion-academica/materias";
    }

    @PostMapping("/cambiar-estado")
    public String cambiarEstadoMateria(@RequestParam Long idMateria,
            @RequestParam Long idEstado,
            RedirectAttributes flash) {
        materiaService.cambiarEstado(idMateria, idEstado);
        String msg = idEstado == 1L ? "Materia activada." : "Materia desactivada.";
        flash.addFlashAttribute("mensajeExito", msg);
        return "redirect:/gestion-academica/materias";
    }
}

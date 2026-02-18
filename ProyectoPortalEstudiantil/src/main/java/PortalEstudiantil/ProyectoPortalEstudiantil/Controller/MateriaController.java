package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Service.MateriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/gestionAcademica/materia")
public class MateriaController {

    @Autowired
    private MateriaService materiaService;

    @PostMapping("/guardar")
    public String guardar(
            @RequestParam String nombre,
            @RequestParam(required = false) String codigo,
            RedirectAttributes flash) {
        try {
            materiaService.insertar(nombre, codigo);
            flash.addFlashAttribute("mensajeExito", "Materia creada correctamente.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/gestionAcademica?tab=materias";
    }
    @PostMapping("/actualizar")
    public String actualizar(
            @RequestParam Long idMateria,
            @RequestParam String nombre,
            @RequestParam(required = false) String codigo,
            RedirectAttributes flash) {
        try {
            materiaService.modificar(idMateria, nombre, codigo);
            flash.addFlashAttribute("mensajeExito", "Materia actualizada correctamente.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/gestionAcademica?tab=materias";
    }

    @PostMapping("/cambiar-estado")
    public String cambiarEstado(
            @RequestParam Long idMateria,
            @RequestParam Long idEstado,
            RedirectAttributes flash) {
        materiaService.cambiarEstado(idMateria, idEstado);
        flash.addFlashAttribute("mensajeExito",
                idEstado == 1L ? "Materia activada." : "Materia desactivada.");
        return "redirect:/gestionAcademica?tab=materias";
    }
}

package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Service.SeccionMateriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/gestionAcademica/seccionMateria")
public class SeccionMateriaController {

    @Autowired
    private SeccionMateriaService seccionMateriaService;

    @PostMapping("/guardar")
    public String guardar(@RequestParam Long idSeccionFk,
            @RequestParam Long idMateriaFk,
            @RequestParam Long idUsuarioDocenteFk,
            RedirectAttributes flash) {
        try {
            seccionMateriaService.insertar(idSeccionFk, idMateriaFk, idUsuarioDocenteFk);
            flash.addFlashAttribute("mensajeExito", "Materia asignada a la sección correctamente.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/gestionAcademica?tab=seccionMateria";
    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam Long idSeccionMateria,
            @RequestParam Long idSeccionFk,
            @RequestParam Long idMateriaFk,
            @RequestParam Long idUsuarioDocenteFk,
            RedirectAttributes flash) {
        try {
            seccionMateriaService.modificar(idSeccionMateria, idSeccionFk,
                    idMateriaFk, idUsuarioDocenteFk);
            flash.addFlashAttribute("mensajeExito", "Asignación actualizada correctamente.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/gestionAcademica?tab=seccionMateria";

    }

    @PostMapping("/cambiar-estado")
    public String cambiarEstado(@RequestParam Long idSeccionMateria,
            @RequestParam Long idEstado,
            RedirectAttributes flash) {
        seccionMateriaService.cambiarEstado(idSeccionMateria, idEstado);
        String msg = idEstado == 1L ? "Asignación activada." : "Asignación desactivada.";
        flash.addFlashAttribute("mensajeExito", msg);
        return "redirect:/gestionAcademica?tab=seccionMateria";
    }
}

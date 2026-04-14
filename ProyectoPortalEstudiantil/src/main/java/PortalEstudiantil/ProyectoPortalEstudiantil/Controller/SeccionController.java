package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Seccion;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.MatriculaService;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.SeccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/gestionAcademica/seccion")
public class SeccionController {

    @Autowired
    private SeccionService seccionService;
    @Autowired
    private MatriculaService matriculaService;

    @PostMapping("/guardar")
    public String guardar(@RequestParam String numero,
            @RequestParam Long idPeriodoFk,
            RedirectAttributes flash) {
        try {
            seccionService.insertar(numero, idPeriodoFk);
            flash.addFlashAttribute("mensajeExito", "Sección creada correctamente.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/gestionAcademica?tab=secciones";

    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam Long idSeccion,
            @RequestParam String numero,
            @RequestParam Long idPeriodoFk,
            RedirectAttributes flash) {
        try {
            seccionService.modificar(idSeccion, numero, idPeriodoFk);
            flash.addFlashAttribute("mensajeExito", "Sección actualizada correctamente.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/gestionAcademica?tab=secciones";
    }

    @PostMapping("/cambiar-estado")
    public String cambiar(@RequestParam Long idSeccion,
            @RequestParam Long idEstado,
            RedirectAttributes flash) {
        seccionService.cambiarEstado(idSeccion, idEstado);
        String msg = idEstado == 1L ? "Sección activada." : "Sección desactivada.";
        flash.addFlashAttribute("mensajeExito", msg);
        return "redirect:/gestionAcademica?tab=secciones";
    }

    @PostMapping("/desmatricular")
    public String desmatricular(@RequestParam Long idMatricula,
            @RequestParam Long idSeccion,
            RedirectAttributes flash) {
        try {
            matriculaService.desmatricular(idMatricula);
            flash.addFlashAttribute("mensajeExito", "Estudiante desmatriculado correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/gestionAcademica?tab=secciones";
    }

}

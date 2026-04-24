package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Service.AulaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/gestionAcademica/aula")
public class AulaController {

    @Autowired
    private AulaService aulaService;

    @PostMapping("/guardar")
    public String guardar(@RequestParam String numero,
            RedirectAttributes flash) {
        try {
            aulaService.insertar(numero.trim().toUpperCase());
            flash.addFlashAttribute("mensajeExito", "Aula creada correctamente.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/gestionAcademica?tab=aulas";
    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam Long idAula,
            @RequestParam String numero,
            RedirectAttributes flash) {
        try {
            aulaService.modificar(idAula, numero.trim().toUpperCase());
            flash.addFlashAttribute("mensajeExito", "Aula actualizada correctamente.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/gestionAcademica?tab=aulas";
    }

    @PostMapping("/cambiar-estado")
    public String cambiarEstado(@RequestParam Long idAula,
            @RequestParam Long idEstado,
            RedirectAttributes flash) {
        aulaService.cambiarEstado(idAula, idEstado);
        String msg = idEstado == 1L ? "Aula activada." : "Aula desactivada.";
        flash.addFlashAttribute("mensajeExito", msg);
        return "redirect:/gestionAcademica?tab=aulas";
    }
}

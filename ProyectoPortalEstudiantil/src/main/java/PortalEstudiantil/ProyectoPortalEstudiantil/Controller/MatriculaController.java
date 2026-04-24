package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Service.MatriculaService;
import java.time.LocalDate;
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
@RequestMapping("/gestionAcademica/matricula")
public class MatriculaController {

    @Autowired
    private MatriculaService matriculaService;

    @GetMapping("/seccion/{idSeccion}")
    public String verSeccion(@PathVariable Long idSeccion) {
        return "redirect:/gestionAcademica?tab=matriculas";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam Long idSeccion,
            @RequestParam Long idEstudiante,
            RedirectAttributes flash) {
        try {
            matriculaService.insertar(LocalDate.now(), idEstudiante, idSeccion);
            flash.addFlashAttribute("mensajeExito", "Estudiante matriculado correctamente.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/gestionAcademica?tab=matriculas";
    }

    @PostMapping("/cambiar-estado")
    public String cambiarEstado(@RequestParam Long idMatricula,
            @RequestParam Long idEstado,
            RedirectAttributes flash) {
        matriculaService.cambiarEstado(idMatricula, idEstado);
        String msg = idEstado == 1L ? "Matrícula activada." : "Matrícula desactivada.";
        flash.addFlashAttribute("mensajeExito", msg);
        return "redirect:/gestionAcademica?tab=matriculas";
    }
}

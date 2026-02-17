package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Materia;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Seccion;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.SeccionMateria;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.MateriaRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.SeccionMateriaRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.SeccionRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.SeccionMateriaService;
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
@RequestMapping("/gestionAcademica/SeccionMateria")
public class SeccionMateriaController {

    @Autowired
    private SeccionMateriaService seccionMateriaService;

    @Autowired
    private SeccionRepository seccionRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private SeccionMateriaRepository seccionMateriaRepository;

    @GetMapping
    public String listaSeccionMateria(@RequestParam(required = false) Long idSeccion, Model model) {
        List<SeccionMateria> asignaciones = idSeccion != null
                ? seccionMateriaService.listarPorSeccion(idSeccion)
                : seccionMateriaService.listarTodas();

        List<Seccion> secciones = seccionRepository.listarActivas();
        List<Materia> materias = materiaRepository.listarActivas();

        model.addAttribute("asignaciones", asignaciones);
        model.addAttribute("secciones", secciones);
        model.addAttribute("materias", materias);
        model.addAttribute("idSeccionSeleccionada", idSeccion);
        model.addAttribute("totalActivas", seccionMateriaService.contarActivas());
        model.addAttribute("paginaActual", "gestion-academica");
        model.addAttribute("tituloPagina", "Gestión Académica");
        return "seccion-materias/lista";
    }

    @GetMapping("/nuevo")
    public String nuevaSeccionMateria(@RequestParam(required = false) Long idSeccion, Model model) {
        List<Seccion> secciones = seccionRepository.listarActivas();
        List<Materia> materias = materiaRepository.listarActivas();

        model.addAttribute("asignacion", new SeccionMateria());
        model.addAttribute("secciones", secciones);
        model.addAttribute("materias", materias);
        model.addAttribute("idSeccionPreseleccionada", idSeccion);
        model.addAttribute("modoEdicion", false);
        model.addAttribute("paginaActual", "gestion-academica");
        model.addAttribute("tituloPagina", "Gestión Académica");
        return "seccion-materias/formulario";
    }

    @PostMapping("/guardar")
    public String guardarSeccionMateria(@RequestParam Long idSeccionFk,
            @RequestParam Long idMateriaFk,
            @RequestParam Long idUsuarioDocenteFk,
            RedirectAttributes flash) {
        try {
            seccionMateriaService.insertar(idSeccionFk, idMateriaFk, idUsuarioDocenteFk);
            flash.addFlashAttribute("mensajeExito", "Materia asignada a la sección correctamente.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
            return "redirect:/gestion-academica/seccion-materias/nuevo";
        }
        return "redirect:/gestion-academica/seccion-materias";
    }

    @GetMapping("/editar/{id}")
    public String editarSeccionMateria(@PathVariable Long id, Model model, RedirectAttributes flash) {
        SeccionMateria asignacion = seccionMateriaService.buscarPorId(id);
        if (asignacion == null) {
            flash.addFlashAttribute("mensajeError", "Asignación no encontrada.");
            return "redirect:/gestion-academica/seccion-materias";
        }

        List<Seccion> secciones = seccionRepository.listarActivas();
        List<Materia> materias = materiaRepository.listarActivas();

        model.addAttribute("asignacion", asignacion);
        model.addAttribute("secciones", secciones);
        model.addAttribute("materias", materias);
        model.addAttribute("modoEdicion", true);
        model.addAttribute("paginaActual", "gestion-academica");
        model.addAttribute("tituloPagina", "Gestión Académica");
        return "seccion-materias/formulario";
    }

    @PostMapping("/actualizar")
    public String actualizarSeccionMateria(@RequestParam Long idSeccionMateria,
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
            return "redirect:/gestion-academica/seccion-materias/editar/" + idSeccionMateria;
        }
        return "redirect:/gestion-academica/seccion-materias";
    }

    @PostMapping("/cambiar-estado")
    public String cambiarEstadoSeccionMateria(@RequestParam Long idSeccionMateria,
            @RequestParam Long idEstado,
            RedirectAttributes flash) {
        seccionMateriaService.cambiarEstado(idSeccionMateria, idEstado);
        String msg = idEstado == 1L ? "Asignación activada." : "Asignación desactivada.";
        flash.addFlashAttribute("mensajeExito", msg);
        return "redirect:/gestion-academica/seccion-materias";
    }
}

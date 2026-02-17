package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Periodo;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Seccion;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.PeriodoRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.SeccionRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.SeccionService;
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
@RequestMapping("/gestionAcademica/seccion")
public class SeccionController {

    @Autowired
    private SeccionService seccionService;

    private PeriodoRepository periodoRepository;

    @GetMapping
    public String lista(@RequestParam(required = false) Long idPeriodo, Model model) {
        List<SeccionRepository.SeccionRow> secciones = idPeriodo != null
                ? seccionService.listarPorPeriodo(idPeriodo)
                : seccionService.listarResumen();

        List<Periodo> periodos = periodoRepository.findAll();

        model.addAttribute("secciones", secciones);
        model.addAttribute("periodos", periodos);
        model.addAttribute("idPeriodoSeleccionado", idPeriodo);
        model.addAttribute("totalActivas", seccionService.contarActivas());
        model.addAttribute("paginaActual", "gestion-academica");
        return "secciones/lista";
    }

    @GetMapping("/nuevo")
    public String nuevaSeccion (Model model) {
        List<Periodo> periodos = periodoRepository.listarActivos();
        model.addAttribute("seccion", new Seccion());
        model.addAttribute("periodos", periodos);
        model.addAttribute("modoEdicion", false);
        model.addAttribute("paginaActual", "gestion-academica");
        return "secciones/formulario";
    }

    @PostMapping("/guardar")
    public String guardarSeccion (@RequestParam String numero,
            @RequestParam Long idPeriodoFk,
            RedirectAttributes flash) {
        try {
            seccionService.insertar(numero, idPeriodoFk);
            flash.addFlashAttribute("mensajeExito", "Sección creada correctamente.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
            return "redirect:/gestion-academica/secciones/nuevo";
        }
        return "redirect:/gestion-academica/secciones";
    }

    @GetMapping("/editar/{id}")
    public String editarSeccion (@PathVariable Long id, Model model, RedirectAttributes flash) {
        Seccion seccion = seccionService.buscarPorId(id);
        if (seccion == null) {
            flash.addFlashAttribute("mensajeError", "Sección no encontrada.");
            return "redirect:/gestion-academica/secciones";
        }
        List<Periodo> periodos = periodoRepository.listarActivos();
        model.addAttribute("seccion", seccion);
        model.addAttribute("periodos", periodos);
        model.addAttribute("modoEdicion", true);
        model.addAttribute("paginaActual", "gestion-academica");
        return "secciones/formulario";
    }

    @PostMapping("/actualizar")
    public String actualizarSeccion(@RequestParam Long idSeccion,
            @RequestParam String numero,
            @RequestParam Long idPeriodoFk,
            RedirectAttributes flash) {
        try {
            seccionService.modificar(idSeccion, numero, idPeriodoFk);
            flash.addFlashAttribute("mensajeExito", "Sección actualizada correctamente.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
            return "redirect:/gestion-academica/secciones/editar/" + idSeccion;
        }
        return "redirect:/gestion-academica/secciones";
    }

    @PostMapping("/cambiar-estado")
    public String cambiarEstadoSeccion (@RequestParam Long idSeccion,
            @RequestParam Long idEstado,
            RedirectAttributes flash) {
        seccionService.cambiarEstado(idSeccion, idEstado);
        String msg = idEstado == 1L ? "Sección activada." : "Sección desactivada.";
        flash.addFlashAttribute("mensajeExito", msg);
        return "redirect:/gestion-academica/secciones";
    }
}

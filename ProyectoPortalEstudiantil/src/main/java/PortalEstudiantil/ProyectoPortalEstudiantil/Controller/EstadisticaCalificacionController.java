package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.EstadisticaCalificacion;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.EstadisticaCalificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/estadisticas")
public class EstadisticaCalificacionController {


    @Autowired
    private EstadisticaCalificacionService estadisticaService;

    @GetMapping("/")
    public String listarEstadisticas(Model model) {
        List<EstadisticaCalificacion> estadisticas = estadisticaService.obtenerTodasEstadisticas();
        model.addAttribute("estadisticas", estadisticas);
        model.addAttribute("totalEstadisticas", estadisticaService.contarTotalEstadisticas());
        model.addAttribute("pageTitle", "Gestión de Estadísticas");
        return "estadisticas/dashboard"; // ✅ CORREGIDO: era "estadisticas/listado"
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("estadistica", new EstadisticaCalificacion());
        model.addAttribute("titulo", "Nueva Estadística");
        model.addAttribute("accion", "/estadisticas/guardar");
        model.addAttribute("pageTitle", "Nueva Estadística");
        return "estadisticas/formulario"; // ✅ CORRECTO
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        try {
            EstadisticaCalificacion estadistica = estadisticaService.obtenerEstadistica(id);
            model.addAttribute("estadistica", estadistica);
            model.addAttribute("titulo", "Editar Estadística");
            model.addAttribute("accion", "/estadisticas/actualizar/" + id);
            model.addAttribute("pageTitle", "Editar Estadística");
            return "estadisticas/formulario"; // ✅ CORRECTO
        } catch (Exception e) {
            return "redirect:/estadisticas/?error=Estadística no encontrada";
        }
    }

    @GetMapping("/ver/{id}")
    public String verEstadistica(@PathVariable Long id, Model model) {
        try {
            EstadisticaCalificacion estadistica = estadisticaService.obtenerEstadistica(id);
            model.addAttribute("estadistica", estadistica);
            model.addAttribute("pageTitle", "Detalle de Estadística");
            return "estadisticas/detalle_graficos"; // ✅ CORREGIDO: era "estadisticas/detalle"
        } catch (Exception e) {
            return "redirect:/estadisticas/?error=Estadística no encontrada";
        }
    }

    @PostMapping("/guardar")
    public String guardarEstadistica(
            @ModelAttribute EstadisticaCalificacion estadistica,
            RedirectAttributes redirectAttributes) {

        try {
            Long idNuevo = estadisticaService.crearEstadistica(estadistica);
            redirectAttributes.addFlashAttribute("mensaje",
                    "Estadística creada exitosamente con ID: " + idNuevo);
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/estadisticas/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje",
                    "Error al crear estadística: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/estadisticas/nuevo";
        }
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarEstadistica(
            @PathVariable Long id,
            @ModelAttribute EstadisticaCalificacion estadistica,
            RedirectAttributes redirectAttributes) {

        try {
            estadistica.setIdEstadistica(id);
            estadisticaService.actualizarEstadistica(estadistica);
            redirectAttributes.addFlashAttribute("mensaje",
                    "Estadística actualizada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/estadisticas/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje",
                    "Error al actualizar estadística: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/estadisticas/editar/" + id;
        }
    }

    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstadoEstadistica(
            @PathVariable Long id,
            @RequestParam Long idEstado,
            RedirectAttributes redirectAttributes) {

        try {
            estadisticaService.cambiarEstadoEstadistica(id, idEstado);
            String estado = idEstado == 1L ? "activada" : "desactivada";
            redirectAttributes.addFlashAttribute("mensaje",
                    "Estadística " + estado + " exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "info");
            return "redirect:/estadisticas/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje",
                    "Error al cambiar estado: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/estadisticas/";
        }
    }

    @PostMapping("/activar/{id}")
    public String activarEstadistica(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return cambiarEstadoEstadistica(id, 1L, redirectAttributes);
    }

    @PostMapping("/desactivar/{id}")
    public String desactivarEstadistica(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return cambiarEstadoEstadistica(id, 2L, redirectAttributes);
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarEstadistica(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            estadisticaService.eliminarEstadistica(id);
            redirectAttributes.addFlashAttribute("mensaje",
                    "Estadística eliminada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "warning");
            return "redirect:/estadisticas/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje",
                    "Error al eliminar estadística: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/estadisticas/";
        }
    }

    @GetMapping("/buscar")
    public String buscarEstadisticas(
            @RequestParam(required = false) Long materia,
            @RequestParam(required = false) Long seccion,
            @RequestParam(required = false) Long docente,
            Model model) {

        List<EstadisticaCalificacion> estadisticas;
        String criterioBusqueda = "";

        if (materia != null && seccion != null && docente != null) {
            EstadisticaCalificacion estadistica = estadisticaService.buscarPorMateriaSeccionDocente(materia, seccion, docente);
            estadisticas = estadistica != null ? List.of(estadistica) : List.of();
            criterioBusqueda = "Materia: " + materia + ", Sección: " + seccion + ", Docente: " + docente;
        } else if (materia != null && seccion != null) {
            estadisticas = estadisticaService.buscarPorMateriaYSeccion(materia, seccion);
            criterioBusqueda = "Materia: " + materia + ", Sección: " + seccion;
        } else if (materia != null && docente != null) {
            estadisticas = estadisticaService.buscarPorMateriaYDocente(materia, docente);
            criterioBusqueda = "Materia: " + materia + ", Docente: " + docente;
        } else if (seccion != null && docente != null) {
            estadisticas = estadisticaService.buscarPorSeccionYDocente(seccion, docente);
            criterioBusqueda = "Sección: " + seccion + ", Docente: " + docente;
        } else if (materia != null) {
            estadisticas = estadisticaService.buscarPorMateria(materia);
            criterioBusqueda = "Materia: " + materia;
        } else if (seccion != null) {
            estadisticas = estadisticaService.buscarPorSeccion(seccion);
            criterioBusqueda = "Sección: " + seccion;
        } else if (docente != null) {
            estadisticas = estadisticaService.buscarPorDocente(docente);
            criterioBusqueda = "Docente: " + docente;
        } else {
            estadisticas = estadisticaService.obtenerTodasEstadisticas();
        }

        model.addAttribute("estadisticas", estadisticas);
        model.addAttribute("criterioBusqueda", criterioBusqueda);
        model.addAttribute("totalResultados", estadisticas.size());
        model.addAttribute("pageTitle", "Búsqueda de Estadísticas");
        return "estadisticas/dashboard"; // ✅ CORREGIDO: era "estadisticas/listado"
    }

    @GetMapping("/activas")
    public String listarActivas(Model model) {
        List<EstadisticaCalificacion> estadisticas = estadisticaService.obtenerEstadisticasActivas();
        model.addAttribute("estadisticas", estadisticas);
        model.addAttribute("criterioBusqueda", "Estadísticas Activas");
        model.addAttribute("totalResultados", estadisticas.size());
        model.addAttribute("pageTitle", "Estadísticas Activas");
        return "estadisticas/dashboard"; // ✅ CORREGIDO: era "estadisticas/listado"
    }

    @GetMapping("/inactivas")
    public String listarInactivas(Model model) {
        List<EstadisticaCalificacion> estadisticas = estadisticaService.obtenerEstadisticasInactivas();
        model.addAttribute("estadisticas", estadisticas);
        model.addAttribute("criterioBusqueda", "Estadísticas Inactivas");
        model.addAttribute("totalResultados", estadisticas.size());
        model.addAttribute("pageTitle", "Estadísticas Inactivas");
        return "estadisticas/dashboard"; // ✅ CORREGIDO: era "estadisticas/listado"
    }

    @GetMapping("/materia/{id}")
    public String estadisticasPorMateria(@PathVariable Long id, Model model) {
        List<EstadisticaCalificacion> estadisticas = estadisticaService.buscarPorMateria(id);
        Double promedioGeneral = estadisticaService.calcularPromedioGeneral(id);
        Double porcentajeAprobacionGeneral = estadisticaService.calcularPorcentajeAprobacionGeneral(id);
        
        model.addAttribute("estadisticas", estadisticas);
        model.addAttribute("promedioGeneral", promedioGeneral);
        model.addAttribute("porcentajeAprobacionGeneral", porcentajeAprobacionGeneral);
        model.addAttribute("criterioBusqueda", "Materia ID: " + id);
        model.addAttribute("totalResultados", estadisticas.size());
        model.addAttribute("pageTitle", "Estadísticas por Materia");
        return "estadisticas/dashboard"; // ✅ CORREGIDO: era "estadisticas/listado"
    }

    @GetMapping("/seccion/{id}")
    public String estadisticasPorSeccion(@PathVariable Long id, Model model) {
        List<EstadisticaCalificacion> estadisticas = estadisticaService.buscarPorSeccion(id);
        model.addAttribute("estadisticas", estadisticas);
        model.addAttribute("criterioBusqueda", "Sección ID: " + id);
        model.addAttribute("totalResultados", estadisticas.size());
        model.addAttribute("pageTitle", "Estadísticas por Sección");
        return "estadisticas/dashboard"; // ✅ CORREGIDO: era "estadisticas/listado"
    }

    @GetMapping("/docente/{id}")
    public String estadisticasPorDocente(@PathVariable Long id, Model model) {
        List<EstadisticaCalificacion> estadisticas = estadisticaService.buscarPorDocente(id);
        model.addAttribute("estadisticas", estadisticas);
        model.addAttribute("criterioBusqueda", "Docente ID: " + id);
        model.addAttribute("totalResultados", estadisticas.size());
        model.addAttribute("pageTitle", "Estadísticas por Docente");
        return "estadisticas/dashboard"; // ✅ CORREGIDO: era "estadisticas/listado"
    }
}
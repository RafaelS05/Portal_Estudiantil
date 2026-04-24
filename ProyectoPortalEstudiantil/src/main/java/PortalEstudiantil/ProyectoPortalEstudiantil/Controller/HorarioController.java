package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Service.HorarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/gestionAcademica/horario")
public class HorarioController {

    @Autowired
    private HorarioService horarioService;

    @PostMapping("/guardar")
    public String guardar(
            @RequestParam Integer diaSemana,
            @RequestParam String horaInicio,
            @RequestParam String horaFin,
            @RequestParam(required = false) Long idAulaFk,
            @RequestParam Long idSeccionMateriaFk,
            RedirectAttributes flash) {
        try {
            horarioService.insertar(diaSemana, horaInicio, horaFin,
                    idAulaFk, idSeccionMateriaFk);
            flash.addFlashAttribute("mensajeExito", "Horario creado correctamente.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/gestionAcademica?tab=horarios";
    }

    @PostMapping("/actualizar")
    public String actualizar(
            @RequestParam Long idHorario,
            @RequestParam Integer diaSemana,
            @RequestParam String horaInicio,
            @RequestParam String horaFin,
            @RequestParam(required = false) Long idAulaFk,
            @RequestParam Long idSeccionMateriaFk,
            RedirectAttributes flash) {
        try {
            horarioService.modificar(idHorario, diaSemana, horaInicio, horaFin,
                    idAulaFk, idSeccionMateriaFk);
            flash.addFlashAttribute("mensajeExito", "Horario actualizado correctamente.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/gestionAcademica?tab=horarios";
    }

    @PostMapping("/cambiar-estado")
    public String cambiarEstado(
            @RequestParam Long idHorario,
            @RequestParam Long idEstado,
            RedirectAttributes flash) {
        horarioService.cambiarEstado(idHorario, idEstado);
        flash.addFlashAttribute("mensajeExito",
                idEstado == 1L ? "Horario activado." : "Horario desactivado.");
        return "redirect:/gestionAcademica?tab=horarios";
    }
}

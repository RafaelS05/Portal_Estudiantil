package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Aula;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Horario;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Materia;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Seccion;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.SeccionMateria;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.AulaRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.HorarioRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.MateriaRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.SeccionMateriaRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.SeccionRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.HorarioService;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.SeccionService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
@RequestMapping("/gestionAcademica/horario")
public class HorarioController {

    @Autowired
    private HorarioService horarioService;

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private SeccionMateriaRepository seccionMateriaRepository;

    @Autowired
    private SeccionRepository seccionRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private AulaRepository aulaRepository;

    @Autowired
    private SeccionService seccionService;

    @GetMapping
    public String listaHorario (Model model) {
        List<Horario> horarios = horarioService.listarActivos();
        List<Seccion> secciones = seccionRepository.listarActivas();

        model.addAttribute("horarios", horarios);
        model.addAttribute("secciones", secciones);
        model.addAttribute("totalActivos", horarioService.contarActivos());
        model.addAttribute("paginaActual", "gestion-academica");
        model.addAttribute("diasSemana", diasSemanaMap());
        return "horarios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoHorario (Model model) {
        List<Seccion> secciones = seccionRepository.listarActivas();
        List<Materia> materias = materiaRepository.listarActivas();
        List<Aula> aulas = aulaRepository.listarActivas();
        List<SeccionMateria> seccionMaterias = seccionMateriaRepository.listarActivas();

        model.addAttribute("horario", new Horario());
        model.addAttribute("secciones", secciones);
        model.addAttribute("materias", materias);
        model.addAttribute("aulas", aulas);
        model.addAttribute("seccionMaterias", seccionMaterias);
        model.addAttribute("diasSemana", diasSemanaMap());
        model.addAttribute("horasDisponibles", horasDisponibles());
        model.addAttribute("modoEdicion", false);
        model.addAttribute("paginaActual", "gestion-academica");
        return "horarios/formulario";
    }

    @PostMapping("/guardar")
    public String guardarHorario (@RequestParam Integer diaSemana,
            @RequestParam String horaInicio,
            @RequestParam String horaFin,
            @RequestParam(required = false) Long idAulaFk,
            @RequestParam Long idSeccionMateriaFk,
            RedirectAttributes flash) {
        try {
            horarioService.insertar(diaSemana, horaInicio, horaFin, idAulaFk, idSeccionMateriaFk);
            flash.addFlashAttribute("mensajeExito", "Horario creado correctamente.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
            return "redirect:/gestion-academica/horarios/nuevo";
        }
        return "redirect:/gestion-academica/horarios";
    }

    @GetMapping("/editar/{id}")
    public String editarHorario (@PathVariable Long id, Model model, RedirectAttributes flash) {
        Horario horario = horarioService.buscarPorId(id);
        if (horario == null) {
            flash.addFlashAttribute("mensajeError", "Horario no encontrado.");
            return "redirect:/gestion-academica/horarios";
        }

        List<Seccion> secciones = seccionRepository.listarActivas();
        List<Materia> materias = materiaRepository.listarActivas();
        List<Aula> aulas = aulaRepository.listarActivas();
        List<SeccionMateria> seccionMaterias = seccionMateriaRepository.listarActivas();

        model.addAttribute("horario", horario);
        model.addAttribute("secciones", secciones);
        model.addAttribute("materias", materias);
        model.addAttribute("aulas", aulas);
        model.addAttribute("seccionMaterias", seccionMaterias);
        model.addAttribute("diasSemana", diasSemanaMap());
        model.addAttribute("horasDisponibles", horasDisponibles());
        model.addAttribute("modoEdicion", true);
        model.addAttribute("paginaActual", "gestion-academica");
        return "horarios/formulario";
    }

    @PostMapping("/actualizar")
    public String actualizarHorario (@RequestParam Long idHorario,
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
            return "redirect:/gestion-academica/horarios/editar/" + idHorario;
        }
        return "redirect:/gestion-academica/horarios";
    }

    @PostMapping("/cambiar-estado")
    public String cambiarEstadoHoario (@RequestParam Long idHorario,
            @RequestParam Long idEstado,
            RedirectAttributes flash) {
        horarioService.cambiarEstado(idHorario, idEstado);
        String msg = idEstado == 1L ? "Horario activado." : "Horario desactivado.";
        flash.addFlashAttribute("mensajeExito", msg);
        return "redirect:/gestion-academica/horarios";
    }

    private Map<Integer, String> diasSemanaMap() {
        Map<Integer, String> dias = new LinkedHashMap<>();
        dias.put(1, "Lunes");
        dias.put(2, "Martes");
        dias.put(3, "Miércoles");
        dias.put(4, "Jueves");
        dias.put(5, "Viernes");
        dias.put(6, "Sábado");
        return dias;
    }

    private List<String> horasDisponibles() {
        List<String> horas = new java.util.ArrayList<>();
        for (int h = 7; h <= 16; h++) {
            horas.add(String.format("%02d:00", h));
            horas.add(String.format("%02d:20", h));
            horas.add(String.format("%02d:40", h));
        }
        return horas;
    }

}

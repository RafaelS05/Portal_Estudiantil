package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.*;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/gestionAcademica")
public class GestionAcademicaController {

    @Autowired
    private PeriodoService periodoService;
    @Autowired
    private MateriaService materiaService;
    @Autowired
    private SeccionService seccionService;
    @Autowired
    private AulaService aulaService;
    @Autowired
    private SeccionMateriaService seccionMateriaService;
    @Autowired
    private HorarioService horarioService;
    @Autowired
    private MatriculaService matriculaService;
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PeriodoRepository periodoRepository;
    @Autowired
    private MateriaRepository materiaRepository;
    @Autowired
    private SeccionRepository seccionRepository;
    @Autowired
    private AulaRepository aulaRepository;
    @Autowired
    private SeccionMateriaRepository seccionMateriaRepository;
    @Autowired
    private MatriculaRepository matriculaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public String index(@RequestParam(required = false, defaultValue = "periodos") String tab,
            Model model) {

        //Periodo
        model.addAttribute("periodos", periodoService.listarResumen());
        model.addAttribute("periodoActual", periodoService.obtenerPeriodoActual());
        model.addAttribute("totalPeriodosActivos", periodoService.contarActivos());

        //Materia
        model.addAttribute("materias", materiaService.listarTodas());
        model.addAttribute("totalMateriasActivas", materiaService.contarActivas());

        //Seccion
        model.addAttribute("secciones", seccionService.listarResumen());
        model.addAttribute("periodosActivos", periodoRepository.listarActivos());
        model.addAttribute("totalSeccionesActivas", seccionService.contarActivas());
        model.addAttribute("matriculasPorSeccion", matriculaService.listTodos());

        //Aula
        model.addAttribute("aulas", aulaService.listarTodas());
        model.addAttribute("totalAulasActivas", aulaService.contarActivas());

        //SeccionMateria
        model.addAttribute("asignaciones", seccionMateriaService.listarTodas());
        model.addAttribute("seccionesActivas", seccionRepository.listarActivas());
        model.addAttribute("materiasActivas", materiaRepository.listarActivas());
        model.addAttribute("aulasActivas", aulaRepository.listarActivas());
        model.addAttribute("totalAsignacionesActivas", seccionMateriaService.contarActivas());
        model.addAttribute("profesorAsigna", usuarioService.buscarPorTipoYEstado(2L, 1L));

        //Horario
        model.addAttribute("horarios", horarioService.listarTodos());
        model.addAttribute("seccionMaterias", seccionMateriaService.listarTodas());
        model.addAttribute("diasSemana", diasSemanaMap());
        model.addAttribute("horasDisponibles", horasDisponibles());
        model.addAttribute("totalHorariosActivos", horarioService.contarActivos());

        //Matricula
        model.addAttribute("todasMatriculas", matriculaService.listTodos());
        model.addAttribute("estudiantesActivos", usuarioRepository.listarEstudiantesSinMatriculaActiva());
        model.addAttribute("totalMatriculasActivas", matriculaService.contarActivas());

        //Comunes y js
        model.addAttribute("tabActivo", tab);
        model.addAttribute("paginaActual", "gestion-academica");
        model.addAttribute("pageTitle", "Gestión Académica");

        return "gestionAcademica/gestionAcademica";
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

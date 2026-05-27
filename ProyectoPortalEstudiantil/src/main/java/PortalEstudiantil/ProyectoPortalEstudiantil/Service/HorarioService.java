package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Horario;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.HorarioRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.HorarioRepository.HorarioRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.HorarioRepository.SeccionEncargadoRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.SeccionMateriaRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HorarioService {

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private SeccionMateriaRepository seccionMateriaRepository;

    public List<HorarioRow> listarActivos() {
        return horarioRepository.listarResumen();
    }

    public List<HorarioRow> listarTodos() {
        return horarioRepository.listarTodos();
    }

    // ── Vista de consulta de horarios por sección ────────────────

    public List<HorarioRow> horarioPorSeccion(Long idSeccion) {
        return horarioRepository.listarActivosPorSeccion(idSeccion);
    }

    public List<SeccionEncargadoRow> seccionesDeEncargado(Long idEncargado) {
        return horarioRepository.listarSeccionesDeEncargado(idEncargado);
    }

    /** Agrupa los bloques de una sección por día de la semana, en orden. */
    public List<DiaHorario> agruparPorDia(List<HorarioRow> bloques) {
        Map<Integer, List<HorarioRow>> porDia = new LinkedHashMap<>();
        for (HorarioRow b : bloques) {
            porDia.computeIfAbsent(b.getDiaSemana(), k -> new ArrayList<>()).add(b);
        }
        List<DiaHorario> dias = new ArrayList<>();
        porDia.forEach((dia, lista) -> dias.add(new DiaHorario(dia, nombreDia(dia), lista)));
        return dias;
    }

    public record DiaHorario(int dia, String nombreDia, List<HorarioRow> bloques) {

        public int getDia() {
            return dia;
        }

        public String getNombreDia() {
            return nombreDia;
        }

        public List<HorarioRow> getBloques() {
            return bloques;
        }
    }

    public List<Horario> listarPorSeccionMateria(Long idSeccionMateria) {
        return horarioRepository.listarPorSeccionMateria(idSeccionMateria);
    }

    public List<Horario> listarPorAula(Long idAula) {
        return horarioRepository.listarPorAula(idAula);
    }

    public Horario buscarPorId(Long id) {
        return horarioRepository.findByIdHorario(id);
    }

    @Transactional
    public Long insertar(Integer diaSemana, String horaInicio, String horaFin,
            Long idAulaFk, Long idSeccionMateriaFk) {
        // Validar duplicado de bloque
        if (horarioRepository.contarDuplicadoBloque(diaSemana, horaInicio, horaFin,
                idSeccionMateriaFk, null) > 0) {
            throw new IllegalArgumentException(
                    "Ya existe un horario para ese bloque en esta sección-materia.");
        }
        horarioRepository.insertarHorario(
                diaSemana, horaInicio, horaFin,
                idAulaFk, idSeccionMateriaFk, 1L);

        return horarioRepository.obtenerUltimoIdInsertado();

    }

    @Transactional
    public void modificar(Long idHorario, Integer diaSemana, String horaInicio, String horaFin,
            Long idAulaFk, Long idSeccionMateriaFk) {
        Horario existente = horarioRepository.findByIdHorario(idHorario);
        if (existente == null) {
            throw new IllegalArgumentException("Horario no encontrado: " + idHorario);
        }
        if (horarioRepository.contarDuplicadoBloque(diaSemana, horaInicio, horaFin,
                idSeccionMateriaFk, idHorario) > 0) {
            throw new IllegalArgumentException(
                    "Ya existe un horario para ese bloque en esta sección-materia.");
        }
        horarioRepository.modificarHorario(idHorario, diaSemana, horaInicio, horaFin,
                idAulaFk, idSeccionMateriaFk);
    }

    @Transactional
    public void cambiarEstado(Long idHorario, Long idEstado) {
        horarioRepository.cambiarEstadoHorario(idHorario, idEstado);
    }

    public long contarActivos() {
        return horarioRepository.countByIdEstadoFk(1L);
    }

    public static String nombreDia(int dia) {
        return switch (dia) {
            case 1 ->
                "Lunes";
            case 2 ->
                "Martes";
            case 3 ->
                "Miércoles";
            case 4 ->
                "Jueves";
            case 5 ->
                "Viernes";
            case 6 ->
                "Sábado";
            case 7 ->
                "Domingo";
            default ->
                "Día " + dia;
        };
    }
}

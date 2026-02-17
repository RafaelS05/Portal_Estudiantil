package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Horario;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.HorarioRepository;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HorarioService {

    private static final Logger log = LoggerFactory.getLogger(HorarioService.class);

    private static final Long ESTADO_ACTIVO = 1L;
    private static final Long ESTADO_INACTIVO = 2L;

    private static final DateTimeFormatter HHMM = DateTimeFormatter.ofPattern("HH:mm");

    private final HorarioRepository horarioRepository;

    public HorarioService(HorarioRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
    }

    // Helpers
    private String normalizar(String s) {
        return s == null ? null : s.trim();
    }

    private void validarDiaSemana(Integer diaSemana) {
        if (diaSemana == null) {
            throw new IllegalArgumentException("El día de la semana es obligatorio.");
        }
        // Ajusta si tu sistema usa 0-6
        if (diaSemana < 1 || diaSemana > 7) {
            throw new IllegalArgumentException("El día de la semana debe estar entre 1 y 7.");
        }
    }

    private LocalTime parseHora(String hora, String campo) {
        String h = normalizar(hora);
        if (h == null || h.isEmpty()) {
            throw new IllegalArgumentException("La " + campo + " es obligatoria.");
        }
        try {
            return LocalTime.parse(h, HHMM);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("La " + campo + " debe tener formato HH:mm. Valor recibido: " + hora);
        }
    }

    private void validarRangoHoras(String horaInicio, String horaFin) {
        LocalTime ini = parseHora(horaInicio, "hora de inicio");
        LocalTime fin = parseHora(horaFin, "hora de fin");
        if (!ini.isBefore(fin)) {
            throw new IllegalArgumentException("La hora de inicio debe ser menor que la hora de fin.");
        }
    }

    private void validarIds(Long idSeccionMateriaFk, Long idEstadoFk) {
        if (idSeccionMateriaFk == null) {
            throw new IllegalArgumentException("La sección-materia es obligatoria.");
        }
        if (idEstadoFk == null) {
            throw new IllegalArgumentException("El estado es obligatorio.");
        }
    }

    private void validarDuplicado(Integer diaSemana, String horaInicio, String horaFin,
            Long idSeccionMateriaFk, Long idHorario) {
        long dup = horarioRepository.contarDuplicadoBloque(diaSemana, horaInicio, horaFin, idSeccionMateriaFk, idHorario);
        if (dup > 0) {
            throw new IllegalArgumentException(
                    "Ya existe un bloque de horario igual para esa Sección-Materia (día + hora inicio + hora fin)."
            );
        }
    }

    // CRUD
    @Transactional
    public Long crearHorario(Integer diaSemana, String horaInicio, String horaFin,
            Long idAulaFk, Long idSeccionMateriaFk, Long idEstadoFk) {

        validarDiaSemana(diaSemana);

        horaInicio = normalizar(horaInicio);
        horaFin = normalizar(horaFin);

        validarRangoHoras(horaInicio, horaFin);
        validarIds(idSeccionMateriaFk, idEstadoFk);

        //pre-check
        validarDuplicado(diaSemana, horaInicio, horaFin, idSeccionMateriaFk, null);

        Long id = horarioRepository.insertarHorarioRetornaId(
                diaSemana,
                horaInicio,
                horaFin,
                idAulaFk,
                idSeccionMateriaFk,
                idEstadoFk
        );

        if (id == null || id <= 0) {
            throw new RuntimeException("El stored procedure HORARIO_INSERTAR no retornó un ID válido.");
        }

        log.info("Horario creado con ID: {}", id);
        return id;
    }

    @Transactional
    public Long crearHorario(Horario horario) {
        if (horario == null) {
            throw new IllegalArgumentException("El horario es obligatorio.");
        }
        return crearHorario(
                horario.getDiaSemana(),
                horario.getHoraInicio(),
                horario.getHoraFin(),
                horario.getIdAulaFk(),
                horario.getIdSeccionMateriaFk(),
                horario.getIdEstadoFk()
        );
    }

    @Transactional
    public void actualizarHorario(Long idHorario, Integer diaSemana, String horaInicio, String horaFin,
            Long idAulaFk, Long idSeccionMateriaFk) {

        if (idHorario == null) {
            throw new IllegalArgumentException("El ID del horario es obligatorio.");
        }

        // validar existencia
        obtenerHorario(idHorario);

        validarDiaSemana(diaSemana);

        horaInicio = normalizar(horaInicio);
        horaFin = normalizar(horaFin);

        validarRangoHoras(horaInicio, horaFin);

        if (idSeccionMateriaFk == null) {
            throw new IllegalArgumentException("La sección-materia es obligatoria.");
        }

        // pre-check
        validarDuplicado(diaSemana, horaInicio, horaFin, idSeccionMateriaFk, idHorario);

        horarioRepository.modificarHorario(
                idHorario,
                diaSemana,
                horaInicio,
                horaFin,
                idAulaFk,
                idSeccionMateriaFk
        );

        log.info("Horario ID {} actualizado.", idHorario);
    }

    @Transactional
    public void actualizarHorario(Horario horario) {
        if (horario == null) {
            throw new IllegalArgumentException("El horario es obligatorio.");
        }
        if (horario.getIdHorario() == null) {
            throw new IllegalArgumentException("El ID del horario es obligatorio para actualizar.");
        }

        actualizarHorario(
                horario.getIdHorario(),
                horario.getDiaSemana(),
                horario.getHoraInicio(),
                horario.getHoraFin(),
                horario.getIdAulaFk(),
                horario.getIdSeccionMateriaFk()
        );
    }

    // Estado
    @Transactional
    public void cambiarEstadoHorario(Long idHorario, Long idEstado) {
        if (idHorario == null) {
            throw new IllegalArgumentException("El ID del horario es obligatorio.");
        }
        if (idEstado == null) {
            throw new IllegalArgumentException("El estado es obligatorio.");
        }

        // valida existencia
        obtenerHorario(idHorario);

        horarioRepository.cambiarEstadoHorario(idHorario, idEstado);

        log.info("Horario ID {} estado cambiado a {}", idHorario,
                ESTADO_ACTIVO.equals(idEstado) ? "ACTIVO" : "INACTIVO");
    }

    @Transactional
    public void activarHorario(Long idHorario) {
        cambiarEstadoHorario(idHorario, ESTADO_ACTIVO);
    }

    @Transactional
    public void desactivarHorario(Long idHorario) {
        cambiarEstadoHorario(idHorario, ESTADO_INACTIVO);
    }

    // Consultas
    public Horario obtenerHorario(Long idHorario) {
        return horarioRepository.findById(idHorario)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado con ID: " + idHorario));
    }

    public List<Horario> listarTodos() {
        return horarioRepository.findAll();
    }

    public List<Horario> listarActivos() {
        return horarioRepository.listarActivos();
    }

    public List<Horario> listarPorSeccionMateria(Long idSeccionMateriaFk) {
        if (idSeccionMateriaFk == null) {
            throw new IllegalArgumentException("La sección-materia es obligatoria.");
        }
        return horarioRepository.listarPorSeccionMateria(idSeccionMateriaFk);
    }

    public List<Horario> listarPorAula(Long idAulaFk) {
        if (idAulaFk == null) {
            throw new IllegalArgumentException("El aula es obligatoria.");
        }
        return horarioRepository.listarPorAula(idAulaFk);
    }

    public List<Horario> listarPorSeccionMateriaYDia(Long idSeccionMateriaFk, Integer diaSemana) {
        if (idSeccionMateriaFk == null) {
            throw new IllegalArgumentException("La sección-materia es obligatoria.");
        }
        validarDiaSemana(diaSemana);
        return horarioRepository.listarPorSeccionMateriaYDia(idSeccionMateriaFk, diaSemana);
    }

    public long contarPorEstado(Long idEstadoFk) {
        if (idEstadoFk == null) {
            throw new IllegalArgumentException("El estado es obligatorio.");
        }
        return horarioRepository.countByIdEstadoFk(idEstadoFk);
    }
}

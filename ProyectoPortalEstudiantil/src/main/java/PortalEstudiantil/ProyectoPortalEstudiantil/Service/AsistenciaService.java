package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.AsistenciaRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.AsistenciaRepository.AsistenciaListadoRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.AsistenciaRepository.PaseListaRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class AsistenciaService {

    private static final Logger log = LoggerFactory.getLogger(AsistenciaService.class);

    // Estados de asistencia (coinciden con ESTADOS_TB)
    public static final Long ESTADO_PRESENTE = 7L;
    public static final Long ESTADO_AUSENTE = 8L;
    public static final Long ESTADO_JUSTIFICADO = 10L;

    private final AsistenciaRepository asistenciaRepository;

    public AsistenciaService(AsistenciaRepository asistenciaRepository) {
        this.asistenciaRepository = asistenciaRepository;
    }

    // ══════════════════════════════════════════════════════════════════
    // LISTADO / CONSULTAS
    // ══════════════════════════════════════════════════════════════════
    public List<AsistenciaListadoRow> listar(Long idSeccionMateria, LocalDate fecha) {
        if (idSeccionMateria != null && fecha != null) {
            return asistenciaRepository.listarPorSeccionMateriaYFecha(idSeccionMateria, fecha);
        } else if (idSeccionMateria != null) {
            return asistenciaRepository.listarPorSeccionMateria(idSeccionMateria);
        } else if (fecha != null) {
            return asistenciaRepository.listarPorFecha(fecha);
        }
        return asistenciaRepository.listarTodas();
    }

    public long contarPorEstado(Long idEstado) {
        return asistenciaRepository.countByIdEstadoFk(idEstado);
    }

    // ══════════════════════════════════════════════════════════════════
    // PASE DE LISTA
    // ══════════════════════════════════════════════════════════════════
    public List<PaseListaRow> cargarPaseLista(Long idSeccionMateria, LocalDate fecha) {
        if (idSeccionMateria == null) {
            throw new IllegalArgumentException("La sección-materia es obligatoria");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        return asistenciaRepository.cargarPaseLista(idSeccionMateria, fecha);
    }

    /**
     * Guarda el pase de lista completo. {@code estadosPorMatricula} es un mapa
     * idMatricula → idEstado generado por el controller al parsear los
     * parámetros del form.
     */
    @Transactional
    public void guardarPaseLista(Long idSeccionMateria,
            LocalDate fecha,
            Map<Long, Long> estadosPorMatricula) {

        if (idSeccionMateria == null) {
            throw new IllegalArgumentException("La sección-materia es obligatoria");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        if (estadosPorMatricula == null || estadosPorMatricula.isEmpty()) {
            throw new IllegalArgumentException("No hay registros de asistencia para guardar");
        }

        // Cargamos los registros actuales para saber cuáles ya existen
        List<PaseListaRow> existentes = asistenciaRepository.cargarPaseLista(idSeccionMateria, fecha);

        for (PaseListaRow fila : existentes) {
            Long idMatricula = fila.getIdMatriculaFk();
            Long nuevoEstado = estadosPorMatricula.get(idMatricula);
            if (nuevoEstado == null) {
                continue; // No vino en el form (no debería ocurrir)
            }
            if (fila.getIdAsistencia() != null) {
                // Ya existe: solo cambiamos estado si es diferente
                if (!nuevoEstado.equals(fila.getIdEstadoFk())) {
                    asistenciaRepository.cambiarEstadoAsistencia(fila.getIdAsistencia(), nuevoEstado);
                }
            } else {
                // No existe: insertar
                asistenciaRepository.insertarAsistencia(fecha, idMatricula, idSeccionMateria, nuevoEstado);
            }
        }

        log.info("Pase de lista guardado – SeccionMateria: {}, Fecha: {}, Alumnos: {}",
                idSeccionMateria, fecha, estadosPorMatricula.size());
    }

    // ══════════════════════════════════════════════════════════════════
    // CAMBIAR ESTADO / JUSTIFICAR
    // ══════════════════════════════════════════════════════════════════
    @Transactional
    public void cambiarEstado(Long idAsistencia, Long idEstado) {
        if (!asistenciaRepository.existsById(idAsistencia)) {
            throw new RuntimeException("No existe asistencia con ID: " + idAsistencia);
        }
        if (idEstado == null) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }
        asistenciaRepository.cambiarEstadoAsistencia(idAsistencia, idEstado);
        log.info("Asistencia ID {} → estado {}", idAsistencia, idEstado);
    }

    @Transactional
    public void justificar(Long idAsistencia) {
        cambiarEstado(idAsistencia, ESTADO_JUSTIFICADO);
        log.info("Asistencia ID {} justificada", idAsistencia);
    }
}

package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Correo;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.AsistenciaRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.AsistenciaRepository.AsistenciaListadoRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.AsistenciaRepository.PaseListaRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.AsistenciaRepository.SeccionMateriaDropdownRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.CorreoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AsistenciaService {

    private static final Logger log = LoggerFactory.getLogger(AsistenciaService.class);

    public static final Long ESTADO_PRESENTE = 7L;
    public static final Long ESTADO_AUSENTE = 8L;
    public static final Long ESTADO_JUSTIFICADO = 10L;

    private final AsistenciaRepository asistenciaRepository;
    private final CorreoRepository correoRepo; // ← AGREGADO

    public AsistenciaService(AsistenciaRepository asistenciaRepository,
            CorreoRepository correoRepo) { // ← AGREGADO
        this.asistenciaRepository = asistenciaRepository;
        this.correoRepo = correoRepo;
    }

    // ══════════════════════════════════════════════════════════════════
    // NUEVO: obtener ID de usuario por email (para leer el user logueado)
    // ══════════════════════════════════════════════════════════════════
    public Long obtenerIdUsuarioPorEmail(String email) {
        Correo correo = correoRepo.findByCorreo(email);
        return correo.getUsuario().getIdUsuario();
    }

    // ══════════════════════════════════════════════════════════════════
    // NUEVO: secciones-materia para el dropdown del modal
    // ══════════════════════════════════════════════════════════════════
    public List<SeccionMateriaDropdownRow> obtenerSeccionesMateria(Long idDocente, boolean esAdmin) {
        if (esAdmin) {
            return asistenciaRepository.listarTodasSeccionesMateria();
        }
        return asistenciaRepository.listarSeccionesMateriaPorDocente(idDocente);
    }

    // ══════════════════════════════════════════════════════════════════
    // LISTADO (actualizado: acepta idDocente para filtrar por profesor)
    // ══════════════════════════════════════════════════════════════════
    public List<AsistenciaListadoRow> listar(Long idSeccionMateria, LocalDate fecha, Long idDocente) {
        List<AsistenciaListadoRow> result;

        if (idDocente != null) {
            // Profesor: trae solo sus registros y filtra en memoria si hay filtros adicionales
            result = asistenciaRepository.listarPorDocente(idDocente);

            if (idSeccionMateria != null) {
                final Long sm = idSeccionMateria;
                result = result.stream()
                        .filter(a -> sm.equals(a.getIdSeccionMateriaFk()))
                        .collect(Collectors.toList());
            }
            if (fecha != null) {
                final String fechaStr = fecha.toString();
                result = result.stream()
                        .filter(a -> fechaStr.equals(a.getFechaAsistencia()))
                        .collect(Collectors.toList());
            }
        } else {
            // Admin: comportamiento original con queries optimizadas
            if (idSeccionMateria != null && fecha != null) {
                result = asistenciaRepository.listarPorSeccionMateriaYFecha(idSeccionMateria, fecha);
            } else if (idSeccionMateria != null) {
                result = asistenciaRepository.listarPorSeccionMateria(idSeccionMateria);
            } else if (fecha != null) {
                result = asistenciaRepository.listarPorFecha(fecha);
            } else {
                result = asistenciaRepository.listarTodas();
            }
        }

        return result;
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

        List<PaseListaRow> existentes = asistenciaRepository.cargarPaseLista(idSeccionMateria, fecha);

        for (PaseListaRow fila : existentes) {
            Long idMatricula = fila.getIdMatriculaFk();
            Long nuevoEstado = estadosPorMatricula.get(idMatricula);
            if (nuevoEstado == null) {
                continue;
            }
            if (fila.getIdAsistencia() != null) {
                if (!nuevoEstado.equals(fila.getIdEstadoFk())) {
                    asistenciaRepository.cambiarEstadoAsistencia(fila.getIdAsistencia(), nuevoEstado);
                }
            } else {
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
    
    // ══════════════════════════════════════════════════════════════════
    // HISTORIAL DE ASISTENCIA – ENCARGADO
    // ══════════════════════════════════════════════════════════════════
    public List<AsistenciaRepository.HijoDropdownRow> obtenerHijosDeEncargado(Long idEncargado) {
        return asistenciaRepository.listarHijosPorEncargado(idEncargado);
    }

    public List<AsistenciaRepository.HistorialAsistenciaHijoRow> obtenerHistorialHijo(
            Long idEncargado, Long idEstudiante) {
        return asistenciaRepository.listarHistorialPorEncargado(idEncargado, idEstudiante);
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Periodo;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.PeriodoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PeriodoService {

    private static final Logger log = LoggerFactory.getLogger(PeriodoService.class);

    private static final Long ESTADO_ACTIVO = 1L;
    private static final Long ESTADO_INACTIVO = 2L;

    // Regla del proyecto: existen 2 periodos en total
    private static final long MAX_PERIODOS = 2;

    private final PeriodoRepository periodoRepository;

    public PeriodoService(PeriodoRepository periodoRepository) {
        this.periodoRepository = periodoRepository;
    }

    // VALIDACIONES
    private void validarBasico(String nombre, LocalDate inicio, LocalDate fin, Long idEstadoFk) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del período es obligatorio");
        }
        if (inicio == null) {
            throw new IllegalArgumentException("La fecha de inicio es obligatoria");
        }
        if (fin == null) {
            throw new IllegalArgumentException("La fecha de fin es obligatoria");
        }
        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser mayor que la fecha de fin");
        }
        if (idEstadoFk == null) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }
    }

    private void validarMaximoPeriodos() {
        long total = periodoRepository.count();
        if (total >= MAX_PERIODOS) {
            throw new IllegalArgumentException(
                    "No se pueden crear más períodos. El sistema permite máximo " + MAX_PERIODOS + " períodos."
            );
        }
    }

    private void validarNombreUnico(String nombre, Long idPeriodoActual) {
        long duplicados = periodoRepository.contarNombreDuplicado(nombre.trim(), idPeriodoActual);
        if (duplicados > 0) {
            throw new IllegalArgumentException("Ya existe un período con el nombre '" + nombre.trim() + "'");
        }
    }

    private void validarNoTraslapeActivos(LocalDate inicio, LocalDate fin, Long idPeriodoActual) {
        long traslapes = periodoRepository.contarTraslapesActivos(inicio, fin, idPeriodoActual);
        if (traslapes > 0) {
            throw new IllegalArgumentException("El rango de fechas se traslapa con otro período ACTIVO.");
        }
    }

    // CRUD
    /**
     * Crea un período. Si se crea en ACTIVO, garantiza: único ACTIVO + sin
     * traslape con otros activos. Retorna ID.
     */
    @Transactional
    public Long crearPeriodo(String nombre, LocalDate fechaInicio, LocalDate fechaFin, Long idEstadoFk) {

        validarBasico(nombre, fechaInicio, fechaFin, idEstadoFk);
        validarMaximoPeriodos();
        validarNombreUnico(nombre, null);

        // Si nace activo, no puede chocar con otro activo.
        if (ESTADO_ACTIVO.equals(idEstadoFk)) {
            validarNoTraslapeActivos(fechaInicio, fechaFin, null);
        }

        Long idCreado = periodoRepository.insertarPeriodoRetornaId(
                nombre.trim(),
                fechaInicio,
                fechaFin,
                idEstadoFk
        );

        if (idCreado == null || idCreado <= 0) {
            throw new RuntimeException("El stored procedure PERIODOS_INSERTAR no retornó un ID válido.");
        }

        log.info("Período creado con ID {}", idCreado);

        // Regla: si se creó ACTIVO, debe quedar como único ACTIVO
        if (ESTADO_ACTIVO.equals(idEstadoFk)) {
            activarPeriodo(idCreado); // desactiva los otros si existieran
        }

        return idCreado;
    }

    @Transactional
    public Long crearPeriodo(Periodo periodo) {
        return crearPeriodo(
                periodo.getNombre(),
                periodo.getFechaInicio(),
                periodo.getFechaFin(),
                periodo.getIdEstadoFk()
        );
    }

    /**
     * Actualiza datos (no cambia estado). Si el período está ACTIVO, se valida
     * que el nuevo rango NO choque con otro ACTIVO.
     */
    @Transactional
    public void actualizarPeriodo(Long idPeriodo, String nombre, LocalDate fechaInicio, LocalDate fechaFin) {

        if (idPeriodo == null) {
            throw new IllegalArgumentException("El ID del período es obligatorio");
        }
        Periodo actual = obtenerPeriodo(idPeriodo);

        // Estado actual se mantiene (aquí no se cambia)
        validarBasico(nombre, fechaInicio, fechaFin, actual.getIdEstadoFk());
        validarNombreUnico(nombre, idPeriodo);

        if (ESTADO_ACTIVO.equals(actual.getIdEstadoFk())) {
            validarNoTraslapeActivos(fechaInicio, fechaFin, idPeriodo);
        }

        periodoRepository.modificarPeriodo(idPeriodo, nombre.trim(), fechaInicio, fechaFin);
        log.info("Período {} actualizado", idPeriodo);
    }

    @Transactional
    public void actualizarPeriodo(Periodo periodo) {
        if (periodo.getIdPeriodo() == null) {
            throw new IllegalArgumentException("El ID del período es obligatorio para actualizar");
        }
        actualizarPeriodo(
                periodo.getIdPeriodo(),
                periodo.getNombre(),
                periodo.getFechaInicio(),
                periodo.getFechaFin()
        );
    }

    // ESTADOS (REGLAS)
    /**
     * Activa un período. Regla: solo 1 ACTIVO. Desactiva los demás. Regla
     * adicional: no traslapar con otro activo (defensivo).
     */
    @Transactional
    public void activarPeriodo(Long idPeriodo) {

        if (idPeriodo == null) {
            throw new IllegalArgumentException("El ID del período es obligatorio");
        }
        Periodo aActivar = obtenerPeriodo(idPeriodo);

        // Evitar traslape contra otros activos
        validarNoTraslapeActivos(aActivar.getFechaInicio(), aActivar.getFechaFin(), idPeriodo);

        // Desactivar otros activos
        List<Periodo> activos = periodoRepository.listarActivos();
        for (Periodo p : activos) {
            if (p.getIdPeriodo() != null && !p.getIdPeriodo().equals(idPeriodo)) {
                periodoRepository.cambiarEstadoPeriodo(p.getIdPeriodo(), ESTADO_INACTIVO);
                log.info("Se desactivó automáticamente el período {}", p.getIdPeriodo());
            }
        }

        // Activar el indicado (si ya estaba activo, igual no pasa nada grave)
        periodoRepository.cambiarEstadoPeriodo(idPeriodo, ESTADO_ACTIVO);
        log.info("Período {} activado", idPeriodo);
    }

    @Transactional
    public void desactivarPeriodo(Long idPeriodo) {
        if (idPeriodo == null) {
            throw new IllegalArgumentException("El ID del período es obligatorio");
        }
        obtenerPeriodo(idPeriodo); // valida existencia

        // Obliga a que siempre exista 1 activo:
        Periodo p = obtenerPeriodo(idPeriodo);
        if (ESTADO_ACTIVO.equals(p.getIdEstadoFk()) && periodoRepository.countByIdEstadoFk(ESTADO_ACTIVO) <= 1) {
            throw new IllegalArgumentException("No se puede desactivar el único período ACTIVO del sistema.");
        }

        periodoRepository.cambiarEstadoPeriodo(idPeriodo, ESTADO_INACTIVO);
        log.info("Período {} desactivado", idPeriodo);
    }

    @Transactional
    public void cambiarEstadoPeriodo(Long idPeriodo, Long idEstado) {
        if (idEstado == null) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }

        if (ESTADO_ACTIVO.equals(idEstado)) {
            activarPeriodo(idPeriodo);
            return;
        }

        if (ESTADO_INACTIVO.equals(idEstado)) {
            desactivarPeriodo(idPeriodo);
            return;
        }

        obtenerPeriodo(idPeriodo);
        periodoRepository.cambiarEstadoPeriodo(idPeriodo, idEstado);
        log.info("Período {} cambiado a estado {}", idPeriodo, idEstado);
    }

    // CONSULTAS
    public Periodo obtenerPeriodo(Long idPeriodo) {
        return periodoRepository.findById(idPeriodo)
                .orElseThrow(() -> new RuntimeException("Período no encontrado con ID: " + idPeriodo));
    }

    public List<Periodo> listarTodos() {
        return periodoRepository.findAll();
    }

    public List<Periodo> listarActivos() {
        return periodoRepository.listarActivos();
    }

    public List<PeriodoRepository.PeriodoRow> listarResumen() {
        return periodoRepository.listarResumen();
    }

    public Periodo obtenerPeriodoActualEnCurso() {
        return periodoRepository.obtenerPeriodoActualEnCurso();
    }

    public Periodo buscarPeriodoQueContieneFecha(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        return periodoRepository.buscarPeriodoQueContieneFecha(fecha);
    }

    public List<Periodo> buscarPorRango(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new IllegalArgumentException("Desde y hasta son obligatorios");
        }
        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException("El rango es inválido (desde > hasta)");
        }
        return periodoRepository.buscarPorRango(desde, hasta);
    }

    public long contarActivos() {
        return periodoRepository.countByIdEstadoFk(ESTADO_ACTIVO);
    }

    public long contarTotal() {
        return periodoRepository.count();
    }

    public boolean existeNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        return periodoRepository.existsByNombreIgnoreCase(nombre.trim());
    }
}

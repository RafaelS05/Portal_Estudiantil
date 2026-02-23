package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.ReporteAcademico;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.ReporteAcademicoRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.ReporteAcademicoRepository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReporteAcademicoService {

    private static final Logger log           = LoggerFactory.getLogger(ReporteAcademicoService.class);
    private static final Long   ESTADO_ACTIVO = 1L;
    private static final Long   ESTADO_INACTIVO = 2L;

    private final ReporteAcademicoRepository repo;

    public ReporteAcademicoService(ReporteAcademicoRepository repo) {
        this.repo = repo;
    }

    // ==================== CONSULTAS ====================

    public List<ReporteListadoRow> listarReportesActivos() {
        return repo.listarReportesActivos();
    }

    public List<ReporteListadoRow> buscarReportes(String busqueda) {
        if (busqueda == null || busqueda.trim().isEmpty()) return listarReportesActivos();
        return repo.buscarReportes(busqueda.trim());
    }

    public List<ReporteDetalleRow> obtenerDetalle(Long idReporte) {
        if (idReporte == null) throw new IllegalArgumentException("El ID del reporte es obligatorio");
        return repo.obtenerDetalleReporte(idReporte);
    }

    public ReporteAcademico obtenerReporte(Long idReporte) {
        return repo.findById(idReporte)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado con ID: " + idReporte));
    }

    public List<EstudianteDisponibleRow> buscarEstudiantes(String busqueda) {
        if (busqueda == null || busqueda.trim().isEmpty())
            throw new IllegalArgumentException("Ingrese al menos un nombre para buscar");
        return repo.buscarEstudiantesParaReporte(busqueda.trim());
    }

    // ==================== CRUD ====================

    @Transactional
    public void crearReporte(Long idMatricula, Long idPeriodo, Long idTipoReporte,
                             BigDecimal promedio, Long idGeneradoPor) {
        validar(idMatricula, idPeriodo, idTipoReporte, idGeneradoPor);
        repo.insertarReporte(LocalDate.now(), promedio, idTipoReporte,
                idGeneradoPor, idMatricula, idPeriodo, ESTADO_ACTIVO);
        log.info("Reporte creado – matrícula {} por usuario {}", idMatricula, idGeneradoPor);
    }

    @Transactional
    public void modificarReporte(Long idReporte, Long idMatricula, Long idPeriodo,
                                 Long idTipoReporte, BigDecimal promedio, Long idGeneradoPor) {
        if (idReporte == null) throw new IllegalArgumentException("El ID del reporte es obligatorio");
        validar(idMatricula, idPeriodo, idTipoReporte, idGeneradoPor);
        ReporteAcademico existente = obtenerReporte(idReporte);
        repo.modificarReporte(idReporte, existente.getFechaCreacionReporte(),
                promedio, idTipoReporte, idGeneradoPor, idMatricula, idPeriodo);
        log.info("Reporte ID {} modificado por usuario {}", idReporte, idGeneradoPor);
    }

    @Transactional
    public void eliminarReporte(Long idReporte) {
        if (!repo.existsById(idReporte))
            throw new RuntimeException("No existe reporte con ID: " + idReporte);
        repo.cambiarEstadoReporte(idReporte, ESTADO_INACTIVO);
        log.info("Reporte ID {} eliminado", idReporte);
    }

    // ==================== VALIDACIÓN INTERNA ====================

    private void validar(Long idMatricula, Long idPeriodo, Long idTipoReporte, Long idGeneradoPor) {
        if (idMatricula   == null) throw new IllegalArgumentException("La matrícula es obligatoria");
        if (idPeriodo     == null) throw new IllegalArgumentException("El período es obligatorio");
        if (idTipoReporte == null) throw new IllegalArgumentException("El tipo de reporte es obligatorio");
        if (idGeneradoPor == null) throw new IllegalArgumentException("El usuario generador es obligatorio");
    }
}
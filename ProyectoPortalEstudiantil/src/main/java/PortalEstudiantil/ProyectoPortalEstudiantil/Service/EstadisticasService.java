package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Periodo;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.EstadisticasRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.Estadisticas.AlertaEstadisticaRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.Estadisticas.AsistenciaEstudianteRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.Estadisticas.CalificacionPorMateriaRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.Estadisticas.CalificacionPorSeccionRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.Estadisticas.KpiGeneralRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Security.PortalUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadisticasService {

    private static final Logger log = LoggerFactory.getLogger(EstadisticasService.class);

    private static final Double UMBRAL_CRITICO = 70.0;

    private final EstadisticasRepository estadisticasRepository;

    public EstadisticasService(EstadisticasRepository estadisticasRepository) {
        this.estadisticasRepository = estadisticasRepository;
    }

    // =========================================================
    // PERÍODO
    // =========================================================

    public Periodo obtenerPeriodoActual() {
        return estadisticasRepository.findPeriodoMasReciente()
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró ningún período activo en el sistema."));
    }

    // =========================================================
    // DASHBOARD ADMINISTRADOR / DIRECTOR
    // =========================================================

    public KpiGeneralRow obtenerKpiGeneral(Long idPeriodo) {
        return estadisticasRepository.obtenerKpiGeneral(idPeriodo, UMBRAL_CRITICO);
    }

    public List<CalificacionPorSeccionRow> obtenerCalificacionesPorSeccion(Long idPeriodo) {
        return estadisticasRepository.calificacionesPorSeccion(idPeriodo);
    }

    public List<CalificacionPorMateriaRow> obtenerCalificacionesPorMateria(Long idPeriodo) {
        return estadisticasRepository.calificacionesPorMateria(idPeriodo);
    }

    public List<AlertaEstadisticaRow> obtenerAlertas(Long idPeriodo) {
        return estadisticasRepository.obtenerAlertas(idPeriodo, UMBRAL_CRITICO);
    }

    // =========================================================
    // DASHBOARD ENCARGADO (PADRE DE FAMILIA)
    // =========================================================

    public List<AsistenciaEstudianteRow> obtenerAsistenciaHijos(Authentication authentication,
                                                                Long idPeriodo) {
        Long idEncargado = obtenerIdUsuarioDesdeAuth(authentication);
        return estadisticasRepository.asistenciaHijosEncargado(idEncargado, idPeriodo);
    }

    // =========================================================
    // UTILIDADES
    // =========================================================

    public Double getUmbralCritico() {
        return UMBRAL_CRITICO;
    }

    private Long obtenerIdUsuarioDesdeAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No hay sesión activa.");
        }
        PortalUserDetails pud = (PortalUserDetails) authentication.getPrincipal();
        Long idUsuario = estadisticasRepository.resolverIdUsuarioPorCredencial(pud.getIdCredencial());
        if (idUsuario == null) {
            throw new RuntimeException("No se pudo resolver el usuario de la sesión actual.");
        }
        return idUsuario;
    }
}
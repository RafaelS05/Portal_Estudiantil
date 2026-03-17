package PortalEstudiantil.ProyectoPortalEstudiantil.Repository.Estadisticas;

/**
 * Proyección nativa: indicadores KPI generales para las tarjetas
 * del panel de estadísticas (promedio global, % asistencia global, alertas activas).
 */
public interface KpiGeneralRow {

    Double getPromedioGlobal();          // AVG de todas las calificaciones activas
    Double getPorcentajeAsistenciaGlobal(); // % global de presentes
    Long   getTotalEstudiantesActivos();
    Long   getTotalAlertasCalificacion();  // secciones con promedio < umbral
}
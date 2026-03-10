package PortalEstudiantil.ProyectoPortalEstudiantil.Repository.Estadisticas;

/**
 * Proyección nativa: promedio de calificaciones agrupado por sección y período.
 * Usada en EstadisticasRepository.
 */
public interface CalificacionPorSeccionRow {

    String getSeccion();       // SECCION_TB.NUMERO
    String getPeriodo();       // PERIODOS_TB.NOMBRE
    Double getPromedio();      // AVG(CALIFICACION)
    Long   getTotalEstudiantes();
}
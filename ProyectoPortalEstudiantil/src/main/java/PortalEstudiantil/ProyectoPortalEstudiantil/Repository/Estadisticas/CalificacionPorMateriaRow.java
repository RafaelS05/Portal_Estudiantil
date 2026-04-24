package PortalEstudiantil.ProyectoPortalEstudiantil.Repository.Estadisticas;

/**
 * Proyección nativa: promedio de calificaciones agrupado por materia.
 * Usada en EstadisticasRepository.
 */
public interface CalificacionPorMateriaRow {

    String getMateria();       // MATERIA_TB.NOMBRE
    Double getPromedio();      // AVG(CALIFICACION)
    Long   getTotalEvaluaciones();
}
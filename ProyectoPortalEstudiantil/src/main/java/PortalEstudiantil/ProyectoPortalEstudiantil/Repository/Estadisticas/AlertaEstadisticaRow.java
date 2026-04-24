package PortalEstudiantil.ProyectoPortalEstudiantil.Repository.Estadisticas;

/**
 * Proyección nativa: secciones o materias cuyo promedio está
 * por debajo del umbral crítico configurado (por defecto 70).
 * Usada para el panel de alertas del administrador / director.
 */
public interface AlertaEstadisticaRow {

    String getTipo();          // "SECCIÓN" o "MATERIA"
    String getNombre();        // identificador (número de sección o nombre de materia)
    Double getPromedio();      // AVG(CALIFICACION) que disparó la alerta
    Long   getTotalAfectados();// cantidad de estudiantes con promedio bajo
}
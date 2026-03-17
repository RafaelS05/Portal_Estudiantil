package PortalEstudiantil.ProyectoPortalEstudiantil.Repository.Estadisticas;

/**
 * Proyección nativa: estadísticas de asistencia por estudiante.
 * Usada en EstadisticasRepository para la vista del encargado.
 *
 * El estado ACTIVO (1) equivale a PRESENTE en ASISTENCIAS_TB.
 * El estado INACTIVO (2) equivale a AUSENTE.
 */
public interface AsistenciaEstudianteRow {

    Long   getIdEstudiante();
    String getNombreEstudiante();       // NOMBRE + PRIMER_APELLIDO
    String getSeccion();                // SECCION_TB.NUMERO
    Long   getTotalClases();            // total registros en ASISTENCIAS_TB
    Long   getTotalPresente();          // registros con ID_ESTADO_FK = 1
    Long   getTotalAusente();           // registros con ID_ESTADO_FK = 2
    Double getPorcentajeAsistencia();   // (presentes / total) * 100
}
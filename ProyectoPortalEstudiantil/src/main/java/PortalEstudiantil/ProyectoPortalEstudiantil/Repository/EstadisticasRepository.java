package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.Estadisticas.AlertaEstadisticaRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.Estadisticas.AsistenciaEstudianteRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.Estadisticas.CalificacionPorMateriaRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.Estadisticas.CalificacionPorSeccionRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.Estadisticas.KpiGeneralRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Periodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EstadisticasRepository extends JpaRepository<Periodo, Long> {

    // =========================================================
    // RESOLVER ID_USUARIO DESDE CREDENCIAL (sin tocar AuthRepository)
    // =========================================================
    @Query(value = """
        SELECT ID_USUARIO_FK
        FROM CREDENCIALES_TB
        WHERE ID_CREDENCIAL = :idCredencial
        LIMIT 1
    """, nativeQuery = true)
    Long resolverIdUsuarioPorCredencial(@Param("idCredencial") Long idCredencial);

    // =========================================================
    // PERÍODO MÁS RECIENTE
    // =========================================================
    @Query(value = """
        SELECT p.*
        FROM PERIODOS_TB p
        WHERE p.ID_ESTADO_FK = (SELECT ID_ESTADO FROM ESTADOS_TB WHERE DESCRIPCION = 'ACTIVO' LIMIT 1)
        ORDER BY p.FECHA_FIN DESC
        LIMIT 1
    """, nativeQuery = true)
    Optional<Periodo> findPeriodoMasReciente();

    // =========================================================
    // KPI GENERAL (tarjetas superiores del dashboard)
    // =========================================================
    @Query(value = """
    SELECT
        ROUND(AVG(c.CALIFICACION), 2) AS promedioGlobal,
        (SELECT ROUND(
            SUM(CASE WHEN est_a.DESCRIPCION = 'PRESENTE' THEN 1 ELSE 0 END)
            * 100.0 / NULLIF(COUNT(a.ID_ASISTENCIA), 0)
        , 2)
        FROM ASISTENCIAS_TB a
        JOIN ESTADOS_TB est_a ON a.ID_ESTADO_FK = est_a.ID_ESTADO
        WHERE a.ID_MATRICULA_FK IN (
            SELECT m.ID_MATRICULA
            FROM MATRICULA_TB m
            JOIN SECCION_TB sc ON m.ID_SECCION_FK = sc.ID_SECCION
            WHERE sc.ID_PERIODO_FK = :idPeriodo
        )
        ) AS porcentajeAsistenciaGlobal,
        (SELECT COUNT(*) FROM USUARIOS_TB u
         WHERE u.ID_TIPOUSUARIO_FK = (
             SELECT ID_TIPOUSUARIO FROM TIPOUSUARIO_TB WHERE NOMBRE = 'Estudiante' LIMIT 1)
         AND u.ID_ESTADO_FK = (
             SELECT ID_ESTADO FROM ESTADOS_TB WHERE DESCRIPCION = 'ACTIVO' LIMIT 1)
        ) AS totalEstudiantesActivos,
        (SELECT COUNT(*) FROM (
            SELECT sm.ID_SECCION_FK
            FROM CALIFICACIONES_TB cal
            JOIN EVALUACION_TB ev      ON cal.ID_EVALUACION_FK = ev.ID_EVALUACION
            JOIN SECCIONMATERIA_TB sm  ON ev.ID_SECCIONMATERIA_FK = sm.ID_SECCIONMATERIA
            WHERE ev.ID_PERIODO_FK = :idPeriodo
            GROUP BY sm.ID_SECCION_FK
            HAVING AVG(cal.CALIFICACION) < :umbralCritico
        ) alertas) AS totalAlertasCalificacion
    FROM CALIFICACIONES_TB c
    JOIN EVALUACION_TB ev ON c.ID_EVALUACION_FK = ev.ID_EVALUACION
    WHERE ev.ID_PERIODO_FK = :idPeriodo
""", nativeQuery = true)
    KpiGeneralRow obtenerKpiGeneral(
            @Param("idPeriodo") Long idPeriodo,
            @Param("umbralCritico") Double umbralCritico
    );

    // =========================================================
    // CALIFICACIONES POR SECCIÓN
    // =========================================================
    @Query(value = """
        SELECT
            s.NUMERO                         AS seccion,
            p.NOMBRE                         AS periodo,
            ROUND(AVG(c.CALIFICACION), 2)    AS promedio,
            COUNT(DISTINCT m.ID_USUARIO_ESTUDIANTE_FK) AS totalEstudiantes
        FROM CALIFICACIONES_TB c
        JOIN EVALUACION_TB ev         ON c.ID_EVALUACION_FK = ev.ID_EVALUACION
        JOIN PERIODOS_TB p            ON ev.ID_PERIODO_FK = p.ID_PERIODO
        JOIN SECCIONMATERIA_TB sm     ON ev.ID_SECCIONMATERIA_FK = sm.ID_SECCIONMATERIA
        JOIN SECCION_TB s             ON sm.ID_SECCION_FK = s.ID_SECCION
        JOIN MATRICULA_TB m           ON c.ID_MATRICULA_FK = m.ID_MATRICULA
        WHERE ev.ID_PERIODO_FK = :idPeriodo
        GROUP BY s.ID_SECCION, s.NUMERO, p.NOMBRE
        ORDER BY s.NUMERO ASC
    """, nativeQuery = true)
    List<CalificacionPorSeccionRow> calificacionesPorSeccion(@Param("idPeriodo") Long idPeriodo);

    // =========================================================
    // CALIFICACIONES POR MATERIA
    // =========================================================
    @Query(value = """
        SELECT
            mt.NOMBRE                         AS materia,
            ROUND(AVG(c.CALIFICACION), 2)     AS promedio,
            COUNT(c.ID_CALIFICACIONES)        AS totalEvaluaciones
        FROM CALIFICACIONES_TB c
        JOIN EVALUACION_TB ev         ON c.ID_EVALUACION_FK = ev.ID_EVALUACION
        JOIN SECCIONMATERIA_TB sm     ON ev.ID_SECCIONMATERIA_FK = sm.ID_SECCIONMATERIA
        JOIN MATERIA_TB mt            ON sm.ID_MATERIA_FK = mt.ID_MATERIA
        WHERE ev.ID_PERIODO_FK = :idPeriodo
        GROUP BY mt.ID_MATERIA, mt.NOMBRE
        ORDER BY promedio DESC
    """, nativeQuery = true)
    List<CalificacionPorMateriaRow> calificacionesPorMateria(@Param("idPeriodo") Long idPeriodo);

    // =========================================================
    // ASISTENCIA DE ESTUDIANTES DE UN ENCARGADO
    // =========================================================
    @Query(value = """
        SELECT
            u.ID_USUARIO                                             AS idEstudiante,
            CONCAT(u.NOMBRE, ' ', u.PRIMER_APELLIDO)                AS nombreEstudiante,
            s.NUMERO                                                 AS seccion,
            COUNT(a.ID_ASISTENCIA)                                   AS totalClases,
            SUM(CASE WHEN est.DESCRIPCION = 'PRESENTE' THEN 1 ELSE 0 END) AS totalPresente,
            SUM(CASE WHEN est.DESCRIPCION = 'AUSENTE'  THEN 1 ELSE 0 END) AS totalAusente,
            ROUND(
                SUM(CASE WHEN est.DESCRIPCION = 'PRESENTE' THEN 1 ELSE 0 END)
                * 100.0 / NULLIF(COUNT(a.ID_ASISTENCIA), 0)
            , 2)                                                     AS porcentajeAsistencia
        FROM ENCARGADOESTUDIANTE_TB ee
        JOIN USUARIOS_TB u            ON ee.ID_USUARIO_ESTUDIANTE_FK = u.ID_USUARIO
        JOIN MATRICULA_TB m           ON m.ID_USUARIO_ESTUDIANTE_FK = u.ID_USUARIO
        JOIN SECCION_TB s             ON m.ID_SECCION_FK = s.ID_SECCION
        JOIN ASISTENCIAS_TB a         ON a.ID_MATRICULA_FK = m.ID_MATRICULA
        JOIN ESTADOS_TB est           ON a.ID_ESTADO_FK = est.ID_ESTADO
        WHERE ee.ID_USUARIO_ENCARGADO_FK = :idEncargado
          AND ee.ID_ESTADO_FK = (SELECT ID_ESTADO FROM ESTADOS_TB WHERE DESCRIPCION = 'ACTIVO' LIMIT 1)
          AND s.ID_PERIODO_FK = :idPeriodo
        GROUP BY u.ID_USUARIO, u.NOMBRE, u.PRIMER_APELLIDO, s.NUMERO
        ORDER BY u.PRIMER_APELLIDO ASC
    """, nativeQuery = true)
    List<AsistenciaEstudianteRow> asistenciaHijosEncargado(
            @Param("idEncargado") Long idEncargado,
            @Param("idPeriodo") Long idPeriodo
    );

    // =========================================================
    // ALERTAS — secciones con promedio por debajo del umbral
    // =========================================================
    @Query(value = """
        SELECT
            'SECCIÓN'                        AS tipo,
            s.NUMERO                         AS nombre,
            ROUND(AVG(c.CALIFICACION), 2)    AS promedio,
            COUNT(DISTINCT m.ID_USUARIO_ESTUDIANTE_FK) AS totalAfectados
        FROM CALIFICACIONES_TB c
        JOIN EVALUACION_TB ev         ON c.ID_EVALUACION_FK = ev.ID_EVALUACION
        JOIN SECCIONMATERIA_TB sm     ON ev.ID_SECCIONMATERIA_FK = sm.ID_SECCIONMATERIA
        JOIN SECCION_TB s             ON sm.ID_SECCION_FK = s.ID_SECCION
        JOIN MATRICULA_TB m           ON c.ID_MATRICULA_FK = m.ID_MATRICULA
        WHERE ev.ID_PERIODO_FK = :idPeriodo
        GROUP BY s.ID_SECCION, s.NUMERO
        HAVING AVG(c.CALIFICACION) < :umbral

        UNION ALL

        SELECT
            'MATERIA'                        AS tipo,
            mt.NOMBRE                        AS nombre,
            ROUND(AVG(c.CALIFICACION), 2)    AS promedio,
            COUNT(DISTINCT m.ID_USUARIO_ESTUDIANTE_FK) AS totalAfectados
        FROM CALIFICACIONES_TB c
        JOIN EVALUACION_TB ev         ON c.ID_EVALUACION_FK = ev.ID_EVALUACION
        JOIN SECCIONMATERIA_TB sm     ON ev.ID_SECCIONMATERIA_FK = sm.ID_SECCIONMATERIA
        JOIN MATERIA_TB mt            ON sm.ID_MATERIA_FK = mt.ID_MATERIA
        JOIN MATRICULA_TB m           ON c.ID_MATRICULA_FK = m.ID_MATRICULA
        WHERE ev.ID_PERIODO_FK = :idPeriodo
        GROUP BY mt.ID_MATERIA, mt.NOMBRE
        HAVING AVG(c.CALIFICACION) < :umbral

        ORDER BY promedio ASC
    """, nativeQuery = true)
    List<AlertaEstadisticaRow> obtenerAlertas(
            @Param("idPeriodo") Long idPeriodo,
            @Param("umbral") Double umbral
    );
    // =========================================================
// LISTAR SECCIONES DEL PERÍODO (para el dropdown de filtro)
// =========================================================

    interface SeccionRow {

        Long getIdSeccion();

        String getNumero();
    }

    @Query(value = """
    SELECT s.ID_SECCION AS idSeccion, s.NUMERO AS numero
    FROM SECCION_TB s
    WHERE s.ID_PERIODO_FK = :idPeriodo
      AND s.ID_ESTADO_FK = (SELECT ID_ESTADO FROM ESTADOS_TB WHERE DESCRIPCION = 'ACTIVO' LIMIT 1)
    ORDER BY s.NUMERO ASC
""", nativeQuery = true)
    List<SeccionRow> listarSeccionesPorPeriodo(@Param("idPeriodo") Long idPeriodo);

// =========================================================
// CALIFICACIONES POR MATERIA FILTRADAS POR SECCIÓN
// =========================================================
    @Query(value = """
    SELECT
        mt.NOMBRE                         AS materia,
        ROUND(AVG(c.CALIFICACION), 2)     AS promedio,
        COUNT(c.ID_CALIFICACIONES)        AS totalEvaluaciones
    FROM CALIFICACIONES_TB c
    JOIN EVALUACION_TB ev         ON c.ID_EVALUACION_FK = ev.ID_EVALUACION
    JOIN SECCIONMATERIA_TB sm     ON ev.ID_SECCIONMATERIA_FK = sm.ID_SECCIONMATERIA
    JOIN MATERIA_TB mt            ON sm.ID_MATERIA_FK = mt.ID_MATERIA
    WHERE ev.ID_PERIODO_FK = :idPeriodo
      AND sm.ID_SECCION_FK = :idSeccion
    GROUP BY mt.ID_MATERIA, mt.NOMBRE
    ORDER BY promedio DESC
""", nativeQuery = true)
    List<CalificacionPorMateriaRow> calificacionesPorMateriaEnSeccion(
            @Param("idPeriodo") Long idPeriodo,
            @Param("idSeccion") Long idSeccion);

// =========================================================
// ALERTAS FILTRADAS POR SECCIÓN
// =========================================================
    @Query(value = """
    SELECT
        'MATERIA'                        AS tipo,
        mt.NOMBRE                        AS nombre,
        ROUND(AVG(c.CALIFICACION), 2)    AS promedio,
        COUNT(DISTINCT m.ID_USUARIO_ESTUDIANTE_FK) AS totalAfectados
    FROM CALIFICACIONES_TB c
    JOIN EVALUACION_TB ev         ON c.ID_EVALUACION_FK = ev.ID_EVALUACION
    JOIN SECCIONMATERIA_TB sm     ON ev.ID_SECCIONMATERIA_FK = sm.ID_SECCIONMATERIA
    JOIN MATERIA_TB mt            ON sm.ID_MATERIA_FK = mt.ID_MATERIA
    JOIN MATRICULA_TB m           ON c.ID_MATRICULA_FK = m.ID_MATRICULA
    WHERE ev.ID_PERIODO_FK = :idPeriodo
      AND sm.ID_SECCION_FK = :idSeccion
    GROUP BY mt.ID_MATERIA, mt.NOMBRE
    HAVING AVG(c.CALIFICACION) < :umbral
    ORDER BY promedio ASC
""", nativeQuery = true)
    List<AlertaEstadisticaRow> obtenerAlertasEnSeccion(
            @Param("idPeriodo") Long idPeriodo,
            @Param("idSeccion") Long idSeccion,
            @Param("umbral") Double umbral);
}

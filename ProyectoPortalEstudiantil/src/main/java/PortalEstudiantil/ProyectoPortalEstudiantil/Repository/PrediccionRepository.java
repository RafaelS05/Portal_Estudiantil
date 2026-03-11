package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Repository
public class PrediccionRepository {

    private final JdbcTemplate jdbc;

    public PrediccionRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }


    //  ENCARGADO: lista de hijos activos con su matrícula y período

    public List<Map<String, Object>> listarHijosDeEncargado(Long idEncargado) {
        return jdbc.queryForList("""
                SELECT
                    u.ID_USUARIO  AS idUsuario,
                    CONCAT(u.NOMBRE,' ',u.PRIMER_APELLIDO,
                           CASE WHEN u.SEGUNDO_APELLIDO IS NULL OR u.SEGUNDO_APELLIDO=''
                                THEN '' ELSE CONCAT(' ',u.SEGUNDO_APELLIDO) END
                    )             AS nombreEstudiante,
                    m.ID_MATRICULA AS idMatricula,
                    s.NUMERO       AS seccion,
                    p.NOMBRE       AS periodo,
                    p.ID_PERIODO   AS idPeriodo
                FROM ENCARGADOESTUDIANTE_TB ee
                JOIN USUARIOS_TB  u ON u.ID_USUARIO  = ee.ID_USUARIO_ESTUDIANTE_FK
                JOIN MATRICULA_TB m ON m.ID_USUARIO_ESTUDIANTE_FK = u.ID_USUARIO
                                   AND m.ID_ESTADO_FK = 1
                JOIN SECCION_TB   s ON s.ID_SECCION  = m.ID_SECCION_FK
                JOIN PERIODOS_TB  p ON p.ID_PERIODO  = s.ID_PERIODO_FK
                WHERE ee.ID_USUARIO_ENCARGADO_FK = ?
                  AND ee.ID_ESTADO_FK = 1
                  AND u.ID_ESTADO_FK  = 1
                ORDER BY p.FECHA_INICIO DESC, u.PRIMER_APELLIDO
                """, idEncargado);
    }


    //  CALIFICACIONES por materia del estudiante en un período

    public List<Map<String, Object>> calificacionesPorMateria(Long idMatricula, Long idPeriodo) {
        return jdbc.queryForList("""
                SELECT
                    mt.NOMBRE AS materia,
                    ROUND(COALESCE(
                        SUM(cal.CALIFICACION * ev.PORCENTAJE) / NULLIF(SUM(ev.PORCENTAJE), 0),
                        0
                    ), 2) AS promedio
                FROM SECCIONMATERIA_TB sm
                JOIN MATERIA_TB mt ON mt.ID_MATERIA = sm.ID_MATERIA_FK
                LEFT JOIN EVALUACION_TB ev
                       ON ev.ID_SECCIONMATERIA_FK = sm.ID_SECCIONMATERIA
                      AND ev.ID_PERIODO_FK = ?
                      AND ev.ID_ESTADO_FK  = 1
                LEFT JOIN CALIFICACIONES_TB cal
                       ON cal.ID_EVALUACION_FK  = ev.ID_EVALUACION
                      AND cal.ID_MATRICULA_FK   = ?
                      AND cal.ID_ESTADO_FK      = 1
                WHERE sm.ID_SECCION_FK = (
                    SELECT ID_SECCION_FK FROM MATRICULA_TB WHERE ID_MATRICULA = ?
                )
                  AND sm.ID_ESTADO_FK = 1
                GROUP BY mt.ID_MATERIA, mt.NOMBRE
                ORDER BY mt.NOMBRE
                """, idPeriodo, idMatricula, idMatricula);
    }


    //  PROMEDIO GENERAL ponderado en el período

    public BigDecimal promedioGeneral(Long idMatricula, Long idPeriodo) {
        BigDecimal r = jdbc.queryForObject("""
                SELECT ROUND(COALESCE(
                    SUM(cal.CALIFICACION * ev.PORCENTAJE) / NULLIF(SUM(ev.PORCENTAJE), 0), 0
                ), 2)
                FROM CALIFICACIONES_TB cal
                JOIN EVALUACION_TB ev ON ev.ID_EVALUACION = cal.ID_EVALUACION_FK
                                     AND ev.ID_PERIODO_FK = ?
                                     AND ev.ID_ESTADO_FK  = 1
                WHERE cal.ID_MATRICULA_FK = ?
                  AND cal.ID_ESTADO_FK    = 1
                """, BigDecimal.class, idPeriodo, idMatricula);
        return r != null ? r : BigDecimal.ZERO;
    }


    //  ASISTENCIA — resumen del estudiante en el período


    public Map<String, Object> resumenAsistencia(Long idMatricula, Long idPeriodo) {
        return jdbc.queryForMap("""
                SELECT
                    COUNT(*)                                              AS total,
                    SUM(CASE WHEN a.ID_ESTADO_FK = 7  THEN 1 ELSE 0 END) AS presentes,
                    SUM(CASE WHEN a.ID_ESTADO_FK = 8  THEN 1 ELSE 0 END) AS ausentes,
                    SUM(CASE WHEN a.ID_ESTADO_FK = 9  THEN 1 ELSE 0 END) AS tardes,
                    SUM(CASE WHEN a.ID_ESTADO_FK = 10 THEN 1 ELSE 0 END) AS justificados
                FROM ASISTENCIAS_TB a
                JOIN SECCIONMATERIA_TB sm ON sm.ID_SECCIONMATERIA = a.ID_SECCIONMATERIA_FK
                JOIN SECCION_TB        s  ON s.ID_SECCION         = sm.ID_SECCION_FK
                JOIN PERIODOS_TB       p  ON p.ID_PERIODO         = s.ID_PERIODO_FK
                WHERE a.ID_MATRICULA_FK = ?
                  AND p.ID_PERIODO      = ?
                """, idMatricula, idPeriodo);
    }


    //  PREDICCIÓN DOCENTE — todos sus estudiantes en el período

    public List<Map<String, Object>> prediccionDocente(Long idDocente, Long idPeriodo) {
        return jdbc.queryForList("""
                SELECT
                    m.ID_MATRICULA AS idMatricula,
                    u.ID_USUARIO   AS idUsuario,
                    CONCAT(u.NOMBRE,' ',u.PRIMER_APELLIDO,
                           CASE WHEN u.SEGUNDO_APELLIDO IS NULL OR u.SEGUNDO_APELLIDO=''
                                THEN '' ELSE CONCAT(' ',u.SEGUNDO_APELLIDO) END
                    )              AS nombreEstudiante,
                    s.NUMERO       AS seccion,
                    p.NOMBRE       AS periodo,
                    p.ID_PERIODO   AS idPeriodo,
                    ROUND(COALESCE(
                        SUM(cal.CALIFICACION*ev.PORCENTAJE)/NULLIF(SUM(ev.PORCENTAJE),0), 0
                    ),2)           AS promedio,
                    CASE
                        WHEN SUM(ev.PORCENTAJE) IS NULL OR SUM(ev.PORCENTAJE)=0 THEN 'SIN_DATOS'
                        WHEN ROUND(SUM(cal.CALIFICACION*ev.PORCENTAJE)/NULLIF(SUM(ev.PORCENTAJE),0),2)<65 THEN 'ALTO'
                        WHEN ROUND(SUM(cal.CALIFICACION*ev.PORCENTAJE)/NULLIF(SUM(ev.PORCENTAJE),0),2)<75 THEN 'MEDIO'
                        ELSE 'BAJO'
                    END            AS nivelRiesgo,
                    COALESCE(
                        (SELECT CONCAT(ue.NOMBRE,' ',ue.PRIMER_APELLIDO)
                         FROM USUARIOS_TB ue
                         JOIN ENCARGADOESTUDIANTE_TB ee ON ee.ID_USUARIO_ENCARGADO_FK=ue.ID_USUARIO
                         WHERE ee.ID_USUARIO_ESTUDIANTE_FK=u.ID_USUARIO AND ee.ID_ESTADO_FK=1 LIMIT 1),
                        'Sin encargado'
                    )              AS nombreEncargado
                FROM MATRICULA_TB m
                JOIN USUARIOS_TB       u  ON u.ID_USUARIO    = m.ID_USUARIO_ESTUDIANTE_FK
                JOIN SECCION_TB        s  ON s.ID_SECCION    = m.ID_SECCION_FK
                JOIN PERIODOS_TB       p  ON p.ID_PERIODO    = s.ID_PERIODO_FK
                JOIN SECCIONMATERIA_TB sm ON sm.ID_SECCION_FK= s.ID_SECCION AND sm.ID_ESTADO_FK=1
                LEFT JOIN EVALUACION_TB     ev  ON ev.ID_SECCIONMATERIA_FK=sm.ID_SECCIONMATERIA
                                               AND ev.ID_PERIODO_FK=p.ID_PERIODO AND ev.ID_ESTADO_FK=1
                LEFT JOIN CALIFICACIONES_TB cal ON cal.ID_EVALUACION_FK=ev.ID_EVALUACION
                                               AND cal.ID_MATRICULA_FK=m.ID_MATRICULA AND cal.ID_ESTADO_FK=1
                WHERE sm.ID_USUARIO_DOCENTE_FK=?
                  AND p.ID_PERIODO=?
                  AND m.ID_ESTADO_FK=1 AND u.ID_ESTADO_FK=1
                GROUP BY m.ID_MATRICULA, u.ID_USUARIO, u.NOMBRE, u.PRIMER_APELLIDO, u.SEGUNDO_APELLIDO,
                         s.NUMERO, p.NOMBRE, p.ID_PERIODO
                ORDER BY nivelRiesgo DESC, u.PRIMER_APELLIDO
                """, idDocente, idPeriodo);
    }


    //  PREDICCIÓN ADMIN — todas las secciones de un período

    public List<Map<String, Object>> prediccionAdmin(Long idPeriodo) {
        return jdbc.queryForList("""
                SELECT
                    m.ID_MATRICULA AS idMatricula,
                    u.ID_USUARIO   AS idUsuario,
                    CONCAT(u.NOMBRE,' ',u.PRIMER_APELLIDO,
                           CASE WHEN u.SEGUNDO_APELLIDO IS NULL OR u.SEGUNDO_APELLIDO=''
                                THEN '' ELSE CONCAT(' ',u.SEGUNDO_APELLIDO) END
                    )              AS nombreEstudiante,
                    s.NUMERO       AS seccion,
                    p.NOMBRE       AS periodo,
                    p.ID_PERIODO   AS idPeriodo,
                    ROUND(COALESCE(
                        SUM(cal.CALIFICACION*ev.PORCENTAJE)/NULLIF(SUM(ev.PORCENTAJE),0),0
                    ),2)           AS promedio,
                    CASE
                        WHEN SUM(ev.PORCENTAJE) IS NULL OR SUM(ev.PORCENTAJE)=0 THEN 'SIN_DATOS'
                        WHEN ROUND(SUM(cal.CALIFICACION*ev.PORCENTAJE)/NULLIF(SUM(ev.PORCENTAJE),0),2)<65 THEN 'ALTO'
                        WHEN ROUND(SUM(cal.CALIFICACION*ev.PORCENTAJE)/NULLIF(SUM(ev.PORCENTAJE),0),2)<75 THEN 'MEDIO'
                        ELSE 'BAJO'
                    END            AS nivelRiesgo,
                    COALESCE(
                        (SELECT CONCAT(ue.NOMBRE,' ',ue.PRIMER_APELLIDO)
                         FROM USUARIOS_TB ue
                         JOIN ENCARGADOESTUDIANTE_TB ee ON ee.ID_USUARIO_ENCARGADO_FK=ue.ID_USUARIO
                         WHERE ee.ID_USUARIO_ESTUDIANTE_FK=u.ID_USUARIO AND ee.ID_ESTADO_FK=1 LIMIT 1),
                        'Sin encargado'
                    )              AS nombreEncargado
                FROM MATRICULA_TB m
                JOIN USUARIOS_TB   u  ON u.ID_USUARIO =m.ID_USUARIO_ESTUDIANTE_FK
                JOIN SECCION_TB    s  ON s.ID_SECCION =m.ID_SECCION_FK
                JOIN PERIODOS_TB   p  ON p.ID_PERIODO =s.ID_PERIODO_FK
                LEFT JOIN SECCIONMATERIA_TB sm  ON sm.ID_SECCION_FK=s.ID_SECCION AND sm.ID_ESTADO_FK=1
                LEFT JOIN EVALUACION_TB     ev  ON ev.ID_SECCIONMATERIA_FK=sm.ID_SECCIONMATERIA
                                               AND ev.ID_PERIODO_FK=p.ID_PERIODO AND ev.ID_ESTADO_FK=1
                LEFT JOIN CALIFICACIONES_TB cal ON cal.ID_EVALUACION_FK=ev.ID_EVALUACION
                                               AND cal.ID_MATRICULA_FK=m.ID_MATRICULA AND cal.ID_ESTADO_FK=1
                WHERE p.ID_PERIODO=?
                  AND m.ID_ESTADO_FK=1 AND u.ID_ESTADO_FK=1
                GROUP BY m.ID_MATRICULA, u.ID_USUARIO, u.NOMBRE, u.PRIMER_APELLIDO, u.SEGUNDO_APELLIDO,
                         s.NUMERO, p.NOMBRE, p.ID_PERIODO
                ORDER BY nivelRiesgo DESC, s.NUMERO, u.PRIMER_APELLIDO
                """, idPeriodo);
    }


    //  ESTADÍSTICAS por sección 

    public List<Map<String, Object>> estadisticasPorPeriodo(Long idPeriodo) {
        return jdbc.queryForList("""
                SELECT
                    s.NUMERO AS seccion,
                    p.NOMBRE AS periodo,
                    COUNT(DISTINCT m.ID_MATRICULA) AS totalEstudiantes,
                    SUM(CASE WHEN sub.promedio > 0 AND ROUND(sub.promedio,2)<65  THEN 1 ELSE 0 END) AS riesgoAlto,
                    SUM(CASE WHEN ROUND(sub.promedio,2)>=65 AND ROUND(sub.promedio,2)<75 THEN 1 ELSE 0 END) AS riesgoMedio,
                    SUM(CASE WHEN ROUND(sub.promedio,2)>=75 THEN 1 ELSE 0 END) AS riesgoBajo,
                    SUM(CASE WHEN COALESCE(sub.promedio,0)=0 THEN 1 ELSE 0 END) AS sinCalificaciones
                FROM MATRICULA_TB m
                JOIN SECCION_TB  s ON s.ID_SECCION=m.ID_SECCION_FK
                JOIN PERIODOS_TB p ON p.ID_PERIODO=s.ID_PERIODO_FK
                LEFT JOIN (
                    SELECT cal.ID_MATRICULA_FK, ev.ID_PERIODO_FK,
                           COALESCE(SUM(cal.CALIFICACION*ev.PORCENTAJE)/NULLIF(SUM(ev.PORCENTAJE),0),0) AS promedio
                    FROM CALIFICACIONES_TB cal
                    JOIN EVALUACION_TB ev ON ev.ID_EVALUACION=cal.ID_EVALUACION_FK AND ev.ID_ESTADO_FK=1
                    WHERE cal.ID_ESTADO_FK=1
                    GROUP BY cal.ID_MATRICULA_FK, ev.ID_PERIODO_FK
                ) sub ON sub.ID_MATRICULA_FK=m.ID_MATRICULA AND sub.ID_PERIODO_FK=p.ID_PERIODO
                WHERE p.ID_PERIODO=? AND m.ID_ESTADO_FK=1
                GROUP BY s.ID_SECCION, s.NUMERO, p.NOMBRE
                ORDER BY s.NUMERO
                """, idPeriodo);
    }
}
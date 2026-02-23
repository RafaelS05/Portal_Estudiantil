package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.ReporteAcademico;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ReporteAcademicoRepository extends JpaRepository<ReporteAcademico, Long> {

    // ─── Listado principal ───────────────────────────────────────────────────

    interface ReporteListadoRow {
        Long       getIdReporte();
        String     getNombreEstudiante();
        String     getNombreSeccion();
        String     getNombrePeriodo();
        String     getNombreTipoReporte();
        BigDecimal getPromedioPonderado();
        LocalDate  getFechaCreacionReporte();
    }

    @Query(value = """
        SELECT
            r.ID_REPORTE              AS idReporte,
            CONCAT(u.NOMBRE,' ',u.PRIMER_APELLIDO,
                CASE WHEN u.SEGUNDO_APELLIDO IS NULL OR u.SEGUNDO_APELLIDO = ''
                     THEN '' ELSE CONCAT(' ',u.SEGUNDO_APELLIDO) END
            )                         AS nombreEstudiante,
            s.NUMERO                  AS nombreSeccion,
            p.NOMBRE                  AS nombrePeriodo,
            tr.NOMBRE                 AS nombreTipoReporte,
            r.PROMEDIO_PONDERADO      AS promedioPonderado,
            r.FECHA_CREACION_REPORTE  AS fechaCreacionReporte
        FROM  REPORTESACADEMICOS_TB r
        JOIN  MATRICULA_TB   m  ON m.ID_MATRICULA    = r.ID_MATRICULA_FK
        JOIN  USUARIOS_TB    u  ON u.ID_USUARIO      = m.ID_USUARIO_ESTUDIANTE_FK
        JOIN  SECCION_TB     s  ON s.ID_SECCION      = m.ID_SECCION_FK
        JOIN  PERIODOS_TB    p  ON p.ID_PERIODO      = r.ID_PERIODO_FK
        JOIN  TIPOREPORTE_TB tr ON tr.ID_TIPOREPORTE = r.ID_TIPOREPORTE_FK
        WHERE r.ID_ESTADO_FK = 1
        ORDER BY r.ID_REPORTE DESC
    """, nativeQuery = true)
    List<ReporteListadoRow> listarReportesActivos();

    @Query(value = """
        SELECT
            r.ID_REPORTE              AS idReporte,
            CONCAT(u.NOMBRE,' ',u.PRIMER_APELLIDO,
                CASE WHEN u.SEGUNDO_APELLIDO IS NULL OR u.SEGUNDO_APELLIDO = ''
                     THEN '' ELSE CONCAT(' ',u.SEGUNDO_APELLIDO) END
            )                         AS nombreEstudiante,
            s.NUMERO                  AS nombreSeccion,
            p.NOMBRE                  AS nombrePeriodo,
            tr.NOMBRE                 AS nombreTipoReporte,
            r.PROMEDIO_PONDERADO      AS promedioPonderado,
            r.FECHA_CREACION_REPORTE  AS fechaCreacionReporte
        FROM  REPORTESACADEMICOS_TB r
        JOIN  MATRICULA_TB   m  ON m.ID_MATRICULA    = r.ID_MATRICULA_FK
        JOIN  USUARIOS_TB    u  ON u.ID_USUARIO      = m.ID_USUARIO_ESTUDIANTE_FK
        JOIN  SECCION_TB     s  ON s.ID_SECCION      = m.ID_SECCION_FK
        JOIN  PERIODOS_TB    p  ON p.ID_PERIODO      = r.ID_PERIODO_FK
        JOIN  TIPOREPORTE_TB tr ON tr.ID_TIPOREPORTE = r.ID_TIPOREPORTE_FK
        WHERE r.ID_ESTADO_FK = 1
          AND (
              LOWER(u.NOMBRE)           LIKE LOWER(CONCAT('%',:b,'%')) OR
              LOWER(u.PRIMER_APELLIDO)  LIKE LOWER(CONCAT('%',:b,'%')) OR
              LOWER(u.SEGUNDO_APELLIDO) LIKE LOWER(CONCAT('%',:b,'%')) OR
              LOWER(s.NUMERO)           LIKE LOWER(CONCAT('%',:b,'%')) OR
              LOWER(p.NOMBRE)           LIKE LOWER(CONCAT('%',:b,'%'))
          )
        ORDER BY r.ID_REPORTE DESC
    """, nativeQuery = true)
    List<ReporteListadoRow> buscarReportes(@Param("b") String busqueda);

    // ─── Detalle con calificaciones ──────────────────────────────────────────

    interface ReporteDetalleRow {
        Long       getIdReporte();
        String     getNombreEstudiante();
        String     getNombreSeccion();
        String     getNombrePeriodo();
        String     getNombreTipoReporte();
        BigDecimal getPromedioPonderado();
        LocalDate  getFechaCreacionReporte();
        String     getNombreMateria();
        String     getTipoEvaluacion();
        BigDecimal getPorcentajeEvaluacion();
        BigDecimal getCalificacion();
        BigDecimal getAporte();
    }

    @Query(value = """
        SELECT
            r.ID_REPORTE              AS idReporte,
            CONCAT(u.NOMBRE,' ',u.PRIMER_APELLIDO,
                CASE WHEN u.SEGUNDO_APELLIDO IS NULL OR u.SEGUNDO_APELLIDO = ''
                     THEN '' ELSE CONCAT(' ',u.SEGUNDO_APELLIDO) END
            )                         AS nombreEstudiante,
            s.NUMERO                  AS nombreSeccion,
            p.NOMBRE                  AS nombrePeriodo,
            tr.NOMBRE                 AS nombreTipoReporte,
            r.PROMEDIO_PONDERADO      AS promedioPonderado,
            r.FECHA_CREACION_REPORTE  AS fechaCreacionReporte,
            mat.NOMBRE                AS nombreMateria,
            ev.TIPO                   AS tipoEvaluacion,
            ev.PORCENTAJE             AS porcentajeEvaluacion,
            cal.CALIFICACION          AS calificacion,
            ROUND(cal.CALIFICACION * ev.PORCENTAJE / 100, 2) AS aporte
        FROM  REPORTESACADEMICOS_TB r
        JOIN  MATRICULA_TB       m   ON m.ID_MATRICULA       = r.ID_MATRICULA_FK
        JOIN  USUARIOS_TB        u   ON u.ID_USUARIO         = m.ID_USUARIO_ESTUDIANTE_FK
        JOIN  SECCION_TB         s   ON s.ID_SECCION         = m.ID_SECCION_FK
        JOIN  PERIODOS_TB        p   ON p.ID_PERIODO         = r.ID_PERIODO_FK
        JOIN  TIPOREPORTE_TB     tr  ON tr.ID_TIPOREPORTE    = r.ID_TIPOREPORTE_FK
        LEFT JOIN CALIFICACIONES_TB cal ON cal.ID_MATRICULA_FK   = m.ID_MATRICULA
                                       AND cal.ID_ESTADO_FK = 1
        LEFT JOIN EVALUACION_TB  ev  ON ev.ID_EVALUACION        = cal.ID_EVALUACION_FK
                                    AND ev.ID_PERIODO_FK         = r.ID_PERIODO_FK
                                    AND ev.ID_ESTADO_FK = 1
        LEFT JOIN SECCIONMATERIA_TB sm ON sm.ID_SECCIONMATERIA   = ev.ID_SECCIONMATERIA_FK
        LEFT JOIN MATERIA_TB     mat ON mat.ID_MATERIA           = sm.ID_MATERIA_FK
        WHERE r.ID_REPORTE = :idReporte
        ORDER BY mat.NOMBRE, ev.TIPO
    """, nativeQuery = true)
    List<ReporteDetalleRow> obtenerDetalleReporte(@Param("idReporte") Long idReporte);

    // ─── Estudiantes disponibles para generar reporte ────────────────────────

    interface EstudianteDisponibleRow {
        Long   getIdMatricula();
        Long   getIdUsuario();
        String getNombreEstudiante();
        String getNombreSeccion();
        Long   getIdPeriodo();
        String getNombrePeriodo();
    }

    @Query(value = """
        SELECT
            m.ID_MATRICULA   AS idMatricula,
            u.ID_USUARIO     AS idUsuario,
            CONCAT(u.NOMBRE,' ',u.PRIMER_APELLIDO,
                CASE WHEN u.SEGUNDO_APELLIDO IS NULL OR u.SEGUNDO_APELLIDO = ''
                     THEN '' ELSE CONCAT(' ',u.SEGUNDO_APELLIDO) END
            )                AS nombreEstudiante,
            s.NUMERO         AS nombreSeccion,
            p.ID_PERIODO     AS idPeriodo,
            p.NOMBRE         AS nombrePeriodo
        FROM  MATRICULA_TB m
        JOIN  USUARIOS_TB u ON u.ID_USUARIO = m.ID_USUARIO_ESTUDIANTE_FK
        JOIN  SECCION_TB  s ON s.ID_SECCION = m.ID_SECCION_FK
        JOIN  PERIODOS_TB p ON p.ID_PERIODO = s.ID_PERIODO_FK
        WHERE m.ID_ESTADO_FK      = 1
          AND u.ID_TIPOUSUARIO_FK = 3
          AND u.ID_ESTADO_FK      = 1
          AND (
              LOWER(u.NOMBRE)           LIKE LOWER(CONCAT('%',:b,'%')) OR
              LOWER(u.PRIMER_APELLIDO)  LIKE LOWER(CONCAT('%',:b,'%')) OR
              LOWER(u.SEGUNDO_APELLIDO) LIKE LOWER(CONCAT('%',:b,'%'))
          )
        ORDER BY p.NOMBRE, u.NOMBRE
    """, nativeQuery = true)
    List<EstudianteDisponibleRow> buscarEstudiantesParaReporte(@Param("b") String busqueda);

    // ─── CRUD stored procedures ──────────────────────────────────────────────

    @Modifying
    @Transactional
    @Query(value = """
        CALL REPORTESACADEMICOS_INSERTAR(
            :fechaCreacion, :promedio, :idTipoReporte,
            :idGeneradoPor, :idMatricula, :idPeriodo, :idEstado)
    """, nativeQuery = true)
    void insertarReporte(
            @Param("fechaCreacion")  LocalDate  fechaCreacion,
            @Param("promedio")       BigDecimal promedio,
            @Param("idTipoReporte")  Long       idTipoReporte,
            @Param("idGeneradoPor")  Long       idGeneradoPor,
            @Param("idMatricula")    Long       idMatricula,
            @Param("idPeriodo")      Long       idPeriodo,
            @Param("idEstado")       Long       idEstado
    );

    @Modifying
    @Transactional
    @Query(value = """
        CALL REPORTESACADEMICOS_MODIFICAR(
            :idReporte, :fechaCreacion, :promedio,
            :idTipoReporte, :idGeneradoPor, :idMatricula, :idPeriodo)
    """, nativeQuery = true)
    void modificarReporte(
            @Param("idReporte")      Long       idReporte,
            @Param("fechaCreacion")  LocalDate  fechaCreacion,
            @Param("promedio")       BigDecimal promedio,
            @Param("idTipoReporte")  Long       idTipoReporte,
            @Param("idGeneradoPor")  Long       idGeneradoPor,
            @Param("idMatricula")    Long       idMatricula,
            @Param("idPeriodo")      Long       idPeriodo
    );

    @Modifying
    @Transactional
    @Query(value = "CALL REPORTESACADEMICOS_CAMBIAR_ESTADO(:idReporte, :idEstado)",
           nativeQuery = true)
    void cambiarEstadoReporte(
            @Param("idReporte") Long idReporte,
            @Param("idEstado")  Long idEstado
    );
}
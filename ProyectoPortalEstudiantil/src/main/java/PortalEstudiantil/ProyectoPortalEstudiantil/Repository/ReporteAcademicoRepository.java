package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.ReporteAcademico;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;

@Repository
public interface ReporteAcademicoRepository extends JpaRepository<ReporteAcademico, Long> {

    // ============================================================
    //  PROYECCIONES
    // ============================================================
    interface ReporteListadoRow {

        Long getIdReporte();

        String getNombreEstudiante();

        String getNombreSeccion();

        String getNombrePeriodo();

        String getNombreTipoReporte();

        BigDecimal getPromedioPonderado();

        LocalDate getFechaCreacionReporte();
    }

    interface ReporteDetalleRow {

        Long getIdReporte();

        String getNombreEstudiante();

        String getNombreSeccion();

        String getNombrePeriodo();

        String getNombreTipoReporte();

        BigDecimal getPromedioPonderado();

        LocalDate getFechaCreacionReporte();

        // Detalle por materia
        String getNombreMateria();

        String getTipoEvaluacion();

        BigDecimal getPorcentajeEvaluacion();

        BigDecimal getCalificacion();

        BigDecimal getAporte();
    }

    /**
     * Proyección para el buscador de estudiantes. Incluye correo para
     * identificar al estudiante si hay nombres duplicados. El promedio se
     * calcula directo en la query ponderando calificaciones × porcentaje.
     */
    interface EstudianteDisponibleRow {

        Long getIdMatricula();

        Long getIdPeriodo();

        Long getIdUsuario();

        String getNombreEstudiante();

        String getNombreSeccion();

        String getNombrePeriodo();

        String getCorreo();

        BigDecimal getPromedio();   // calculado: SUM(cal * pct) / SUM(pct)
    }

    // ============================================================
    //  LISTADO Y BÚSQUEDA DE REPORTES
    // ============================================================
    @Query(value = """
        SELECT
            r.ID_REPORTE           AS idReporte,
            CONCAT(u.NOMBRE, ' ', u.PRIMER_APELLIDO,
                   CASE WHEN u.SEGUNDO_APELLIDO IS NULL OR u.SEGUNDO_APELLIDO = ''
                        THEN '' ELSE CONCAT(' ', u.SEGUNDO_APELLIDO) END
            )                               AS nombreEstudiante,
            s.NUMERO                        AS nombreSeccion,
            p.NOMBRE                        AS nombrePeriodo,
            t.NOMBRE                        AS nombreTipoReporte,
            r.PROMEDIO_PONDERADO            AS promedioPonderado,
            r.FECHA_CREACION_REPORTE        AS fechaCreacionReporte
        FROM REPORTESACADEMICOS_TB r
        JOIN MATRICULA_TB   m ON m.ID_MATRICULA  = r.ID_MATRICULA_FK
        JOIN USUARIOS_TB    u ON u.ID_USUARIO    = m.ID_USUARIO_ESTUDIANTE_FK
        JOIN SECCION_TB     s ON s.ID_SECCION    = m.ID_SECCION_FK
        JOIN PERIODOS_TB    p ON p.ID_PERIODO    = r.ID_PERIODO_FK
        JOIN TIPOREPORTE_TB t ON t.ID_TIPOREPORTE = r.ID_TIPOREPORTE_FK
        WHERE r.ID_ESTADO_FK = 1
        ORDER BY r.ID_REPORTE DESC
    """, nativeQuery = true)
    List<ReporteListadoRow> listarReportesActivos();

    @Query(value = """
        SELECT
            r.ID_REPORTE           AS idReporte,
            CONCAT(u.NOMBRE, ' ', u.PRIMER_APELLIDO,
                   CASE WHEN u.SEGUNDO_APELLIDO IS NULL OR u.SEGUNDO_APELLIDO = ''
                        THEN '' ELSE CONCAT(' ', u.SEGUNDO_APELLIDO) END
            )                               AS nombreEstudiante,
            s.NUMERO                        AS nombreSeccion,
            p.NOMBRE                        AS nombrePeriodo,
            t.NOMBRE                        AS nombreTipoReporte,
            r.PROMEDIO_PONDERADO            AS promedioPonderado,
            r.FECHA_CREACION_REPORTE        AS fechaCreacionReporte
        FROM REPORTESACADEMICOS_TB r
        JOIN MATRICULA_TB   m ON m.ID_MATRICULA  = r.ID_MATRICULA_FK
        JOIN USUARIOS_TB    u ON u.ID_USUARIO    = m.ID_USUARIO_ESTUDIANTE_FK
        JOIN SECCION_TB     s ON s.ID_SECCION    = m.ID_SECCION_FK
        JOIN PERIODOS_TB    p ON p.ID_PERIODO    = r.ID_PERIODO_FK
        JOIN TIPOREPORTE_TB t ON t.ID_TIPOREPORTE = r.ID_TIPOREPORTE_FK
        WHERE r.ID_ESTADO_FK = 1
          AND (
               u.NOMBRE          LIKE CONCAT('%', :busqueda, '%')
            OR u.PRIMER_APELLIDO LIKE CONCAT('%', :busqueda, '%')
            OR u.SEGUNDO_APELLIDO LIKE CONCAT('%', :busqueda, '%')
            OR s.NUMERO          LIKE CONCAT('%', :busqueda, '%')
            OR p.NOMBRE          LIKE CONCAT('%', :busqueda, '%')
          )
        ORDER BY r.ID_REPORTE DESC
    """, nativeQuery = true)
    List<ReporteListadoRow> buscarReportes(@Param("busqueda") String busqueda);

    // ============================================================
    //  DETALLE DE UN REPORTE
    // ============================================================
    @Query(value = """
        SELECT
            r.ID_REPORTE           AS idReporte,
            CONCAT(u.NOMBRE, ' ', u.PRIMER_APELLIDO,
                   CASE WHEN u.SEGUNDO_APELLIDO IS NULL OR u.SEGUNDO_APELLIDO = ''
                        THEN '' ELSE CONCAT(' ', u.SEGUNDO_APELLIDO) END
            )                               AS nombreEstudiante,
            s.NUMERO                        AS nombreSeccion,
            p.NOMBRE                        AS nombrePeriodo,
            t.NOMBRE                        AS nombreTipoReporte,
            r.PROMEDIO_PONDERADO            AS promedioPonderado,
            r.FECHA_CREACION_REPORTE        AS fechaCreacionReporte,
            ma.NOMBRE                       AS nombreMateria,
            ev.TIPO                         AS tipoEvaluacion,
            ev.PORCENTAJE                   AS porcentajeEvaluacion,
            cal.CALIFICACION                AS calificacion,
            ROUND(cal.CALIFICACION * ev.PORCENTAJE / 100, 2) AS aporte
        FROM REPORTESACADEMICOS_TB r
        JOIN MATRICULA_TB   m  ON m.ID_MATRICULA   = r.ID_MATRICULA_FK
        JOIN USUARIOS_TB    u  ON u.ID_USUARIO     = m.ID_USUARIO_ESTUDIANTE_FK
        JOIN SECCION_TB     s  ON s.ID_SECCION     = m.ID_SECCION_FK
        JOIN PERIODOS_TB    p  ON p.ID_PERIODO     = r.ID_PERIODO_FK
        JOIN TIPOREPORTE_TB t  ON t.ID_TIPOREPORTE = r.ID_TIPOREPORTE_FK
        LEFT JOIN CALIFICACIONES_TB cal ON cal.ID_MATRICULA_FK = m.ID_MATRICULA
                                       AND cal.ID_ESTADO_FK = 1
        LEFT JOIN EVALUACION_TB ev  ON ev.ID_EVALUACION = cal.ID_EVALUACION_FK
                                    AND ev.ID_PERIODO_FK = r.ID_PERIODO_FK
        LEFT JOIN SECCIONMATERIA_TB sm ON sm.ID_SECCIONMATERIA = ev.ID_SECCIONMATERIA_FK
        LEFT JOIN MATERIA_TB ma        ON ma.ID_MATERIA = sm.ID_MATERIA_FK
        WHERE r.ID_REPORTE = :idReporte
        ORDER BY ma.NOMBRE, ev.TIPO
    """, nativeQuery = true)
    List<ReporteDetalleRow> obtenerDetalleReporte(@Param("idReporte") Long idReporte);

    // ============================================================
    //  BÚSQUEDA DE ESTUDIANTES PARA CREAR REPORTE
    //  - Busca por nombre, primer apellido o segundo apellido
    //  - Incluye correo de login para identificar al estudiante
    //  - Calcula promedio ponderado de las calificaciones ya registradas
    // ============================================================
    @Query(value = """
        SELECT
            m.ID_MATRICULA                  AS idMatricula,
            p.ID_PERIODO                    AS idPeriodo,
            u.ID_USUARIO                    AS idUsuario,
            CONCAT(u.NOMBRE, ' ', u.PRIMER_APELLIDO,
                   CASE WHEN u.SEGUNDO_APELLIDO IS NULL OR u.SEGUNDO_APELLIDO = ''
                        THEN '' ELSE CONCAT(' ', u.SEGUNDO_APELLIDO) END
            )                               AS nombreEstudiante,
            s.NUMERO                        AS nombreSeccion,
            p.NOMBRE                        AS nombrePeriodo,
            COALESCE(
                (SELECT co.CORREO FROM CORREO_TB co
                 WHERE co.ID_USUARIO_FK = u.ID_USUARIO
                   AND co.ES_LOGIN = 'S'
                 LIMIT 1),
                'Sin correo'
            )                               AS correo,
            ROUND(
                COALESCE(
                    SUM(cal.CALIFICACION * ev.PORCENTAJE) / NULLIF(SUM(ev.PORCENTAJE), 0),
                    0
                ), 2
            )                               AS promedio
        FROM MATRICULA_TB m
        JOIN USUARIOS_TB u  ON u.ID_USUARIO  = m.ID_USUARIO_ESTUDIANTE_FK
        JOIN SECCION_TB  s  ON s.ID_SECCION  = m.ID_SECCION_FK
        JOIN PERIODOS_TB p  ON p.ID_PERIODO  = s.ID_PERIODO_FK
        LEFT JOIN CALIFICACIONES_TB cal ON cal.ID_MATRICULA_FK = m.ID_MATRICULA
        LEFT JOIN EVALUACION_TB ev  ON ev.ID_EVALUACION = cal.ID_EVALUACION_FK
                                    AND ev.ID_PERIODO_FK = p.ID_PERIODO
        WHERE u.ID_TIPOUSUARIO_FK = 3
          AND u.ID_ESTADO_FK  = 1
          AND m.ID_ESTADO_FK  = 1
          AND (
               u.NOMBRE           LIKE CONCAT('%', :busqueda, '%')
            OR u.PRIMER_APELLIDO  LIKE CONCAT('%', :busqueda, '%')
            OR u.SEGUNDO_APELLIDO LIKE CONCAT('%', :busqueda, '%')
          )
        GROUP BY
            m.ID_MATRICULA, p.ID_PERIODO, u.ID_USUARIO,
            u.NOMBRE, u.PRIMER_APELLIDO, u.SEGUNDO_APELLIDO,
            s.NUMERO, p.NOMBRE
        ORDER BY u.PRIMER_APELLIDO, u.NOMBRE
    """, nativeQuery = true)
    List<EstudianteDisponibleRow> buscarEstudiantesParaReporte(@Param("busqueda") String busqueda);

    // ============================================================
    //  STORED PROCEDURES
    // ============================================================
        @Modifying
        @Transactional
        @Query(value = """
        CALL REPORTESACADEMICOS_INSERTAR(
            :fechaCreacion,
            :promedioPonderado,
            :idTipoReporte,
            :idGeneradoPor,
            :idMatricula,
            :idPeriodo,
            :idEstado
        )
    """, nativeQuery = true)
        void insertarReporte(
                @Param("fechaCreacion") LocalDate fechaCreacion,
                @Param("promedioPonderado") BigDecimal promedioPonderado,
                @Param("idTipoReporte") Long idTipoReporte,
                @Param("idGeneradoPor") Long idGeneradoPor,
                @Param("idMatricula") Long idMatricula,
                @Param("idPeriodo") Long idPeriodo,
                @Param("idEstado") Long idEstado
        );

        @Modifying
        @Transactional
        @Query(value = """
        CALL REPORTESACADEMICOS_MODIFICAR(
            :idReporte,
            :fechaCreacion,
            :promedioPonderado,
            :idTipoReporte,
            :idGeneradoPor,
            :idMatricula,
            :idPeriodo
        )
    """, nativeQuery = true)
        void modificarReporte(
                @Param("idReporte") Long idReporte,
                @Param("fechaCreacion") LocalDate fechaCreacion,
                @Param("promedioPonderado") BigDecimal promedioPonderado,
                @Param("idTipoReporte") Long idTipoReporte,
                @Param("idGeneradoPor") Long idGeneradoPor,
                @Param("idMatricula") Long idMatricula,
                @Param("idPeriodo") Long idPeriodo
        );

        @Modifying
        @Transactional
        @Query(value = """
        CALL REPORTESACADEMICOS_CAMBIAR_ESTADO(
            :idReporte,
            :idEstado
        )
    """, nativeQuery = true)
        void cambiarEstadoReporte(
                @Param("idReporte") Long idReporte,
                @Param("idEstado") Long idEstado
    );
}

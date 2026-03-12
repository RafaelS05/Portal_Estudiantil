 package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.FeedBack;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.FeedBackResumen;
import java.time.LocalDate;

public interface FeedBackRepository extends JpaRepository<FeedBack, Long> {
   FeedBack findByIdFeedback(Integer idFeedback);
    long countByIdEstadoFk(Integer idEstadoFk);

    @Query(value = """
        SELECT
            f.ID_FEEDBACK          AS idFeedback,
            f.CALIFICACION         AS calificacion,
            f.COMENTARIO           AS comentario,
            f.FECHA_EVALUACION     AS fechaEvaluacion,
            CONCAT(ue.NOMBRE, ' ', ue.PRIMER_APELLIDO)  AS nombreEstudiante,
            CONCAT(ud.NOMBRE, ' ', ud.PRIMER_APELLIDO)  AS nombreDocente,
            m.NOMBRE               AS nombreMateria,
            s.NUMERO               AS nombreSeccion,
            f.ID_SECCIONMATERIA_FK AS idSeccionmateria,
            f.ID_MATRICULA_FK      AS idMatricula,
            f.ID_ESTADO_FK         AS idEstado
        FROM FEEDBACK360_TB       f
        JOIN SECCIONMATERIA_TB   sm ON sm.ID_SECCIONMATERIA      = f.ID_SECCIONMATERIA_FK
        JOIN MATERIA_TB           m ON m.ID_MATERIA               = sm.ID_MATERIA_FK
        JOIN SECCION_TB           s ON s.ID_SECCION               = sm.ID_SECCION_FK
        JOIN USUARIOS_TB         ud ON ud.ID_USUARIO              = sm.ID_USUARIO_DOCENTE_FK
        JOIN MATRICULA_TB        mt ON mt.ID_MATRICULA            = f.ID_MATRICULA_FK
        JOIN USUARIOS_TB         ue ON ue.ID_USUARIO              = mt.ID_USUARIO_ESTUDIANTE_FK
        WHERE f.ID_ESTADO_FK = 1
        ORDER BY f.FECHA_EVALUACION DESC
    """, nativeQuery = true)
    List<FeedBackResumen> listarTodos();

    @Query(value = """
        SELECT
            f.ID_FEEDBACK          AS idFeedback,
            f.CALIFICACION         AS calificacion,
            f.COMENTARIO           AS comentario,
            f.FECHA_EVALUACION     AS fechaEvaluacion,
            CONCAT(ue.NOMBRE, ' ', ue.PRIMER_APELLIDO)  AS nombreEstudiante,
            CONCAT(ud.NOMBRE, ' ', ud.PRIMER_APELLIDO)  AS nombreDocente,
            m.NOMBRE               AS nombreMateria,
            s.NUMERO               AS nombreSeccion,
            f.ID_SECCIONMATERIA_FK AS idSeccionmateria,
            f.ID_MATRICULA_FK      AS idMatricula,
            f.ID_ESTADO_FK         AS idEstado
        FROM FEEDBACK360_TB       f
        JOIN SECCIONMATERIA_TB   sm ON sm.ID_SECCIONMATERIA      = f.ID_SECCIONMATERIA_FK
        JOIN MATERIA_TB           m ON m.ID_MATERIA               = sm.ID_MATERIA_FK
        JOIN SECCION_TB           s ON s.ID_SECCION               = sm.ID_SECCION_FK
        JOIN USUARIOS_TB         ud ON ud.ID_USUARIO              = sm.ID_USUARIO_DOCENTE_FK
        JOIN MATRICULA_TB        mt ON mt.ID_MATRICULA            = f.ID_MATRICULA_FK
        JOIN USUARIOS_TB         ue ON ue.ID_USUARIO              = mt.ID_USUARIO_ESTUDIANTE_FK
        WHERE sm.ID_USUARIO_DOCENTE_FK = :idDocente
          AND f.ID_ESTADO_FK = 1
        ORDER BY s.NUMERO, m.NOMBRE, ue.PRIMER_APELLIDO
    """, nativeQuery = true)
    List<FeedBackResumen> listarPorDocente(@Param("idDocente") Long idDocente);

    @Query(value = """
        SELECT
            f.ID_FEEDBACK                                        AS idFeedback,
            f.CALIFICACION                                       AS calificacion,
            f.COMENTARIO                                         AS comentario,
            f.FECHA_EVALUACION                                   AS fechaEvaluacion,
            CONCAT(ue.NOMBRE, ' ', ue.PRIMER_APELLIDO)          AS nombreEstudiante,
            CONCAT(ud.NOMBRE, ' ', ud.PRIMER_APELLIDO)          AS nombreDocente,
            m.NOMBRE                                             AS nombreMateria,
            s.NUMERO                                             AS nombreSeccion,
            :idSeccionmateria                                    AS idSeccionmateria,
            mt.ID_MATRICULA                                      AS idMatricula,
            IFNULL(f.ID_ESTADO_FK, 1)                           AS idEstado
        FROM MATRICULA_TB         mt
        JOIN SECCION_TB            s ON s.ID_SECCION              = mt.ID_SECCION_FK
        JOIN SECCIONMATERIA_TB    sm ON sm.ID_SECCION_FK           = s.ID_SECCION
                                    AND sm.ID_SECCIONMATERIA        = :idSeccionmateria
        JOIN MATERIA_TB            m ON m.ID_MATERIA               = sm.ID_MATERIA_FK
        JOIN USUARIOS_TB          ud ON ud.ID_USUARIO              = sm.ID_USUARIO_DOCENTE_FK
        JOIN USUARIOS_TB          ue ON ue.ID_USUARIO              = mt.ID_USUARIO_ESTUDIANTE_FK
        LEFT JOIN FEEDBACK360_TB   f ON f.ID_MATRICULA_FK          = mt.ID_MATRICULA
                                    AND f.ID_SECCIONMATERIA_FK      = :idSeccionmateria
                                    AND f.ID_ESTADO_FK = 1
        WHERE mt.ID_ESTADO_FK = 1
          AND ue.ID_ESTADO_FK = 1
        ORDER BY ue.PRIMER_APELLIDO, ue.NOMBRE
    """, nativeQuery = true)
    List<FeedBackResumen> listarEstudiantesConFeedback(
            @Param("idSeccionmateria") Integer idSeccionmateria);

    @Query(value = """
        SELECT
            f.ID_FEEDBACK          AS idFeedback,
            f.CALIFICACION         AS calificacion,
            f.COMENTARIO           AS comentario,
            f.FECHA_EVALUACION     AS fechaEvaluacion,
            CONCAT(ue.NOMBRE, ' ', ue.PRIMER_APELLIDO)  AS nombreEstudiante,
            CONCAT(ud.NOMBRE, ' ', ud.PRIMER_APELLIDO)  AS nombreDocente,
            m.NOMBRE               AS nombreMateria,
            s.NUMERO               AS nombreSeccion,
            f.ID_SECCIONMATERIA_FK AS idSeccionmateria,
            f.ID_MATRICULA_FK      AS idMatricula,
            f.ID_ESTADO_FK         AS idEstado
        FROM FEEDBACK360_TB       f
        JOIN SECCIONMATERIA_TB   sm ON sm.ID_SECCIONMATERIA      = f.ID_SECCIONMATERIA_FK
        JOIN MATERIA_TB           m ON m.ID_MATERIA               = sm.ID_MATERIA_FK
        JOIN SECCION_TB           s ON s.ID_SECCION               = sm.ID_SECCION_FK
        JOIN USUARIOS_TB         ud ON ud.ID_USUARIO              = sm.ID_USUARIO_DOCENTE_FK
        JOIN MATRICULA_TB        mt ON mt.ID_MATRICULA            = f.ID_MATRICULA_FK
        JOIN USUARIOS_TB         ue ON ue.ID_USUARIO              = mt.ID_USUARIO_ESTUDIANTE_FK
        WHERE mt.ID_USUARIO_ESTUDIANTE_FK = :idEstudiante
          AND f.ID_ESTADO_FK = 1
        ORDER BY f.FECHA_EVALUACION DESC
    """, nativeQuery = true)
    List<FeedBackResumen> listarPorEstudiante(
            @Param("idEstudiante") Long idEstudiante);

    @Query(value = """
        SELECT
            f.ID_FEEDBACK          AS idFeedback,
            f.CALIFICACION         AS calificacion,
            f.COMENTARIO           AS comentario,
            f.FECHA_EVALUACION     AS fechaEvaluacion,
            CONCAT(ue.NOMBRE, ' ', ue.PRIMER_APELLIDO)  AS nombreEstudiante,
            CONCAT(ud.NOMBRE, ' ', ud.PRIMER_APELLIDO)  AS nombreDocente,
            m.NOMBRE               AS nombreMateria,
            s.NUMERO               AS nombreSeccion,
            f.ID_SECCIONMATERIA_FK AS idSeccionmateria,
            f.ID_MATRICULA_FK      AS idMatricula,
            f.ID_ESTADO_FK         AS idEstado
        FROM FEEDBACK360_TB       f
        JOIN SECCIONMATERIA_TB   sm ON sm.ID_SECCIONMATERIA      = f.ID_SECCIONMATERIA_FK
        JOIN MATERIA_TB           m ON m.ID_MATERIA               = sm.ID_MATERIA_FK
        JOIN SECCION_TB           s ON s.ID_SECCION               = sm.ID_SECCION_FK
        JOIN USUARIOS_TB         ud ON ud.ID_USUARIO              = sm.ID_USUARIO_DOCENTE_FK
        JOIN MATRICULA_TB        mt ON mt.ID_MATRICULA            = f.ID_MATRICULA_FK
        JOIN USUARIOS_TB         ue ON ue.ID_USUARIO              = mt.ID_USUARIO_ESTUDIANTE_FK
        WHERE mt.ID_USUARIO_ESTUDIANTE_FK = :idEstudiante
          AND sm.ID_MATERIA_FK            = :idMateria
          AND f.ID_ESTADO_FK = 1
        ORDER BY f.FECHA_EVALUACION DESC
    """, nativeQuery = true)
    List<FeedBackResumen> listarPorEstudianteYMateria(
            @Param("idEstudiante") Long idEstudiante,
            @Param("idMateria")    Integer idMateria);

    @Query(value = """
        SELECT
            f.ID_FEEDBACK          AS idFeedback,
            f.CALIFICACION         AS calificacion,
            f.COMENTARIO           AS comentario,
            f.FECHA_EVALUACION     AS fechaEvaluacion,
            CONCAT(ue.NOMBRE, ' ', ue.PRIMER_APELLIDO)  AS nombreEstudiante,
            CONCAT(ud.NOMBRE, ' ', ud.PRIMER_APELLIDO)  AS nombreDocente,
            m.NOMBRE               AS nombreMateria,
            s.NUMERO               AS nombreSeccion,
            f.ID_SECCIONMATERIA_FK AS idSeccionmateria,
            f.ID_MATRICULA_FK      AS idMatricula,
            f.ID_ESTADO_FK         AS idEstado
        FROM FEEDBACK360_TB          f
        JOIN SECCIONMATERIA_TB       sm ON sm.ID_SECCIONMATERIA      = f.ID_SECCIONMATERIA_FK
        JOIN MATERIA_TB               m ON m.ID_MATERIA               = sm.ID_MATERIA_FK
        JOIN SECCION_TB               s ON s.ID_SECCION               = sm.ID_SECCION_FK
        JOIN USUARIOS_TB             ud ON ud.ID_USUARIO              = sm.ID_USUARIO_DOCENTE_FK
        JOIN MATRICULA_TB            mt ON mt.ID_MATRICULA            = f.ID_MATRICULA_FK
        JOIN USUARIOS_TB             ue ON ue.ID_USUARIO              = mt.ID_USUARIO_ESTUDIANTE_FK
        JOIN ENCARGADOESTUDIANTE_TB  ee ON ee.ID_USUARIO_ESTUDIANTE_FK = ue.ID_USUARIO
        WHERE ee.ID_USUARIO_ENCARGADO_FK = :idEncargado
          AND f.ID_ESTADO_FK = 1
        ORDER BY f.FECHA_EVALUACION DESC
    """, nativeQuery = true)
    List<FeedBackResumen> listarPorEncargado(
            @Param("idEncargado") Long idEncargado);

    @Query(value = """
        SELECT
            f.ID_FEEDBACK          AS idFeedback,
            f.CALIFICACION         AS calificacion,
            f.COMENTARIO           AS comentario,
            f.FECHA_EVALUACION     AS fechaEvaluacion,
            CONCAT(ue.NOMBRE, ' ', ue.PRIMER_APELLIDO)  AS nombreEstudiante,
            CONCAT(ud.NOMBRE, ' ', ud.PRIMER_APELLIDO)  AS nombreDocente,
            m.NOMBRE               AS nombreMateria,
            s.NUMERO               AS nombreSeccion,
            f.ID_SECCIONMATERIA_FK AS idSeccionmateria,
            f.ID_MATRICULA_FK      AS idMatricula,
            f.ID_ESTADO_FK         AS idEstado
        FROM FEEDBACK360_TB          f
        JOIN SECCIONMATERIA_TB       sm ON sm.ID_SECCIONMATERIA      = f.ID_SECCIONMATERIA_FK
        JOIN MATERIA_TB               m ON m.ID_MATERIA               = sm.ID_MATERIA_FK
        JOIN SECCION_TB               s ON s.ID_SECCION               = sm.ID_SECCION_FK
        JOIN USUARIOS_TB             ud ON ud.ID_USUARIO              = sm.ID_USUARIO_DOCENTE_FK
        JOIN MATRICULA_TB            mt ON mt.ID_MATRICULA            = f.ID_MATRICULA_FK
        JOIN USUARIOS_TB             ue ON ue.ID_USUARIO              = mt.ID_USUARIO_ESTUDIANTE_FK
        JOIN ENCARGADOESTUDIANTE_TB  ee ON ee.ID_USUARIO_ESTUDIANTE_FK = ue.ID_USUARIO
        WHERE ee.ID_USUARIO_ENCARGADO_FK = :idEncargado
          AND sm.ID_MATERIA_FK           = :idMateria
          AND f.ID_ESTADO_FK = 1
        ORDER BY f.FECHA_EVALUACION DESC
    """, nativeQuery = true)
    List<FeedBackResumen> listarPorEncargadoYMateria(
            @Param("idEncargado") Long idEncargado,
            @Param("idMateria")   Integer idMateria);

    @Query(value = """
        SELECT
            s.NUMERO               AS seccion,
            ROUND(AVG(f.CALIFICACION), 2) AS promedio,
            COUNT(f.ID_FEEDBACK)   AS total
        FROM FEEDBACK360_TB       f
        JOIN SECCIONMATERIA_TB   sm ON sm.ID_SECCIONMATERIA = f.ID_SECCIONMATERIA_FK
        JOIN SECCION_TB           s ON s.ID_SECCION          = sm.ID_SECCION_FK
        WHERE f.ID_ESTADO_FK = 1
        GROUP BY s.ID_SECCION, s.NUMERO
        ORDER BY promedio ASC
    """, nativeQuery = true)
    List<Map<String, Object>> promediosPorSeccion();

    @Query(value = """
        SELECT
            CONCAT(ud.NOMBRE, ' ', ud.PRIMER_APELLIDO) AS docente,
            ROUND(AVG(f.CALIFICACION), 2)              AS promedio,
            COUNT(f.ID_FEEDBACK)                       AS total
        FROM FEEDBACK360_TB       f
        JOIN SECCIONMATERIA_TB   sm ON sm.ID_SECCIONMATERIA = f.ID_SECCIONMATERIA_FK
        JOIN USUARIOS_TB         ud ON ud.ID_USUARIO         = sm.ID_USUARIO_DOCENTE_FK
        WHERE f.ID_ESTADO_FK = 1
        GROUP BY sm.ID_USUARIO_DOCENTE_FK, docente
        ORDER BY promedio ASC
    """, nativeQuery = true)
    List<Map<String, Object>> promediosPorDocente();

    @Query(value = """
        SELECT
            f.ID_FEEDBACK          AS idFeedback,
            f.CALIFICACION         AS calificacion,
            f.COMENTARIO           AS comentario,
            f.FECHA_EVALUACION     AS fechaEvaluacion,
            CONCAT(ue.NOMBRE, ' ', ue.PRIMER_APELLIDO)  AS nombreEstudiante,
            CONCAT(ud.NOMBRE, ' ', ud.PRIMER_APELLIDO)  AS nombreDocente,
            m.NOMBRE               AS nombreMateria,
            s.NUMERO               AS nombreSeccion,
            f.ID_SECCIONMATERIA_FK AS idSeccionmateria,
            f.ID_MATRICULA_FK      AS idMatricula,
            f.ID_ESTADO_FK         AS idEstado
        FROM FEEDBACK360_TB       f
        JOIN SECCIONMATERIA_TB   sm ON sm.ID_SECCIONMATERIA      = f.ID_SECCIONMATERIA_FK
        JOIN MATERIA_TB           m ON m.ID_MATERIA               = sm.ID_MATERIA_FK
        JOIN SECCION_TB           s ON s.ID_SECCION               = sm.ID_SECCION_FK
        JOIN USUARIOS_TB         ud ON ud.ID_USUARIO              = sm.ID_USUARIO_DOCENTE_FK
        JOIN MATRICULA_TB        mt ON mt.ID_MATRICULA            = f.ID_MATRICULA_FK
        JOIN USUARIOS_TB         ue ON ue.ID_USUARIO              = mt.ID_USUARIO_ESTUDIANTE_FK
        WHERE f.CALIFICACION <= :umbral
          AND f.ID_ESTADO_FK = 1
        ORDER BY f.CALIFICACION ASC
    """, nativeQuery = true)
    List<FeedBackResumen> listarAlertas(@Param("umbral") int umbral);

    @Query(value = """
        SELECT
            p.NOMBRE               AS periodo,
            s.NUMERO               AS seccion,
            ROUND(AVG(f.CALIFICACION), 2) AS promedio,
            COUNT(f.ID_FEEDBACK)   AS total
        FROM FEEDBACK360_TB       f
        JOIN SECCIONMATERIA_TB   sm ON sm.ID_SECCIONMATERIA = f.ID_SECCIONMATERIA_FK
        JOIN SECCION_TB           s ON s.ID_SECCION          = sm.ID_SECCION_FK
        JOIN PERIODOS_TB          p ON p.ID_PERIODO          = s.ID_PERIODO_FK
        WHERE p.ID_PERIODO IN (:idPeriodo1, :idPeriodo2)
          AND f.ID_ESTADO_FK = 1
        GROUP BY p.ID_PERIODO, p.NOMBRE, s.ID_SECCION, s.NUMERO
        ORDER BY p.NOMBRE, s.NUMERO
    """, nativeQuery = true)
    List<Map<String, Object>> comparativaPorPeriodos(
            @Param("idPeriodo1") Integer idPeriodo1,
            @Param("idPeriodo2") Integer idPeriodo2);

    @Query(value = """
        SELECT COUNT(*)
        FROM FEEDBACK360_TB
        WHERE ID_SECCIONMATERIA_FK = :idSeccionmateria
          AND ID_MATRICULA_FK      = :idMatricula
          AND ID_ESTADO_FK = 1
    """, nativeQuery = true)
    long contarExistente(
            @Param("idSeccionmateria") Integer idSeccionmateria,
            @Param("idMatricula")      Integer idMatricula);

    @Modifying
    @Transactional
    @Query(value = """
        CALL FEEDBACK360_INSERTAR(
            :calificacion, :comentario, :fechaEvaluacion,
            :idSeccionmateriaFk, :idMatriculaFk, :idEstadoFk
        )
    """, nativeQuery = true)
    void insertarFeedback(
            @Param("calificacion")       Integer   calificacion,
            @Param("comentario")         String    comentario,
            @Param("fechaEvaluacion")    LocalDate fechaEvaluacion,
            @Param("idSeccionmateriaFk") Integer   idSeccionmateriaFk,
            @Param("idMatriculaFk")      Integer   idMatriculaFk,
            @Param("idEstadoFk")         Integer   idEstadoFk);

    @Modifying
    @Transactional
    @Query(value = """
        CALL FEEDBACK360_MODIFICAR(
            :idFeedback, :calificacion, :comentario, :fechaEvaluacion,
            :idSeccionmateriaFk, :idMatriculaFk
        )
    """, nativeQuery = true)
    void modificarFeedback(
            @Param("idFeedback")         Integer   idFeedback,
            @Param("calificacion")       Integer   calificacion,
            @Param("comentario")         String    comentario,
            @Param("fechaEvaluacion")    LocalDate fechaEvaluacion,
            @Param("idSeccionmateriaFk") Integer   idSeccionmateriaFk,
            @Param("idMatriculaFk")      Integer   idMatriculaFk);

    @Modifying
    @Transactional
    @Query(value = """
        CALL FEEDBACK360_CAMBIAR_ESTADO(:idFeedback, :idEstado)
    """, nativeQuery = true)
    void cambiarEstadoFeedback(
            @Param("idFeedback") Integer idFeedback,
            @Param("idEstado")   Integer idEstado);
}

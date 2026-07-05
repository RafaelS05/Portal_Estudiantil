package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Asistencia;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    // ══════════════════════════════════════════════════════════════════
    // PROYECCIONES
    // ══════════════════════════════════════════════════════════════════
    interface AsistenciaListadoRow {

        Long getIdAsistencia();

        String getFechaAsistencia();

        Long getIdMatriculaFk();

        String getNombreEstudiante();

        Long getIdSeccionMateriaFk();

        String getNombreMateria();

        String getNumeroSeccion();

        String getNombreDocente();

        Long getIdEstadoFk();

        String getEstadoDescripcion();
    }

    interface PaseListaRow {

        Long getIdMatriculaFk();

        String getNombreEstudiante();

        String getNombreMateria();

        String getNumeroSeccion();

        Long getIdAsistencia();

        Long getIdEstadoFk();
    }

    // ← NUEVA PROYECCIÓN: para el dropdown del modal de pase de lista
    interface SeccionMateriaDropdownRow {

        Long getIdSeccionMateria();

        String getNombreMateria();

        String getNumeroSeccion();
    }

    // ══════════════════════════════════════════════════════════════════
    // CONSULTAS – LISTADO
    // ══════════════════════════════════════════════════════════════════
    @Query(value = """
        SELECT
            a.ID_ASISTENCIA          AS idAsistencia,
            DATE_FORMAT(a.FECHA_ASISTENCIA, '%Y-%m-%d') AS fechaAsistencia,
            a.ID_MATRICULA_FK        AS idMatriculaFk,
            CONCAT(u.NOMBRE, ' ', u.PRIMER_APELLIDO,
                   CASE WHEN u.SEGUNDO_APELLIDO IS NULL OR u.SEGUNDO_APELLIDO = ''
                        THEN '' ELSE CONCAT(' ', u.SEGUNDO_APELLIDO) END
            )                        AS nombreEstudiante,
            a.ID_SECCIONMATERIA_FK   AS idSeccionMateriaFk,
            m.NOMBRE                 AS nombreMateria,
            s.NUMERO                 AS numeroSeccion,
            CONCAT(d.NOMBRE, ' ', d.PRIMER_APELLIDO,
                   CASE WHEN d.SEGUNDO_APELLIDO IS NULL OR d.SEGUNDO_APELLIDO = ''
                        THEN '' ELSE CONCAT(' ', d.SEGUNDO_APELLIDO) END
            )                        AS nombreDocente,
            a.ID_ESTADO_FK           AS idEstadoFk,
            e.DESCRIPCION            AS estadoDescripcion
        FROM ASISTENCIAS_TB a
        JOIN MATRICULA_TB       mt ON mt.ID_MATRICULA       = a.ID_MATRICULA_FK
        JOIN USUARIOS_TB        u  ON u.ID_USUARIO          = mt.ID_USUARIO_ESTUDIANTE_FK
        JOIN SECCIONMATERIA_TB  sm ON sm.ID_SECCIONMATERIA  = a.ID_SECCIONMATERIA_FK
        JOIN MATERIA_TB         m  ON m.ID_MATERIA          = sm.ID_MATERIA_FK
        JOIN SECCION_TB         s  ON s.ID_SECCION          = sm.ID_SECCION_FK
        JOIN USUARIOS_TB        d  ON d.ID_USUARIO          = sm.ID_USUARIO_DOCENTE_FK
        JOIN ESTADOS_TB         e  ON e.ID_ESTADO           = a.ID_ESTADO_FK
        ORDER BY a.FECHA_ASISTENCIA DESC, a.ID_ASISTENCIA DESC
    """, nativeQuery = true)
    List<AsistenciaListadoRow> listarTodas();

    @Query(value = """
        SELECT
            a.ID_ASISTENCIA          AS idAsistencia,
            DATE_FORMAT(a.FECHA_ASISTENCIA, '%Y-%m-%d') AS fechaAsistencia,
            a.ID_MATRICULA_FK        AS idMatriculaFk,
            CONCAT(u.NOMBRE, ' ', u.PRIMER_APELLIDO,
                   CASE WHEN u.SEGUNDO_APELLIDO IS NULL OR u.SEGUNDO_APELLIDO = ''
                        THEN '' ELSE CONCAT(' ', u.SEGUNDO_APELLIDO) END
            )                        AS nombreEstudiante,
            a.ID_SECCIONMATERIA_FK   AS idSeccionMateriaFk,
            m.NOMBRE                 AS nombreMateria,
            s.NUMERO                 AS numeroSeccion,
            CONCAT(d.NOMBRE, ' ', d.PRIMER_APELLIDO,
                   CASE WHEN d.SEGUNDO_APELLIDO IS NULL OR d.SEGUNDO_APELLIDO = ''
                        THEN '' ELSE CONCAT(' ', d.SEGUNDO_APELLIDO) END
            )                        AS nombreDocente,
            a.ID_ESTADO_FK           AS idEstadoFk,
            e.DESCRIPCION            AS estadoDescripcion
        FROM ASISTENCIAS_TB a
        JOIN MATRICULA_TB       mt ON mt.ID_MATRICULA       = a.ID_MATRICULA_FK
        JOIN USUARIOS_TB        u  ON u.ID_USUARIO          = mt.ID_USUARIO_ESTUDIANTE_FK
        JOIN SECCIONMATERIA_TB  sm ON sm.ID_SECCIONMATERIA  = a.ID_SECCIONMATERIA_FK
        JOIN MATERIA_TB         m  ON m.ID_MATERIA          = sm.ID_MATERIA_FK
        JOIN SECCION_TB         s  ON s.ID_SECCION          = sm.ID_SECCION_FK
        JOIN USUARIOS_TB        d  ON d.ID_USUARIO          = sm.ID_USUARIO_DOCENTE_FK
        JOIN ESTADOS_TB         e  ON e.ID_ESTADO           = a.ID_ESTADO_FK
        WHERE a.ID_SECCIONMATERIA_FK = :idSeccionMateria
        ORDER BY a.FECHA_ASISTENCIA DESC, a.ID_ASISTENCIA DESC
    """, nativeQuery = true)
    List<AsistenciaListadoRow> listarPorSeccionMateria(
            @Param("idSeccionMateria") Long idSeccionMateria);

    @Query(value = """
        SELECT
            a.ID_ASISTENCIA          AS idAsistencia,
            DATE_FORMAT(a.FECHA_ASISTENCIA, '%Y-%m-%d') AS fechaAsistencia,
            a.ID_MATRICULA_FK        AS idMatriculaFk,
            CONCAT(u.NOMBRE, ' ', u.PRIMER_APELLIDO,
                   CASE WHEN u.SEGUNDO_APELLIDO IS NULL OR u.SEGUNDO_APELLIDO = ''
                        THEN '' ELSE CONCAT(' ', u.SEGUNDO_APELLIDO) END
            )                        AS nombreEstudiante,
            a.ID_SECCIONMATERIA_FK   AS idSeccionMateriaFk,
            m.NOMBRE                 AS nombreMateria,
            s.NUMERO                 AS numeroSeccion,
            CONCAT(d.NOMBRE, ' ', d.PRIMER_APELLIDO,
                   CASE WHEN d.SEGUNDO_APELLIDO IS NULL OR d.SEGUNDO_APELLIDO = ''
                        THEN '' ELSE CONCAT(' ', d.SEGUNDO_APELLIDO) END
            )                        AS nombreDocente,
            a.ID_ESTADO_FK           AS idEstadoFk,
            e.DESCRIPCION            AS estadoDescripcion
        FROM ASISTENCIAS_TB a
        JOIN MATRICULA_TB       mt ON mt.ID_MATRICULA       = a.ID_MATRICULA_FK
        JOIN USUARIOS_TB        u  ON u.ID_USUARIO          = mt.ID_USUARIO_ESTUDIANTE_FK
        JOIN SECCIONMATERIA_TB  sm ON sm.ID_SECCIONMATERIA  = a.ID_SECCIONMATERIA_FK
        JOIN MATERIA_TB         m  ON m.ID_MATERIA          = sm.ID_MATERIA_FK
        JOIN SECCION_TB         s  ON s.ID_SECCION          = sm.ID_SECCION_FK
        JOIN USUARIOS_TB        d  ON d.ID_USUARIO          = sm.ID_USUARIO_DOCENTE_FK
        JOIN ESTADOS_TB         e  ON e.ID_ESTADO           = a.ID_ESTADO_FK
        WHERE a.FECHA_ASISTENCIA = :fecha
        ORDER BY a.ID_ASISTENCIA DESC
    """, nativeQuery = true)
    List<AsistenciaListadoRow> listarPorFecha(
            @Param("fecha") LocalDate fecha);

    @Query(value = """
        SELECT
            a.ID_ASISTENCIA          AS idAsistencia,
            DATE_FORMAT(a.FECHA_ASISTENCIA, '%Y-%m-%d') AS fechaAsistencia,
            a.ID_MATRICULA_FK        AS idMatriculaFk,
            CONCAT(u.NOMBRE, ' ', u.PRIMER_APELLIDO,
                   CASE WHEN u.SEGUNDO_APELLIDO IS NULL OR u.SEGUNDO_APELLIDO = ''
                        THEN '' ELSE CONCAT(' ', u.SEGUNDO_APELLIDO) END
            )                        AS nombreEstudiante,
            a.ID_SECCIONMATERIA_FK   AS idSeccionMateriaFk,
            m.NOMBRE                 AS nombreMateria,
            s.NUMERO                 AS numeroSeccion,
            CONCAT(d.NOMBRE, ' ', d.PRIMER_APELLIDO,
                   CASE WHEN d.SEGUNDO_APELLIDO IS NULL OR d.SEGUNDO_APELLIDO = ''
                        THEN '' ELSE CONCAT(' ', d.SEGUNDO_APELLIDO) END
            )                        AS nombreDocente,
            a.ID_ESTADO_FK           AS idEstadoFk,
            e.DESCRIPCION            AS estadoDescripcion
        FROM ASISTENCIAS_TB a
        JOIN MATRICULA_TB       mt ON mt.ID_MATRICULA       = a.ID_MATRICULA_FK
        JOIN USUARIOS_TB        u  ON u.ID_USUARIO          = mt.ID_USUARIO_ESTUDIANTE_FK
        JOIN SECCIONMATERIA_TB  sm ON sm.ID_SECCIONMATERIA  = a.ID_SECCIONMATERIA_FK
        JOIN MATERIA_TB         m  ON m.ID_MATERIA          = sm.ID_MATERIA_FK
        JOIN SECCION_TB         s  ON s.ID_SECCION          = sm.ID_SECCION_FK
        JOIN USUARIOS_TB        d  ON d.ID_USUARIO          = sm.ID_USUARIO_DOCENTE_FK
        JOIN ESTADOS_TB         e  ON e.ID_ESTADO           = a.ID_ESTADO_FK
        WHERE a.ID_SECCIONMATERIA_FK = :idSeccionMateria
          AND a.FECHA_ASISTENCIA = :fecha
        ORDER BY a.ID_ASISTENCIA DESC
    """, nativeQuery = true)
    List<AsistenciaListadoRow> listarPorSeccionMateriaYFecha(
            @Param("idSeccionMateria") Long idSeccionMateria,
            @Param("fecha") LocalDate fecha);

    // ← NUEVA QUERY: listado filtrado por docente
    @Query(value = """
        SELECT
            a.ID_ASISTENCIA          AS idAsistencia,
            DATE_FORMAT(a.FECHA_ASISTENCIA, '%Y-%m-%d') AS fechaAsistencia,
            a.ID_MATRICULA_FK        AS idMatriculaFk,
            CONCAT(u.NOMBRE, ' ', u.PRIMER_APELLIDO,
                   CASE WHEN u.SEGUNDO_APELLIDO IS NULL OR u.SEGUNDO_APELLIDO = ''
                        THEN '' ELSE CONCAT(' ', u.SEGUNDO_APELLIDO) END
            )                        AS nombreEstudiante,
            a.ID_SECCIONMATERIA_FK   AS idSeccionMateriaFk,
            m.NOMBRE                 AS nombreMateria,
            s.NUMERO                 AS numeroSeccion,
            CONCAT(d.NOMBRE, ' ', d.PRIMER_APELLIDO,
                   CASE WHEN d.SEGUNDO_APELLIDO IS NULL OR d.SEGUNDO_APELLIDO = ''
                        THEN '' ELSE CONCAT(' ', d.SEGUNDO_APELLIDO) END
            )                        AS nombreDocente,
            a.ID_ESTADO_FK           AS idEstadoFk,
            e.DESCRIPCION            AS estadoDescripcion
        FROM ASISTENCIAS_TB a
        JOIN MATRICULA_TB       mt ON mt.ID_MATRICULA       = a.ID_MATRICULA_FK
        JOIN USUARIOS_TB        u  ON u.ID_USUARIO          = mt.ID_USUARIO_ESTUDIANTE_FK
        JOIN SECCIONMATERIA_TB  sm ON sm.ID_SECCIONMATERIA  = a.ID_SECCIONMATERIA_FK
        JOIN MATERIA_TB         m  ON m.ID_MATERIA          = sm.ID_MATERIA_FK
        JOIN SECCION_TB         s  ON s.ID_SECCION          = sm.ID_SECCION_FK
        JOIN USUARIOS_TB        d  ON d.ID_USUARIO          = sm.ID_USUARIO_DOCENTE_FK
        JOIN ESTADOS_TB         e  ON e.ID_ESTADO           = a.ID_ESTADO_FK
        WHERE d.ID_USUARIO = :idDocente
        ORDER BY a.FECHA_ASISTENCIA DESC, a.ID_ASISTENCIA DESC
    """, nativeQuery = true)
    List<AsistenciaListadoRow> listarPorDocente(@Param("idDocente") Long idDocente);

    // ← NUEVA QUERY: secciones-materia de un docente (para el dropdown del modal)
    @Query(value = """
        SELECT
            sm.ID_SECCIONMATERIA AS idSeccionMateria,
            m.NOMBRE             AS nombreMateria,
            s.NUMERO             AS numeroSeccion
        FROM SECCIONMATERIA_TB sm
        JOIN MATERIA_TB m ON m.ID_MATERIA = sm.ID_MATERIA_FK
        JOIN SECCION_TB s ON s.ID_SECCION = sm.ID_SECCION_FK
        WHERE sm.ID_USUARIO_DOCENTE_FK = :idDocente
          AND sm.ID_ESTADO_FK = 1
        ORDER BY m.NOMBRE, s.NUMERO
    """, nativeQuery = true)
    List<SeccionMateriaDropdownRow> listarSeccionesMateriaPorDocente(@Param("idDocente") Long idDocente);

    // ← NUEVA QUERY: todas las secciones-materia (para admins)
    @Query(value = """
        SELECT
            sm.ID_SECCIONMATERIA AS idSeccionMateria,
            m.NOMBRE             AS nombreMateria,
            s.NUMERO             AS numeroSeccion
        FROM SECCIONMATERIA_TB sm
        JOIN MATERIA_TB m ON m.ID_MATERIA = sm.ID_MATERIA_FK
        JOIN SECCION_TB s ON s.ID_SECCION = sm.ID_SECCION_FK
        WHERE sm.ID_ESTADO_FK = 1
        ORDER BY m.NOMBRE, s.NUMERO
    """, nativeQuery = true)
    List<SeccionMateriaDropdownRow> listarTodasSeccionesMateria();

    // ══════════════════════════════════════════════════════════════════
    // CONSULTAS – PASE DE LISTA
    // ══════════════════════════════════════════════════════════════════
    @Query(value = """
        SELECT
            mt.ID_MATRICULA         AS idMatriculaFk,
            CONCAT(u.NOMBRE, ' ', u.PRIMER_APELLIDO,
                   CASE WHEN u.SEGUNDO_APELLIDO IS NULL OR u.SEGUNDO_APELLIDO = ''
                        THEN '' ELSE CONCAT(' ', u.SEGUNDO_APELLIDO) END
            )                       AS nombreEstudiante,
            m.NOMBRE                AS nombreMateria,
            s.NUMERO                AS numeroSeccion,
            a.ID_ASISTENCIA         AS idAsistencia,
            a.ID_ESTADO_FK          AS idEstadoFk
        FROM SECCIONMATERIA_TB sm
        JOIN SECCION_TB        s   ON s.ID_SECCION  = sm.ID_SECCION_FK
        JOIN MATERIA_TB        m   ON m.ID_MATERIA  = sm.ID_MATERIA_FK
        JOIN MATRICULA_TB      mt  ON mt.ID_SECCION_FK = sm.ID_SECCION_FK
                                  AND mt.ID_ESTADO_FK  = 1
        JOIN USUARIOS_TB       u   ON u.ID_USUARIO  = mt.ID_USUARIO_ESTUDIANTE_FK
        LEFT JOIN ASISTENCIAS_TB a ON a.ID_MATRICULA_FK       = mt.ID_MATRICULA
                                  AND a.ID_SECCIONMATERIA_FK  = sm.ID_SECCIONMATERIA
                                  AND a.FECHA_ASISTENCIA      = :fecha
        WHERE sm.ID_SECCIONMATERIA = :idSeccionMateria
          AND sm.ID_ESTADO_FK      = 1
        ORDER BY u.PRIMER_APELLIDO, u.NOMBRE
    """, nativeQuery = true)
    List<PaseListaRow> cargarPaseLista(
            @Param("idSeccionMateria") Long idSeccionMateria,
            @Param("fecha") LocalDate fecha);

    // ══════════════════════════════════════════════════════════════════
    // CONTADORES
    // ══════════════════════════════════════════════════════════════════
    long countByIdEstadoFk(Long idEstadoFk);

    // ══════════════════════════════════════════════════════════════════
    // CRUD – stored procedures
    // ══════════════════════════════════════════════════════════════════
    @Modifying
    @Transactional
    @Query(value = """
        CALL ASISTENCIAS_INSERTAR(
            :fecha,
            :idMatricula,
            :idSeccionMateria,
            :idEstado
        )
    """, nativeQuery = true)
    void insertarAsistencia(
            @Param("fecha") LocalDate fecha,
            @Param("idMatricula") Long idMatricula,
            @Param("idSeccionMateria") Long idSeccionMateria,
            @Param("idEstado") Long idEstado
    );

    @Modifying
    @Transactional
    @Query(value = """
        CALL ASISTENCIAS_MODIFICAR(
            :idAsistencia,
            :fecha,
            :idMatricula,
            :idSeccionMateria
        )
    """, nativeQuery = true)
    void modificarAsistencia(
            @Param("idAsistencia") Long idAsistencia,
            @Param("fecha") LocalDate fecha,
            @Param("idMatricula") Long idMatricula,
            @Param("idSeccionMateria") Long idSeccionMateria
    );

    @Modifying
    @Transactional
    @Query(value = """
        CALL ASISTENCIAS_CAMBIAR_ESTADO(
            :idAsistencia,
            :idEstado
        )
    """, nativeQuery = true)
    void cambiarEstadoAsistencia(
            @Param("idAsistencia") Long idAsistencia,
            @Param("idEstado") Long idEstado
    );

    // ← NUEVA PROYECCIÓN: historial de asistencia para el ENCARGADO
    interface HistorialAsistenciaHijoRow {

        String getFechaAsistencia();

        String getNombreMateria();

        String getNumeroSeccion();

        Long getIdEstadoFk();

        String getEstadoDescripcion();

        String getNombreEstudiante();
    }

    // ← NUEVA QUERY: historial de asistencia de los hijos de un encargado
    @Query(value = """
        SELECT
            DATE_FORMAT(a.FECHA_ASISTENCIA, '%Y-%m-%d') AS fechaAsistencia,
            m.NOMBRE                 AS nombreMateria,
            s.NUMERO                 AS numeroSeccion,
            a.ID_ESTADO_FK           AS idEstadoFk,
            e.DESCRIPCION            AS estadoDescripcion,
            CONCAT(u.NOMBRE, ' ', u.PRIMER_APELLIDO,
                   CASE WHEN u.SEGUNDO_APELLIDO IS NULL OR u.SEGUNDO_APELLIDO = ''
                        THEN '' ELSE CONCAT(' ', u.SEGUNDO_APELLIDO) END
            )                        AS nombreEstudiante
        FROM ASISTENCIAS_TB a
        JOIN MATRICULA_TB       mt ON mt.ID_MATRICULA       = a.ID_MATRICULA_FK
        JOIN USUARIOS_TB        u  ON u.ID_USUARIO          = mt.ID_USUARIO_ESTUDIANTE_FK
        JOIN SECCIONMATERIA_TB  sm ON sm.ID_SECCIONMATERIA  = a.ID_SECCIONMATERIA_FK
        JOIN MATERIA_TB         m  ON m.ID_MATERIA          = sm.ID_MATERIA_FK
        JOIN SECCION_TB         s  ON s.ID_SECCION          = sm.ID_SECCION_FK
        JOIN ESTADOS_TB         e  ON e.ID_ESTADO           = a.ID_ESTADO_FK
        JOIN ENCARGADOESTUDIANTE_TB ee ON ee.ID_USUARIO_ESTUDIANTE_FK = mt.ID_USUARIO_ESTUDIANTE_FK
        WHERE ee.ID_USUARIO_ENCARGADO_FK = :idEncargado
          AND ee.ID_ESTADO_FK = 1
          AND (:idEstudiante IS NULL OR mt.ID_USUARIO_ESTUDIANTE_FK = :idEstudiante)
        ORDER BY u.PRIMER_APELLIDO, u.NOMBRE, a.FECHA_ASISTENCIA DESC
    """, nativeQuery = true)
    List<HistorialAsistenciaHijoRow> listarHistorialPorEncargado(
            @Param("idEncargado") Long idEncargado,
            @Param("idEstudiante") Long idEstudiante);

    // ← NUEVA QUERY: lista de hijos de un encargado (para el selector, si tiene más de uno)
    @Query(value = """
        SELECT DISTINCT
            u.ID_USUARIO AS idUsuario,
            CONCAT(u.NOMBRE, ' ', u.PRIMER_APELLIDO,
                   CASE WHEN u.SEGUNDO_APELLIDO IS NULL OR u.SEGUNDO_APELLIDO = ''
                        THEN '' ELSE CONCAT(' ', u.SEGUNDO_APELLIDO) END
            ) AS nombreCompleto
        FROM ENCARGADOESTUDIANTE_TB ee
        JOIN USUARIOS_TB u ON u.ID_USUARIO = ee.ID_USUARIO_ESTUDIANTE_FK
        WHERE ee.ID_USUARIO_ENCARGADO_FK = :idEncargado
          AND ee.ID_ESTADO_FK = 1
        ORDER BY nombreCompleto
    """, nativeQuery = true)
    List<HijoDropdownRow> listarHijosPorEncargado(@Param("idEncargado") Long idEncargado);

    interface HijoDropdownRow {

        Long getIdUsuario();

        String getNombreCompleto();
    }
}

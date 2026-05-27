package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Horario;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface HorarioRepository extends JpaRepository<Horario, Long> {

    Horario findByIdHorario(Long idHorario);

    List<Horario> findByIdEstadoFk(Long idEstadoFk);

    long countByIdEstadoFk(Long idEstadoFk);

    interface HorarioRow {

        Long getIdHorario();

        Long getIdSeccionMateriaFk();

        String getNumeroSeccion();

        String getNombreMateria();

        String getNombreDocente();

        Integer getDiaSemana();

        String getHoraInicio();

        String getHoraFin();

        Long getIdAulaFk();

        String getNumeroAula();

        Long getIdEstadoFk();
    }

    interface SeccionEncargadoRow {

        Long getIdSeccion();

        String getNumeroSeccion();

        String getPeriodo();

        String getNombreEstudiante();
    }

    @Query(value = """
    SELECT
        h.ID_HORARIO              AS idHorario,
        h.ID_SECCIONMATERIA_FK    AS idSeccionMateriaFk,
        s.NUMERO                  AS numeroSeccion,
        m.NOMBRE                  AS nombreMateria,
        CONCAT(u.NOMBRE, ' ', u.PRIMER_APELLIDO) AS nombreDocente,
        h.DIA_SEMANA              AS diaSemana,
        h.HORA_INICIO             AS horaInicio,
        h.HORA_FIN                AS horaFin,
        h.ID_AULA_FK              AS idAulaFk,
        a.NUMERO                  AS numeroAula,
        h.ID_ESTADO_FK            AS idEstadoFk
    FROM HORARIO_TB h
    JOIN SECCIONMATERIA_TB sm ON sm.ID_SECCIONMATERIA  = h.ID_SECCIONMATERIA_FK
    JOIN SECCION_TB  s  ON s.ID_SECCION  = sm.ID_SECCION_FK
    JOIN MATERIA_TB  m  ON m.ID_MATERIA  = sm.ID_MATERIA_FK
    JOIN USUARIOS_TB u  ON u.ID_USUARIO  = sm.ID_USUARIO_DOCENTE_FK
    LEFT JOIN AULA_TB a ON a.ID_AULA     = h.ID_AULA_FK
    WHERE h.ID_ESTADO_FK = 1
    ORDER BY s.NUMERO, h.DIA_SEMANA, h.HORA_INICIO
""", nativeQuery = true)
    List<HorarioRow> listarResumen();
    
        @Query(value = """
    SELECT
        h.ID_HORARIO              AS idHorario,
        h.ID_SECCIONMATERIA_FK    AS idSeccionMateriaFk,
        s.NUMERO                  AS numeroSeccion,
        m.NOMBRE                  AS nombreMateria,
        CONCAT(u.NOMBRE, ' ', u.PRIMER_APELLIDO) AS nombreDocente,
        h.DIA_SEMANA              AS diaSemana,
        h.HORA_INICIO             AS horaInicio,
        h.HORA_FIN                AS horaFin,
        h.ID_AULA_FK              AS idAulaFk,
        a.NUMERO                  AS numeroAula,
        h.ID_ESTADO_FK            AS idEstadoFk
    FROM HORARIO_TB h
    JOIN SECCIONMATERIA_TB sm ON sm.ID_SECCIONMATERIA  = h.ID_SECCIONMATERIA_FK
    JOIN SECCION_TB  s  ON s.ID_SECCION  = sm.ID_SECCION_FK
    JOIN MATERIA_TB  m  ON m.ID_MATERIA  = sm.ID_MATERIA_FK
    JOIN USUARIOS_TB u  ON u.ID_USUARIO  = sm.ID_USUARIO_DOCENTE_FK
    LEFT JOIN AULA_TB a ON a.ID_AULA     = h.ID_AULA_FK
    ORDER BY s.NUMERO, h.DIA_SEMANA, h.HORA_INICIO
""", nativeQuery = true)
    List<HorarioRow> listarTodos();

    // Horario activo de una sección (todas sus materias) para la vista de consulta
    @Query(value = """
    SELECT
        h.ID_HORARIO              AS idHorario,
        h.ID_SECCIONMATERIA_FK    AS idSeccionMateriaFk,
        s.NUMERO                  AS numeroSeccion,
        m.NOMBRE                  AS nombreMateria,
        CONCAT(u.NOMBRE, ' ', u.PRIMER_APELLIDO) AS nombreDocente,
        h.DIA_SEMANA              AS diaSemana,
        h.HORA_INICIO             AS horaInicio,
        h.HORA_FIN                AS horaFin,
        h.ID_AULA_FK              AS idAulaFk,
        a.NUMERO                  AS numeroAula,
        h.ID_ESTADO_FK            AS idEstadoFk
    FROM HORARIO_TB h
    JOIN SECCIONMATERIA_TB sm ON sm.ID_SECCIONMATERIA  = h.ID_SECCIONMATERIA_FK
    JOIN SECCION_TB  s  ON s.ID_SECCION  = sm.ID_SECCION_FK
    JOIN MATERIA_TB  m  ON m.ID_MATERIA  = sm.ID_MATERIA_FK
    JOIN USUARIOS_TB u  ON u.ID_USUARIO  = sm.ID_USUARIO_DOCENTE_FK
    LEFT JOIN AULA_TB a ON a.ID_AULA     = h.ID_AULA_FK
    WHERE h.ID_ESTADO_FK = 1
      AND sm.ID_SECCION_FK = :idSeccion
    ORDER BY h.DIA_SEMANA, h.HORA_INICIO
""", nativeQuery = true)
    List<HorarioRow> listarActivosPorSeccion(@Param("idSeccion") Long idSeccion);

    // Secciones donde están matriculados los estudiantes ligados a un encargado
    @Query(value = """
    SELECT DISTINCT
        s.ID_SECCION AS idSeccion,
        s.NUMERO     AS numeroSeccion,
        p.NOMBRE     AS periodo,
        CONCAT(u.NOMBRE, ' ', u.PRIMER_APELLIDO,
               IFNULL(CONCAT(' ', u.SEGUNDO_APELLIDO), '')) AS nombreEstudiante
    FROM ENCARGADOESTUDIANTE_TB ee
    JOIN USUARIOS_TB  u ON u.ID_USUARIO = ee.ID_USUARIO_ESTUDIANTE_FK
    JOIN MATRICULA_TB m ON m.ID_USUARIO_ESTUDIANTE_FK = u.ID_USUARIO
                       AND m.ID_ESTADO_FK IN (1, 3)
    JOIN SECCION_TB   s ON s.ID_SECCION = m.ID_SECCION_FK
    JOIN PERIODOS_TB  p ON p.ID_PERIODO = s.ID_PERIODO_FK
    WHERE ee.ID_USUARIO_ENCARGADO_FK = :idEncargado
      AND ee.ID_ESTADO_FK = 1
      AND u.ID_ESTADO_FK  = 1
    ORDER BY p.FECHA_INICIO DESC, nombreEstudiante
""", nativeQuery = true)
    List<SeccionEncargadoRow> listarSeccionesDeEncargado(@Param("idEncargado") Long idEncargado);

    // Consultas útiles
    @Query(value = """
        SELECT *
        FROM HORARIO_TB
        WHERE ID_ESTADO_FK = 1
        ORDER BY DIA_SEMANA ASC, HORA_INICIO ASC
    """, nativeQuery = true)
    List<Horario> listarActivos();

    @Query(value = """
        SELECT *
        FROM HORARIO_TB
        WHERE ID_SECCIONMATERIA_FK = :idSeccionMateria
        ORDER BY DIA_SEMANA ASC, HORA_INICIO ASC
    """, nativeQuery = true)
    List<Horario> listarPorSeccionMateria(@Param("idSeccionMateria") Long idSeccionMateria);

    @Query(value = """
        SELECT *
        FROM HORARIO_TB
        WHERE ID_AULA_FK = :idAula
        ORDER BY DIA_SEMANA ASC, HORA_INICIO ASC
    """, nativeQuery = true)
    List<Horario> listarPorAula(@Param("idAula") Long idAula);

    @Query(value = """
        SELECT *
        FROM HORARIO_TB
        WHERE ID_SECCIONMATERIA_FK = :idSeccionMateria
          AND DIA_SEMANA = :diaSemana
        ORDER BY HORA_INICIO ASC
    """, nativeQuery = true)
    List<Horario> listarPorSeccionMateriaYDia(@Param("idSeccionMateria") Long idSeccionMateria,
            @Param("diaSemana") Integer diaSemana);

    // Validaciones (UQ)
    // UQ: (DIA_SEMANA, HORA_INICIO, HORA_FIN, ID_SECCIONMATERIA_FK)
    @Query(value = """
        SELECT COUNT(*)
        FROM HORARIO_TB
        WHERE DIA_SEMANA = :diaSemana
          AND HORA_INICIO = :horaInicio
          AND HORA_FIN = :horaFin
          AND ID_SECCIONMATERIA_FK = :idSeccionMateriaFk
          AND (:idHorario IS NULL OR ID_HORARIO <> :idHorario)
    """, nativeQuery = true)
    long contarDuplicadoBloque(
            @Param("diaSemana") Integer diaSemana,
            @Param("horaInicio") String horaInicio,
            @Param("horaFin") String horaFin,
            @Param("idSeccionMateriaFk") Long idSeccionMateriaFk,
            @Param("idHorario") Long idHorario
    );

    @Query(value = """
        SELECT *
        FROM HORARIO_TB
        WHERE DIA_SEMANA = :diaSemana
          AND HORA_INICIO = :horaInicio
          AND HORA_FIN = :horaFin
          AND ID_SECCIONMATERIA_FK = :idSeccionMateriaFk
        LIMIT 1
    """, nativeQuery = true)
    Optional<Horario> findFirstByBloque(@Param("diaSemana") Integer diaSemana,
            @Param("horaInicio") String horaInicio,
            @Param("horaFin") String horaFin,
            @Param("idSeccionMateriaFk") Long idSeccionMateriaFk);

    // CRUD
    @Modifying
    @Transactional
    @Query(value = """
    CALL HORARIO_INSERTAR(:diaSemana, :horaInicio, :horaFin,
                          :idAulaFk, :idSeccionMateriaFk, :idEstadoFk)
""", nativeQuery = true)
    void insertarHorario(@Param("diaSemana") Integer diaSemana,
            @Param("horaInicio") String horaInicio,
            @Param("horaFin") String horaFin,
            @Param("idAulaFk") Long idAulaFk,
            @Param("idSeccionMateriaFk") Long idSeccionMateriaFk,
            @Param("idEstadoFk") Long idEstadoFk);

    @Query(value = "SELECT LAST_INSERT_ID()", nativeQuery = true)
    Long obtenerUltimoIdInsertado();

    @Modifying
    @Transactional
    @Query(value = """
        CALL HORARIO_MODIFICAR(
            :idHorario,
            :diaSemana,
            :horaInicio,
            :horaFin,
            :idAulaFk,
            :idSeccionMateriaFk
        )
    """, nativeQuery = true)
    void modificarHorario(
            @Param("idHorario") Long idHorario,
            @Param("diaSemana") Integer diaSemana,
            @Param("horaInicio") String horaInicio,
            @Param("horaFin") String horaFin,
            @Param("idAulaFk") Long idAulaFk,
            @Param("idSeccionMateriaFk") Long idSeccionMateriaFk
    );

    @Modifying
    @Transactional
    @Query(value = """
        CALL HORARIO_CAMBIAR_ESTADO(
            :idHorario,
            :idEstado
        )
    """, nativeQuery = true)
    void cambiarEstadoHorario(
            @Param("idHorario") Long idHorario,
            @Param("idEstado") Long idEstado
    );
}

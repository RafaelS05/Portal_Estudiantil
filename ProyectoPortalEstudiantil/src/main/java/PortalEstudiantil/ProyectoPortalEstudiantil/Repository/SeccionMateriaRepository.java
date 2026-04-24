package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.SeccionMateria;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface SeccionMateriaRepository extends JpaRepository<SeccionMateria, Long> {

    SeccionMateria findByIdSeccionMateria(Long idSeccionMateria);

    List<SeccionMateria> findByIdEstadoFk(Long idEstadoFk);

    long countByIdEstadoFk(Long idEstadoFk);

    interface SeccionMateriaRow {

        Long getIdSeccionMateria();

        Long getIdSeccionFk();

        String getNumeroSeccion();      // ← viene del JOIN

        Long getIdMateriaFk();

        String getNombreMateria();      // ← viene del JOIN

        Long getIdUsuarioDocenteFk();

        String getNombreDocente();      // ← viene del JOIN

        Long getIdEstadoFk();
    }

    @Query(value = """
    SELECT
        sm.ID_SECCIONMATERIA   AS idSeccionMateria,
        sm.ID_SECCION_FK        AS idSeccionFk,
        s.NUMERO                AS numeroSeccion,
        sm.ID_MATERIA_FK        AS idMateriaFk,
        m.NOMBRE                AS nombreMateria,
        sm.ID_USUARIO_DOCENTE_FK AS idUsuarioDocenteFk,
        CONCAT(u.NOMBRE, ' ', u.PRIMER_APELLIDO) AS nombreDocente,
        sm.ID_ESTADO_FK         AS idEstadoFk
    FROM SECCIONMATERIA_TB sm
    JOIN SECCION_TB  s ON s.ID_SECCION  = sm.ID_SECCION_FK
    JOIN MATERIA_TB  m ON m.ID_MATERIA  = sm.ID_MATERIA_FK
    JOIN USUARIOS_TB  u ON u.ID_USUARIO  = sm.ID_USUARIO_DOCENTE_FK
    ORDER BY s.NUMERO, m.NOMBRE
""", nativeQuery = true)
    List<SeccionMateriaRow> listarResumen();

    // UQ: (ID_SECCION_FK, ID_MATERIA_FK, ID_USUARIO_DOCENTE_FK)
    @Query(value = """
        SELECT COUNT(*)
        FROM SECCIONMATERIA_TB
        WHERE ID_SECCION_FK = :idSeccion
          AND ID_MATERIA_FK = :idMateria
          AND ID_USUARIO_DOCENTE_FK = :idDocente
          AND (:idSeccionMateria IS NULL OR ID_SECCIONMATERIA <> :idSeccionMateria)
    """, nativeQuery = true)
    long contarDuplicado(@Param("idSeccion") Long idSeccion,
            @Param("idMateria") Long idMateria,
            @Param("idDocente") Long idDocente,
            @Param("idSeccionMateria") Long idSeccionMateria);

    @Query(value = """
        SELECT *
        FROM SECCIONMATERIA_TB
        WHERE ID_SECCION_FK = :idSeccion
        ORDER BY ID_SECCIONMATERIA DESC
    """, nativeQuery = true)
    List<SeccionMateria> listarPorSeccion(@Param("idSeccion") Long idSeccion);

    @Query(value = """
        SELECT *
        FROM SECCIONMATERIA_TB
        WHERE ID_USUARIO_DOCENTE_FK = :idDocente
        ORDER BY ID_SECCIONMATERIA DESC
    """, nativeQuery = true)
    List<SeccionMateria> listarPorDocente(@Param("idDocente") Long idDocente);

    @Query(value = """
        SELECT *
        FROM SECCIONMATERIA_TB
        WHERE ID_SECCION_FK = :idSeccion
          AND ID_MATERIA_FK = :idMateria
          AND ID_USUARIO_DOCENTE_FK = :idDocente
        LIMIT 1
    """, nativeQuery = true)
    Optional<SeccionMateria> findFirstByTripleta(@Param("idSeccion") Long idSeccion,
            @Param("idMateria") Long idMateria,
            @Param("idDocente") Long idDocente);

    @Query(value = """
        SELECT *
        FROM SECCIONMATERIA_TB
        WHERE ID_ESTADO_FK = 1
        ORDER BY ID_SECCIONMATERIA DESC
    """, nativeQuery = true)
    List<SeccionMateria> listarActivas();

    // CRUD via PROCEDURES
    @Query(value = """
        CALL SECCIONMATERIA_INSERTAR(:idSeccionFk, :idMateriaFk, :idUsuarioDocenteFk, :idEstadoFk)
    """, nativeQuery = true)
    Long insertarSeccionMateriaRetornaId(@Param("idSeccionFk") Long idSeccionFk,
            @Param("idMateriaFk") Long idMateriaFk,
            @Param("idUsuarioDocenteFk") Long idUsuarioDocenteFk,
            @Param("idEstadoFk") Long idEstadoFk);

    @Modifying
    @Transactional
    @Query(value = """
        CALL SECCIONMATERIA_MODIFICAR(:idSeccionMateria, :idSeccionFk, :idMateriaFk, :idUsuarioDocenteFk)
    """, nativeQuery = true)
    void modificarSeccionMateria(@Param("idSeccionMateria") Long idSeccionMateria,
            @Param("idSeccionFk") Long idSeccionFk,
            @Param("idMateriaFk") Long idMateriaFk,
            @Param("idUsuarioDocenteFk") Long idUsuarioDocenteFk);

    @Modifying
    @Transactional
    @Query(value = """
        CALL SECCIONMATERIA_CAMBIAR_ESTADO(:idSeccionMateria, :idEstado)
    """, nativeQuery = true)
    void cambiarEstadoSeccionMateria(@Param("idSeccionMateria") Long idSeccionMateria,
            @Param("idEstado") Long idEstado);
    
    @Query(value = """
    SELECT
        sm.ID_SECCIONMATERIA      AS idSeccionMateria,
        sm.ID_SECCION_FK          AS idSeccionFk,
        s.NUMERO                  AS numeroSeccion,
        sm.ID_MATERIA_FK          AS idMateriaFk,
        m.NOMBRE                  AS nombreMateria,
        sm.ID_USUARIO_DOCENTE_FK  AS idUsuarioDocenteFk,
        CONCAT(u.NOMBRE, ' ', u.PRIMER_APELLIDO) AS nombreDocente,
        sm.ID_ESTADO_FK           AS idEstadoFk
    FROM SECCIONMATERIA_TB sm
    JOIN SECCION_TB  s ON s.ID_SECCION = sm.ID_SECCION_FK
    JOIN MATERIA_TB  m ON m.ID_MATERIA = sm.ID_MATERIA_FK
    JOIN USUARIOS_TB u ON u.ID_USUARIO = sm.ID_USUARIO_DOCENTE_FK
    WHERE sm.ID_USUARIO_DOCENTE_FK = :idDocente
      AND sm.ID_ESTADO_FK = 1
    ORDER BY s.NUMERO, m.NOMBRE
""", nativeQuery = true)
List<SeccionMateriaRow> listarResumenPorDocente(@Param("idDocente") Long idDocente);

}

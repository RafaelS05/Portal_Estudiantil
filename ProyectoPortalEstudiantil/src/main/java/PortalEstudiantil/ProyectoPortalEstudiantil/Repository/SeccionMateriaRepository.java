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
}

package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.EncargadoEstudiante;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EncargadoEstudianteRepository extends JpaRepository<EncargadoEstudiante, Long> {

    List<EncargadoEstudiante> findByIdUsuarioEstudianteFk(Long idUsuarioEstudianteFk);

    @Query(value = """
        SELECT ee.*
        FROM ENCARGADOESTUDIANTE_TB ee
        WHERE ee.ID_USUARIO_ESTUDIANTE_FK = :idEstudiante
          AND ee.ID_ESTADO_FK = 1
        ORDER BY ee.ID_ENCARGADOESTUDIANTE DESC
    """, nativeQuery = true)
    List<EncargadoEstudiante> listarActivosPorEstudiante(@Param("idEstudiante") Long idEstudiante);

    @Query(value = """
        SELECT ee.*
        FROM ENCARGADOESTUDIANTE_TB ee
        WHERE ee.ID_USUARIO_ENCARGADO_FK = :idEncargado
          AND ee.ID_ESTADO_FK = 1
        ORDER BY ee.ID_ENCARGADOESTUDIANTE DESC
    """, nativeQuery = true)
    List<EncargadoEstudiante> listarActivosPorEncargado(@Param("idEncargado") Long idEncargado);

    @Modifying
    @Transactional
    @Query(value = """
        CALL ENCARGADOESTUDIANTE_INSERTAR(
            :parentesco,
            :idEstudiante,
            :idEncargado,
            :idEstado
        )
    """, nativeQuery = true)
    void insertarRelacion(
            @Param("parentesco") String parentesco,
            @Param("idEstudiante") Long idEstudiante,
            @Param("idEncargado") Long idEncargado,
            @Param("idEstado") Long idEstado
    );

    @Modifying
    @Transactional
    @Query(value = """
        CALL ENCARGADOESTUDIANTE_CAMBIAR_ESTADO(
            :idRelacion,
            :idEstado
        )
    """, nativeQuery = true)
    void cambiarEstadoRelacion(
            @Param("idRelacion") Long idRelacion,
            @Param("idEstado") Long idEstado
    );

    @Query(value = """
        SELECT COUNT(*)
        FROM ENCARGADOESTUDIANTE_TB ee
        WHERE ee.ID_USUARIO_ESTUDIANTE_FK = :idEstudiante
          AND ee.ID_USUARIO_ENCARGADO_FK = :idEncargado
          AND ee.ID_ESTADO_FK = 1
    """, nativeQuery = true)
    long countRelacionActivaEntre(@Param("idEstudiante") Long idEstudiante,
                                  @Param("idEncargado") Long idEncargado);

    @Query(value = """
        SELECT COUNT(*)
        FROM ENCARGADOESTUDIANTE_TB ee
        WHERE ee.ID_USUARIO_ESTUDIANTE_FK = :idEstudiante
          AND ee.ID_ESTADO_FK = 1
    """, nativeQuery = true)
    long countRelacionesActivasPorEstudiante(@Param("idEstudiante") Long idEstudiante);

    @Query(value = """
        SELECT COUNT(*)
        FROM ENCARGADOESTUDIANTE_TB ee
        WHERE ee.ID_USUARIO_ENCARGADO_FK = :idEncargado
          AND ee.ID_ESTADO_FK = 1
    """, nativeQuery = true)
    long countRelacionesActivasPorEncargado(@Param("idEncargado") Long idEncargado);
}

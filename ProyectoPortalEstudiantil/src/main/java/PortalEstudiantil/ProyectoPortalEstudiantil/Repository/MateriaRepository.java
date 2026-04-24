package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Materia;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MateriaRepository extends JpaRepository<Materia, Long> {
     Materia findByIdMateria(Long idMateria);

    List<Materia> findByIdEstadoFk(Long idEstadoFk);

    Optional<Materia> findFirstByNombreIgnoreCase(String nombre);

    Optional<Materia> findFirstByCodigoMateriaIgnoreCase(String codigoMateria);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByCodigoMateriaIgnoreCase(String codigoMateria);

    long countByIdEstadoFk(Long idEstadoFk);

    @Query(value = """
        SELECT
            m.ID_MATERIA AS idMateria,
            m.NOMBRE AS nombre,
            m.CODIGO AS codigoMateria,
            m.ID_ESTADO_FK AS idEstadoFk,
            CASE WHEN m.ID_ESTADO_FK = 1 THEN 1 ELSE 0 END AS activa
        FROM MATERIA_TB m
        ORDER BY m.NOMBRE ASC
    """, nativeQuery = true)
    List<Materia> listarResumen();

    @Query(value = """
        SELECT *
        FROM MATERIA_TB
        WHERE ID_ESTADO_FK = 1
        ORDER BY NOMBRE ASC
    """, nativeQuery = true)
    List<Materia> listarActivas();

    @Query(value = """
        SELECT *
        FROM MATERIA_TB
        WHERE (
            LOWER(NOMBRE) LIKE LOWER(CONCAT('%', :busqueda, '%'))
            OR LOWER(CODIGO) LIKE LOWER(CONCAT('%', :busqueda, '%'))
        )
        ORDER BY NOMBRE ASC
    """, nativeQuery = true)
    List<Materia> buscarPorNombreOCodigo(@Param("busqueda") String busqueda);

    // ====================
    // Validaciones (duplicados)
    // ====================

    @Query(value = """
        SELECT COUNT(*)
        FROM MATERIA_TB
        WHERE LOWER(NOMBRE) = LOWER(:nombre)
          AND (:idMateria IS NULL OR ID_MATERIA <> :idMateria)
    """, nativeQuery = true)
    long contarNombreDuplicado(@Param("nombre") String nombre,
                              @Param("idMateria") Long idMateria);

    @Query(value = """
        SELECT COUNT(*)
        FROM MATERIA_TB
        WHERE LOWER(CODIGO) = LOWER(:codigo)
          AND CODIGO IS NOT NULL AND CODIGO <> ''
          AND (:idMateria IS NULL OR ID_MATERIA <> :idMateria)
    """, nativeQuery = true)
    long contarCodigoDuplicado(@Param("codigo") String codigo,
                              @Param("idMateria") Long idMateria);

    // CRUD

    @Query(value = """
        CALL MATERIA_INSERTAR(
            :nombre,
            :codigo,
            :idEstadoFk
        )
    """, nativeQuery = true)
    Long insertarMateriaRetornaId(
            @Param("nombre") String nombre,
            @Param("codigo") String codigo,
            @Param("idEstadoFk") Long idEstadoFk
    );

    @Modifying
    @Transactional
    @Query(value = """
        CALL MATERIA_MODIFICAR(
            :idMateria,
            :nombre,
            :codigo
        )
    """, nativeQuery = true)
    void modificarMateria(
            @Param("idMateria") Long idMateria,
            @Param("nombre") String nombre,
            @Param("codigo") String codigo
    );

    @Modifying
    @Transactional
    @Query(value = """
        CALL MATERIA_CAMBIAR_ESTADO(
            :idMateria,
            :idEstado
        )
    """, nativeQuery = true)
    void cambiarEstadoMateria(
            @Param("idMateria") Long idMateria,
            @Param("idEstado") Long idEstado
    );
}

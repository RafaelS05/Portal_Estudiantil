package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Aula;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AulaRepository extends JpaRepository<Aula, Long> {
    
    
    Aula findByIdAula(Long idAula);

    List<Aula> findByIdEstadoFk(Long idEstadoFk);

    long countByIdEstadoFk(Long idEstadoFk);

    Optional<Aula> findFirstByNumeroIgnoreCase(String numero);

    boolean existsByNumeroIgnoreCase(String numero);

    // validación duplicado (con excepción del mismo ID)
    @Query(value = """
        SELECT COUNT(*)
        FROM AULA_TB
        WHERE LOWER(NUMERO) = LOWER(:numero)
          AND (:idAula IS NULL OR ID_AULA <> :idAula)
    """, nativeQuery = true)
    long contarDuplicadoNumero(@Param("numero") String numero,
                               @Param("idAula") Long idAula);

    @Query(value = """
        SELECT *
        FROM AULA_TB
        WHERE ID_ESTADO_FK = 1
        ORDER BY NUMERO ASC
    """, nativeQuery = true)
    List<Aula> listarActivas();

    // CRUD

    @Query(value = """
        CALL AULA_INSERTAR(:numero, :idEstadoFk)
    """, nativeQuery = true)
    Long insertarAulaRetornaId(@Param("numero") String numero,
                               @Param("idEstadoFk") Long idEstadoFk);

    @Modifying
    @Transactional
    @Query(value = """
        CALL AULA_MODIFICAR(:idAula, :numero)
    """, nativeQuery = true)
    void modificarAula(@Param("idAula") Long idAula,
                       @Param("numero") String numero);

    @Modifying
    @Transactional
    @Query(value = """
        CALL AULA_CAMBIAR_ESTADO(:idAula, :idEstado)
    """, nativeQuery = true)
    void cambiarEstadoAula(@Param("idAula") Long idAula,
                           @Param("idEstado") Long idEstado);
}
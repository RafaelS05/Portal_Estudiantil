package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Seccion;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SeccionRepository extends JpaRepository<Seccion, Long> {
Seccion findByIdSeccion(Long idSeccion);

    List<Seccion> findByIdEstadoFk(Long idEstadoFk);

    long countByIdEstadoFk(Long idEstadoFk);

    // Proyección para resumen
    interface SeccionRow {
        Long getIdSeccion();
        String getNumero();
        Long getIdPeriodoFk();
        Long getIdEstadoFk();
        String getNombrePeriodo();
        Integer getCantidadEstudiantes();
        Integer getActiva();
    }

    @Query(value = """
        SELECT
            s.ID_SECCION AS idSeccion,
            s.NUMERO AS numero,
            s.ID_PERIODO_FK AS idPeriodoFk,
            s.ID_ESTADO_FK AS idEstadoFk,
            p.NOMBRE AS nombrePeriodo,
            0 AS cantidadEstudiantes,
            CASE WHEN s.ID_ESTADO_FK = 1 THEN 1 ELSE 0 END AS activa
        FROM SECCION_TB s
        JOIN PERIODOS_TB p ON p.ID_PERIODO = s.ID_PERIODO_FK
        ORDER BY p.FECHA_INICIO DESC, s.NUMERO ASC
    """, nativeQuery = true)
    List<SeccionRow> listarResumen();

    @Query(value = """
        SELECT
            s.ID_SECCION AS idSeccion,
            s.NUMERO AS numero,
            s.ID_PERIODO_FK AS idPeriodoFk,
            s.ID_ESTADO_FK AS idEstadoFk,
            p.NOMBRE AS nombrePeriodo,
            0 AS cantidadEstudiantes,
            CASE WHEN s.ID_ESTADO_FK = 1 THEN 1 ELSE 0 END AS activa
        FROM SECCION_TB s
        JOIN PERIODOS_TB p ON p.ID_PERIODO = s.ID_PERIODO_FK
        WHERE s.ID_PERIODO_FK = :idPeriodo
        ORDER BY s.NUMERO ASC
    """, nativeQuery = true)
    List<SeccionRow> listarPorPeriodo(@Param("idPeriodo") Long idPeriodo);

    // Consultas
    @Query(value = """
        SELECT *
        FROM SECCION_TB
        WHERE ID_ESTADO_FK = 1
        ORDER BY ID_SECCION DESC
    """, nativeQuery = true)
    List<Seccion> listarActivas();

    @Query(value = """
        SELECT *
        FROM SECCION_TB
        WHERE ID_PERIODO_FK = :idPeriodo
          AND ID_ESTADO_FK = 1
        ORDER BY NUMERO ASC
    """, nativeQuery = true)
    List<Seccion> listarActivasPorPeriodo(@Param("idPeriodo") Long idPeriodo);

    @Query(value = """
        SELECT *
        FROM SECCION_TB
        WHERE ID_PERIODO_FK = :idPeriodo
          AND LOWER(NUMERO) = LOWER(:numero)
        LIMIT 1
    """, nativeQuery = true)
    Optional<Seccion> findFirstByPeriodoYNumero(@Param("idPeriodo") Long idPeriodo,
                                                @Param("numero") String numero);

    // Validación duplicado (UQ: ID_PERIODO_FK + NUMERO)
    @Query(value = """
        SELECT COUNT(*)
        FROM SECCION_TB
        WHERE ID_PERIODO_FK = :idPeriodo
          AND LOWER(NUMERO) = LOWER(:numero)
          AND (:idSeccion IS NULL OR ID_SECCION <> :idSeccion)
    """, nativeQuery = true)
    long contarDuplicadoNumeroEnPeriodo(@Param("idPeriodo") Long idPeriodo,
                                        @Param("numero") String numero,
                                        @Param("idSeccion") Long idSeccion);

    // CRUD

    @Query(value = """
        CALL SECCION_INSERTAR(:numero, :idPeriodoFk, :idEstadoFk)
    """, nativeQuery = true)
    Long insertarSeccionRetornaId(@Param("numero") String numero,
                                  @Param("idPeriodoFk") Long idPeriodoFk,
                                  @Param("idEstadoFk") Long idEstadoFk);

    @Modifying
    @Transactional
    @Query(value = """
        CALL SECCION_MODIFICAR(:idSeccion, :numero, :idPeriodoFk)
    """, nativeQuery = true)
    void modificarSeccion(@Param("idSeccion") Long idSeccion,
                          @Param("numero") String numero,
                          @Param("idPeriodoFk") Long idPeriodoFk);

    @Modifying
    @Transactional
    @Query(value = """
        CALL SECCION_CAMBIAR_ESTADO(:idSeccion, :idEstado)
    """, nativeQuery = true)
    void cambiarEstadoSeccion(@Param("idSeccion") Long idSeccion,
                              @Param("idEstado") Long idEstado);
}
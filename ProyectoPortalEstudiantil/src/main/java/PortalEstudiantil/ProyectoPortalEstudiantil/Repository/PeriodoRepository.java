package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Periodo;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface PeriodoRepository extends JpaRepository<Periodo, Long> {

    Periodo findByIdPeriodo(Long idPeriodo);

    List<Periodo> findByIdEstadoFk(Long idEstadoFk);

    Optional<Periodo> findFirstByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    long countByIdEstadoFk(Long idEstadoFk);

    interface PeriodoRow {

        Long getIdPeriodo();

        String getNombre();

        LocalDate getFechaInicio();

        LocalDate getFechaFin();

        Long getIdEstadoFk();

        Integer getEnCurso();

        Integer getActivo();
    }
    
    

    @Query(value = """
        SELECT * FROM PERIODOS_TB
        WHERE ID_ESTADO_FK = 1
        ORDER BY FECHA_INICIO DESC
    """, nativeQuery = true)
    List<Periodo> listarActivos();

    @Query(value = """
        SELECT
            p.ID_PERIODO AS idPeriodo,
            p.NOMBRE AS nombre,
            p.FECHA_INICIO AS fechaInicio,
            p.FECHA_FIN AS fechaFin,
            p.ID_ESTADO_FK AS idEstadoFk,
            CASE WHEN CURDATE() BETWEEN p.FECHA_INICIO AND p.FECHA_FIN THEN 1 ELSE 0 END AS enCurso,
            CASE WHEN p.ID_ESTADO_FK = 1 THEN 1 ELSE 0 END AS activo
        FROM PERIODOS_TB p
        ORDER BY p.FECHA_INICIO DESC
    """, nativeQuery = true)
    List<PeriodoRow> listarResumen();

    @Query(value = """
        SELECT * FROM PERIODOS_TB
        WHERE CURDATE() BETWEEN FECHA_INICIO AND FECHA_FIN
          AND ID_ESTADO_FK = 1
        ORDER BY FECHA_INICIO DESC
        LIMIT 1
    """, nativeQuery = true)
    Periodo obtenerPeriodoActualEnCurso();

    @Query(value = """
        SELECT * FROM PERIODOS_TB
        WHERE :fecha BETWEEN FECHA_INICIO AND FECHA_FIN
        ORDER BY FECHA_INICIO DESC
        LIMIT 1
    """, nativeQuery = true)
    Periodo buscarPeriodoQueContieneFecha(@Param("fecha") LocalDate fecha);

    @Query(value = """
        SELECT * FROM PERIODOS_TB
        WHERE FECHA_INICIO >= :desde
          AND FECHA_FIN <= :hasta
        ORDER BY FECHA_INICIO DESC
    """, nativeQuery = true)
    List<Periodo> buscarPorRango(@Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);

    @Query(value = """
        SELECT COUNT(*) 
        FROM PERIODOS_TB
        WHERE LOWER(NOMBRE) = LOWER(:nombre)
          AND (:idPeriodo IS NULL OR ID_PERIODO <> :idPeriodo)
    """, nativeQuery = true)
    long contarNombreDuplicado(@Param("nombre") String nombre,
            @Param("idPeriodo") Long idPeriodo);

    @Query(value = """
        SELECT COUNT(*)
        FROM PERIODOS_TB p
        WHERE (:idPeriodo IS NULL OR p.ID_PERIODO <> :idPeriodo)
          AND p.ID_ESTADO_FK = 1
          AND :fechaInicio <= p.FECHA_FIN
          AND :fechaFin >= p.FECHA_INICIO
    """, nativeQuery = true)
    long contarTraslapesActivos(@Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("idPeriodo") Long idPeriodo);

    // -------------------------
    // CRUD via PROCEDURES
    // -------------------------
    @Modifying
    @Transactional
    @Query(value = """
    CALL PERIODOS_INSERTAR(
        :nombre,
        :fechaInicio,
        :fechaFin,
        :idEstadoFk
    )
""", nativeQuery = true)
    Long insertarPeriodoRetornaId(
            @Param("nombre") String nombre,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("idEstadoFk") Long idEstadoFk
    );

    @Modifying
    @Transactional
    @Query(value = """
        CALL PERIODOS_MODIFICAR(
            :idPeriodo,
            :nombre,
            :fechaInicio,
            :fechaFin
        )
    """, nativeQuery = true)
    void modificarPeriodo(
            @Param("idPeriodo") Long idPeriodo,
            @Param("nombre") String nombre,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );

    @Modifying
    @Transactional
    @Query(value = """
        CALL PERIODOS_CAMBIAR_ESTADO(
            :idPeriodo,
            :idEstado
        )
    """, nativeQuery = true)
    void cambiarEstadoPeriodo(
            @Param("idPeriodo") Long idPeriodo,
            @Param("idEstado") Long idEstado
    );

}

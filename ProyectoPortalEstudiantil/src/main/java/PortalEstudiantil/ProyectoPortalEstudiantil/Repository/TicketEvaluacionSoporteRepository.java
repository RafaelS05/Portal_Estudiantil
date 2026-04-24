package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.TicketEvaluacionSoporte;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketEvaluacionSoporteRepository extends JpaRepository<TicketEvaluacionSoporte, Long>{

    // SP: EVALUACIONSOPORTE_INSERTAR
    @Modifying
    @Query(value = """
            CALL EVALUACIONSOPORTE_INSERTAR(:puntuacion, :comentario, :fechaEvaluacion,
                                            :idUsuarioFk, :idTicketFk, :idEstadoFk)
            """, nativeQuery = true)
    void insertar(@Param("puntuacion") Integer puntuacion,
                  @Param("comentario") String comentario,
                  @Param("fechaEvaluacion") LocalDate fechaEvaluacion,
                  @Param("idUsuarioFk") Long idUsuarioFk,
                  @Param("idTicketFk") Long idTicketFk,
                  @Param("idEstadoFk") Long idEstadoFk);

    // SP: EVALUACIONSOPORTE_MODIFICAR
    @Modifying
    @Query(value = """
            CALL EVALUACIONSOPORTE_MODIFICAR(:idEvaluacionSoporte, :puntuacion, :comentario,
                                             :fechaEvaluacion, :idUsuarioFk, :idTicketFk)
            """, nativeQuery = true)
    void modificar(@Param("idEvaluacionSoporte") Long idEvaluacionSoporte,
                   @Param("puntuacion") Integer puntuacion,
                   @Param("comentario") String comentario,
                   @Param("fechaEvaluacion") LocalDate fechaEvaluacion,
                   @Param("idUsuarioFk") Long idUsuarioFk,
                   @Param("idTicketFk") Long idTicketFk);

    // SP: EVALUACIONSOPORTE_CAMBIAR_ESTADO
    @Modifying
    @Query(value = "CALL EVALUACIONSOPORTE_CAMBIAR_ESTADO(:idEvaluacionSoporte, :idEstado)",
           nativeQuery = true)
    void cambiarEstado(@Param("idEvaluacionSoporte") Long idEvaluacionSoporte,
                       @Param("idEstado") Long idEstado);

    Optional<TicketEvaluacionSoporte> findByIdTicketFk(Long idTicketFk);
    List<TicketEvaluacionSoporte> findByIdUsuarioFk(Long idUsuarioFk);
    boolean existsByIdUsuarioFkAndIdTicketFk(Long idUsuarioFk, Long idTicketFk);
}

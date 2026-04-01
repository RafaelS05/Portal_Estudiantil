package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.TicketAdjunto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketAdjuntoRepository extends JpaRepository<TicketAdjunto, Long>{
    
    // SP: ADJUNTOTICKET_INSERTAR
    @Modifying
    @Query(value = """
            CALL ADJUNTOTICKET_INSERTAR(:rutaArchivo, :idUsuarioSubidoFk,
                                        :idTicketFk, :idEstadoFk)
            """, nativeQuery = true)
    void insertar(@Param("rutaArchivo") String rutaArchivo,
                  @Param("idUsuarioSubidoFk") Long idUsuarioSubidoFk,
                  @Param("idTicketFk") Long idTicketFk,
                  @Param("idEstadoFk") Long idEstadoFk);

    // SP: ADJUNTOTICKET_MODIFICAR
    @Modifying
    @Query(value = """
            CALL ADJUNTOTICKET_MODIFICAR(:idAdjuntoTicket, :rutaArchivo,
                                         :idUsuarioSubidoFk, :idTicketFk)
            """, nativeQuery = true)
    void modificar(@Param("idAdjuntoTicket") Long idAdjuntoTicket,
                   @Param("rutaArchivo") String rutaArchivo,
                   @Param("idUsuarioSubidoFk") Long idUsuarioSubidoFk,
                   @Param("idTicketFk") Long idTicketFk);

    // SP: ADJUNTOTICKET_CAMBIAR_ESTADO
    @Modifying
    @Query(value = "CALL ADJUNTOTICKET_CAMBIAR_ESTADO(:idAdjuntoTicket, :idEstado)",
           nativeQuery = true)
    void cambiarEstado(@Param("idAdjuntoTicket") Long idAdjuntoTicket,
                       @Param("idEstado") Long idEstado);

    // Buscar todos los adjuntos de un ticket
    List<TicketAdjunto> findByIdTicketFk(Long idTicketFk);
}

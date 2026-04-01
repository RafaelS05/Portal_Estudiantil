package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Ticket;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository
        extends JpaRepository<Ticket, Long> {

    // SP: TICKET_INSERTAR
    @Modifying
    @Query(value = """
            CALL TICKET_INSERTAR(:titulo, :comentario, :fechaCreacionTicket,
                                 :fechaCierre, :prioridad, :idUsuarioReportaFk,
                                 :idUsuarioTecnicoFk, :idCategoriaFk, :idEstadoFk)
            """, nativeQuery = true)
    void insertar(@Param("titulo") String titulo,
            @Param("comentario") String comentario,
            @Param("fechaCreacionTicket") LocalDate fechaCreacionTicket,
            @Param("fechaCierre") LocalDate fechaCierre,
            @Param("prioridad") String prioridad,
            @Param("idUsuarioReportaFk") Long idUsuarioReportaFk,
            @Param("idUsuarioTecnicoFk") Long idUsuarioTecnicoFk,
            @Param("idCategoriaFk") Long idCategoriaFk,
            @Param("idEstadoFk") Long idEstadoFk);

    // SP: TICKET_MODIFICAR
    @Modifying
    @Query(value = """
            CALL TICKET_MODIFICAR(:idTicket, :titulo, :comentario, :fechaCreacionTicket,
                                  :fechaCierre, :prioridad, :idUsuarioReportaFk,
                                  :idUsuarioTecnicoFk, :idCategoriaFk)
            """, nativeQuery = true)
    void modificar(@Param("idTicket") Long idTicket,
            @Param("titulo") String titulo,
            @Param("comentario") String comentario,
            @Param("fechaCreacionTicket") LocalDate fechaCreacionTicket,
            @Param("fechaCierre") LocalDate fechaCierre,
            @Param("prioridad") String prioridad,
            @Param("idUsuarioReportaFk") Long idUsuarioReportaFk,
            @Param("idUsuarioTecnicoFk") Long idUsuarioTecnicoFk,
            @Param("idCategoriaFk") Long idCategoriaFk);

    // SP: TICKET_CAMBIAR_ESTADO
    @Modifying
    @Query(value = "CALL TICKET_CAMBIAR_ESTADO(:idTicket, :idEstado)",
            nativeQuery = true)
    void cambiarEstado(@Param("idTicket") Long idTicket,
            @Param("idEstado") Long idEstado);

    // Consultas para las vistas (usuario ve solo los suyos, admin ve todos)
    List<Ticket> findByIdUsuarioReportaFk(Long idUsuario);

    List<Ticket> findByIdEstadoFk(Long idEstado);

    List<Ticket> findByIdCategoriaFk(Long idCategoria);

    List<Ticket> findByIdUsuarioReportaFkAndIdEstadoFk(Long idUsuario, Long idEstado);

    List<Ticket> findByIdUsuarioReportaFkAndIdCategoriaFk(Long idUsuario, Long idCategoria);

    // Filtro combinado estado + categoría para el admin
    List<Ticket> findByIdEstadoFkAndIdCategoriaFk(Long idEstado, Long idCategoria);
}

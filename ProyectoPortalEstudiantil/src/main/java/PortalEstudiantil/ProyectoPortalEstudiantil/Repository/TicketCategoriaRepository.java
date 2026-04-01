package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.TicketCategoria;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketCategoriaRepository extends JpaRepository<TicketCategoria, Long> {

    @Modifying
    @Query(value = "CALL CATEGORIATICKET_CAMBIAR_ESTADO(:idCategoria, :idEstado)",
           nativeQuery = true)
    void cambiarEstado(@Param("idCategoria") Long idCategoria,
                       @Param("idEstado") Long idEstado);

    List<TicketCategoria> findByIdEstadoFk(Long idEstado);
    Optional<TicketCategoria> findByNombre(String nombre);
    
}

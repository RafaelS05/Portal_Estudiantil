package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.EvaluacionSoporte;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluacionSoporteRepository {

    Optional<EvaluacionSoporte> findByIdTicketFk(Long idTicketFk);

    List<EvaluacionSoporte> findByIdUsuarioFk(Long idUsuarioFk);

    // Verificar si el usuario ya evaluó ese ticket (restricción UNIQUE del DDL)
    boolean existsByIdUsuarioFkAndIdTicketFk(Long idUsuarioFk, Long idTicketFk);
}

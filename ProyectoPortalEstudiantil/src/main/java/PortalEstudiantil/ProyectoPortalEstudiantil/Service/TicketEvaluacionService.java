package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.TicketAdjunto;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.TicketEvaluacionSoporte;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.TicketEvaluacionSoporteRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TicketEvaluacionService {

    @Autowired
    private TicketEvaluacionSoporteRepository repo;

    public void insertar(TicketEvaluacionSoporte evaluacion) {
        if (repo.existsByIdUsuarioFkAndIdTicketFk(
                evaluacion.getIdUsuarioFk(), evaluacion.getIdTicketFk())) {
            throw new RuntimeException("El usuario ya evaluó este ticket.");
        }

        repo.insertar(
                evaluacion.getPuntuacion(),
                evaluacion.getComentario(),
                LocalDate.now(),
                evaluacion.getIdUsuarioFk(),
                evaluacion.getIdTicketFk(),
                11L
        );
    }

    public void modificar(Long idEvaluacion, TicketEvaluacionSoporte evaluacion) {
        repo.modificar(
                idEvaluacion,
                evaluacion.getPuntuacion(),
                evaluacion.getComentario(),
                LocalDate.now(),
                evaluacion.getIdUsuarioFk(),
                evaluacion.getIdTicketFk()
        );
    }

    public void cambiarEstado(Long idEvaluacion, Long idEstado) {
        repo.cambiarEstado(idEvaluacion, idEstado);
    }

    @Transactional(readOnly = true)
    public Optional<TicketEvaluacionSoporte> buscarPorTicket(Long idTicketFk) {
        return repo.findByIdTicketFk(idTicketFk);
    }

    @Transactional(readOnly = true)
    public List<TicketEvaluacionSoporte> listarPorUsuario(Long idUsuarioFk) {
        return repo.findByIdUsuarioFk(idUsuarioFk);
    }

    @Transactional(readOnly = true)
    public boolean yaEvaluo(Long idUsuarioFk, Long idTicketFk) {
        return repo.existsByIdUsuarioFkAndIdTicketFk(idUsuarioFk, idTicketFk);
    }

}

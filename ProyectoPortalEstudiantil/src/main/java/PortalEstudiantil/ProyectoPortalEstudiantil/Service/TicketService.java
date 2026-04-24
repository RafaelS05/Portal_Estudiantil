package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Ticket;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.TicketRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TicketService {

    @Autowired
    private TicketRepository repo;

    public void insertar(Ticket ticket) {
        repo.insertar(
                ticket.getTitulo(),
                ticket.getComentario(),
                LocalDate.now(),
                ticket.getFechaCierre() != null
                ? ticket.getFechaCierre().toLocalDate() : null,
                ticket.getPrioridad(),
                ticket.getIdUsuarioReportaFk(),
                ticket.getIdUsuarioTecnicoFk(),
                ticket.getIdCategoriaFk(),
                1L // idEstadoFk = 1 "Nuevo" siempre al crear
        );
    }

    public void modificar(Long idTicket, Ticket ticket) {
        repo.findById(idTicket)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado: " + idTicket));
        repo.modificar(
                idTicket,
                ticket.getTitulo(),
                ticket.getComentario(),
                LocalDate.now(),
                ticket.getFechaCierre() != null
                ? ticket.getFechaCierre().toLocalDate() : null,
                ticket.getPrioridad(),
                ticket.getIdUsuarioReportaFk(),
                ticket.getIdUsuarioTecnicoFk(),
                ticket.getIdCategoriaFk()
        );
    }

    public void cambiarEstado(Long idTicket, Long idEstado) {
        repo.findById(idTicket)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado: " + idTicket));
        repo.cambiarEstado(idTicket, idEstado);
    }

    @Transactional
    public Optional<Ticket> buscarPorId(Long idTicket) {
        return repo.findById(idTicket);
    }

    @Transactional
    public List<Ticket> listarTodos() {
        return repo.findAll();
    }

    @Transactional
    public List<Ticket> filtrarAdmin(Long idEstado, Long idCategoria) {
        if (idEstado != null && idCategoria != null) {
            return repo.findByIdEstadoFkAndIdCategoriaFk(idEstado, idCategoria);
        }
        if (idEstado != null) {
            return repo.findByIdEstadoFk(idEstado);
        }
        if (idCategoria != null) {
            return repo.findByIdCategoriaFk(idCategoria);
        }
        return repo.findAll();
    }

    @Transactional
    public List<Ticket> filtrarUsuario(Long idUsuario, Long idEstado, Long idCategoria) {
        if (idEstado != null) {
            return repo.findByIdUsuarioReportaFkAndIdEstadoFk(idUsuario, idEstado);
        }
        if (idCategoria != null) {
            return repo.findByIdUsuarioReportaFkAndIdCategoriaFk(idUsuario, idCategoria);
        }
        return repo.findByIdUsuarioReportaFk(idUsuario);
    }

}

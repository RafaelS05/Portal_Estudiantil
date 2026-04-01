package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.TicketCategoria;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.TicketCategoriaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TicketCategoriaService {

    @Autowired
    private TicketCategoriaRepository ticketCategoriaRepo;

    @Transactional
    public List<TicketCategoria> listarTodas() {
        return ticketCategoriaRepo.findAll();
    }

    @Transactional
    public List<TicketCategoria> listarActivas() {
        return ticketCategoriaRepo.findByIdEstadoFk(1L);
    }

    @Transactional
    public Optional<TicketCategoria> buscarPorId(Long id) {
        return ticketCategoriaRepo.findById(id);
    }

    @Transactional
    public Optional<TicketCategoria> buscarPorNombre(String nombre) {
        return ticketCategoriaRepo.findByNombre(nombre);
    }

    public void cambiarEstado(Long idCategoria, Long idEstado) {
        ticketCategoriaRepo.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada: " + idCategoria));
        ticketCategoriaRepo.cambiarEstado(idCategoria, idEstado);
    }
}

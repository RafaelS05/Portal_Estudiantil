package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.TicketAdjunto;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.TicketAdjuntoRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TicketAdjuntoService {
    
    @Autowired
    private TicketAdjuntoRepository repo;
    
     public void insertar(TicketAdjunto adjunto) {
        repo.insertar(
            adjunto.getRutaArchivo(),
            adjunto.getIdUsuarioSubidoFk(),
            adjunto.getIdTicketFk(),
            11L  // idEstadoFk = 1 Abierto
        );
    }

    public void modificar(Long idAdjunto, TicketAdjunto adjunto) {
        repo.modificar(
            idAdjunto,
            adjunto.getRutaArchivo(),
            adjunto.getIdUsuarioSubidoFk(),
            adjunto.getIdTicketFk()
        );
    }

    public void cambiarEstado(Long idAdjunto, Long idEstado) {
        repo.cambiarEstado(idAdjunto, idEstado);
    }

    @Transactional
    public List<TicketAdjunto> listarPorTicket(Long idTicketFk) {
        return repo.findByIdTicketFk(idTicketFk);
    }
}

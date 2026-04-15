package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Direccion;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.DireccionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DireccionService {

    private final DireccionRepository direccionRepo;

    public DireccionService(DireccionRepository direccionRepo) {
        this.direccionRepo = direccionRepo;
    }

    public Direccion obtenerPorUsuario(Long idUsuario) {
        return direccionRepo.findByUsuario_IdUsuario(idUsuario);
    }

    @Transactional
    public void guardarDireccion(Long idUsuario, Long idProvincia, Long idCanton,
                                  Long idDistrito, String otrasSenas) {
        if (idProvincia == null && idCanton == null && idDistrito == null
                && (otrasSenas == null || otrasSenas.isBlank())) {
            return; // nada que guardar
        }

        Direccion existente = obtenerPorUsuario(idUsuario);
        if (existente != null && existente.getIdDireccion() != null) {
            direccionRepo.modificarDireccion(
                    existente.getIdDireccion(),
                    otrasSenas != null ? otrasSenas.trim() : "",
                    idUsuario,
                    idProvincia,
                    idCanton,
                    idDistrito
            );
        } else {
            direccionRepo.insertarDireccion(
                    otrasSenas != null ? otrasSenas.trim() : "",
                    idUsuario,
                    idProvincia,
                    idCanton,
                    idDistrito,
                    1L
            );
        }
    }
}
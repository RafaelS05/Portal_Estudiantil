package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.AsistenciaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;

    public AsistenciaService(AsistenciaRepository asistenciaRepository) {
        this.asistenciaRepository = asistenciaRepository;
    }

    public List<AsistenciaRepository.AsistenciaListadoRow> listarAsistencias(String busqueda) {
        return asistenciaRepository.listarAsistenciasFrontend(busqueda);
    }
    
    public void eliminarAsistencia(Long id) {
        asistenciaRepository.deleteById(id);
    }
}
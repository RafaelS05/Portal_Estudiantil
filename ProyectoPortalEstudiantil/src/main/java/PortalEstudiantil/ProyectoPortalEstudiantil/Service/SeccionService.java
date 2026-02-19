package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Seccion;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.PeriodoRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.SeccionRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeccionService {
    @Autowired
    private SeccionRepository seccionRepository;

    @Autowired
    private PeriodoRepository periodoRepository;

    public List<SeccionRepository.SeccionRow> listarResumen() {
        return seccionRepository.listarResumen();
    }

    public List<SeccionRepository.SeccionRow> listarPorPeriodo(Long idPeriodo) {
        return seccionRepository.listarPorPeriodo(idPeriodo);
    }

    public List<Seccion> listarActivas() {
        return seccionRepository.listarActivas();
    }

    public List<Seccion> listarActivasPorPeriodo(Long idPeriodo) {
        return seccionRepository.listarActivasPorPeriodo(idPeriodo);
    }

    public Seccion buscarPorId(Long id) {
        return seccionRepository.findByIdSeccion(id);
    }

    @Transactional
    public Long insertar(String numero, Long idPeriodoFk) {
        if (seccionRepository.contarDuplicadoNumeroEnPeriodo(idPeriodoFk, numero, null) > 0) {
            throw new IllegalArgumentException(
                    "Ya existe la sección '" + numero + "' en ese período.");
        }
        return seccionRepository.insertarSeccionRetornaId(numero.trim().toUpperCase(), idPeriodoFk, 1L);
    }

    @Transactional
    public void modificar(Long idSeccion, String numero, Long idPeriodoFk) {
        Seccion existente = seccionRepository.findByIdSeccion(idSeccion);
        if (existente == null) {
            throw new IllegalArgumentException("Sección no encontrada: " + idSeccion);
        }
        if (seccionRepository.contarDuplicadoNumeroEnPeriodo(idPeriodoFk, numero, idSeccion) > 0) {
            throw new IllegalArgumentException(
                    "Ya existe la sección '" + numero + "' en ese período.");
        }
        seccionRepository.modificarSeccion(idSeccion, numero.trim().toUpperCase(), idPeriodoFk);
    }

    @Transactional
    public void cambiarEstado(Long idSeccion, Long idEstado) {
        seccionRepository.cambiarEstadoSeccion(idSeccion, idEstado);
    }

    public long contarActivas() {
        return seccionRepository.countByIdEstadoFk(1L);
    }
}

package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.SeccionMateria;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.MateriaRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.SeccionMateriaRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.SeccionMateriaRepository.SeccionMateriaRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.SeccionRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeccionMateriaService {

    @Autowired
    private SeccionMateriaRepository seccionMateriaRepository;

    @Autowired
    private SeccionRepository seccionRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    public List<SeccionMateriaRow> listarTodas() {
        return seccionMateriaRepository.listarResumen();
    }

    public List<SeccionMateria> listarActivas() {
        return seccionMateriaRepository.listarActivas();
    }

    public List<SeccionMateria> listarPorSeccion(Long idSeccion) {
        return seccionMateriaRepository.listarPorSeccion(idSeccion);
    }

    public List<SeccionMateria> listarPorDocente(Long idDocente) {
        return seccionMateriaRepository.listarPorDocente(idDocente);
    }

    public SeccionMateria buscarPorId(Long id) {
        return seccionMateriaRepository.findByIdSeccionMateria(id);
    }

    public List<SeccionMateriaRow> listarResumenPorDocente(Long idDocente) {
        return seccionMateriaRepository.listarResumenPorDocente(idDocente);
    }

    @Transactional
    public Long insertar(Long idSeccionFk, Long idMateriaFk, Long idUsuarioDocenteFk) {
        if (seccionRepository.findByIdSeccion(idSeccionFk) == null) {
            throw new IllegalArgumentException("La sección seleccionada no existe.");
        }
        if (materiaRepository.findByIdMateria(idMateriaFk) == null) {
            throw new IllegalArgumentException("La materia seleccionada no existe.");
        }
        if (seccionMateriaRepository.contarDuplicado(
                idSeccionFk, idMateriaFk, idUsuarioDocenteFk, null) > 0) {
            throw new IllegalArgumentException(
                    "Esa combinación de sección, materia y docente ya existe.");
        }
        return seccionMateriaRepository.insertarSeccionMateriaRetornaId(
                idSeccionFk, idMateriaFk, idUsuarioDocenteFk, 1L);
    }

    @Transactional
    public void modificar(Long idSeccionMateria, Long idSeccionFk,
            Long idMateriaFk, Long idUsuarioDocenteFk) {
        SeccionMateria existente = seccionMateriaRepository.findByIdSeccionMateria(idSeccionMateria);
        if (existente == null) {
            throw new IllegalArgumentException("Asignación no encontrada: " + idSeccionMateria);
        }
        if (seccionMateriaRepository.contarDuplicado(
                idSeccionFk, idMateriaFk, idUsuarioDocenteFk, idSeccionMateria) > 0) {
            throw new IllegalArgumentException(
                    "Esa combinación de sección, materia y docente ya existe.");
        }
        seccionMateriaRepository.modificarSeccionMateria(
                idSeccionMateria, idSeccionFk, idMateriaFk, idUsuarioDocenteFk);
    }

    @Transactional
    public void cambiarEstado(Long idSeccionMateria, Long idEstado) {
        seccionMateriaRepository.cambiarEstadoSeccionMateria(idSeccionMateria, idEstado);
    }

    public long contarActivas() {
        return seccionMateriaRepository.countByIdEstadoFk(1L);
    }
}

package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Materia;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.MateriaRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HorarioService {

    @Autowired
    private MateriaRepository materiaRepository;

    public List<Materia> listarTodas() {
        return materiaRepository.findAll();
    }

    public List<Materia> listarActivas() {
        return materiaRepository.listarActivas();
    }

    public Materia buscarPorId(Long id) {
        return materiaRepository.findByIdMateria(id);
    }

    public List<Materia> buscar(String texto) {
        if (texto == null || texto.isBlank()) {
            return listarTodas();
        }
        return materiaRepository.buscarPorNombreOCodigo(texto.trim());
    }

    @Transactional
    public Long insertar(String nombre, String codigo) {
        if (materiaRepository.contarNombreDuplicado(nombre, null) > 0) {
            throw new IllegalArgumentException("Ya existe una materia con el nombre: " + nombre);
        }
        if (codigo != null && !codigo.isBlank()
                && materiaRepository.contarCodigoDuplicado(codigo, null) > 0) {
            throw new IllegalArgumentException("Ya existe una materia con el código: " + codigo);
        }
        String codigoFinal = (codigo != null && !codigo.isBlank()) ? codigo.trim() : null;
        return materiaRepository.insertarMateriaRetornaId(nombre.trim(), codigoFinal, 1L);
    }

    @Transactional
    public void modificar(Long idMateria, String nombre, String codigo) {
        Materia existente = materiaRepository.findByIdMateria(idMateria);
        if (existente == null) {
            throw new IllegalArgumentException("Materia no encontrada: " + idMateria);
        }
        if (materiaRepository.contarNombreDuplicado(nombre, idMateria) > 0) {
            throw new IllegalArgumentException("Ya existe una materia con el nombre: " + nombre);
        }
        if (codigo != null && !codigo.isBlank()
                && materiaRepository.contarCodigoDuplicado(codigo, idMateria) > 0) {
            throw new IllegalArgumentException("Ya existe una materia con el código: " + codigo);
        }
        String codigoFinal = (codigo != null && !codigo.isBlank()) ? codigo.trim() : null;
        materiaRepository.modificarMateria(idMateria, nombre.trim(), codigoFinal);
    }

    @Transactional
    public void cambiarEstado(Long idMateria, Long idEstado) {
        materiaRepository.cambiarEstadoMateria(idMateria, idEstado);
    }

    public long contarActivas() {
        return materiaRepository.countByIdEstadoFk(1L);
    }
}

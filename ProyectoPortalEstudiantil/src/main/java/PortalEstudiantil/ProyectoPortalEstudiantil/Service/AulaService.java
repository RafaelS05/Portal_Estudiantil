package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Aula;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.AulaRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AulaService {
    

    @Autowired
    private AulaRepository aulaRepository;

    public List<Aula> listarTodas() {
        return aulaRepository.findAll();
    }

    public List<Aula> listarActivas() {
        return aulaRepository.listarActivas();
    }

    public Aula buscarPorId(Long id) {
        return aulaRepository.findByIdAula(id);
    }

    public boolean existeNumero(String numero, Long idAulaExcluir) {
        return aulaRepository.contarDuplicadoNumero(numero, idAulaExcluir) > 0;
    }

    @Transactional
    public Long insertar(String numero) {
        if (existeNumero(numero, null)) {
            throw new IllegalArgumentException("Ya existe un aula con el número: " + numero);
        }
        return aulaRepository.insertarAulaRetornaId(numero, 1L);
    }

    @Transactional
    public void modificar(Long idAula, String numero) {
        Aula existente = aulaRepository.findByIdAula(idAula);
        if (existente == null) {
            throw new IllegalArgumentException("Aula no encontrada: " + idAula);
        }
        if (existeNumero(numero, idAula)) {
            throw new IllegalArgumentException("Ya existe un aula con el número: " + numero);
        }
        aulaRepository.modificarAula(idAula, numero);
    }

    @Transactional
    public void cambiarEstado(Long idAula, Long idEstado) {
        aulaRepository.cambiarEstadoAula(idAula, idEstado);
    }

    public long contarActivas() {
        return aulaRepository.countByIdEstadoFk(1L);
    }
}

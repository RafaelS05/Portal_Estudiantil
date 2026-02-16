package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Aula;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.AulaRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AulaService {
    
    private static final Logger log = LoggerFactory.getLogger(AulaService.class);

    private static final Long ESTADO_ACTIVO = 1L;
    private static final Long ESTADO_INACTIVO = 2L;

    private final AulaRepository aulaRepository;

    public AulaService(AulaRepository aulaRepository) {
        this.aulaRepository = aulaRepository;
    }

    private String normalizarNumero(String numero) {
        if (numero == null) return null;
        String n = numero.trim();
        return n.isEmpty() ? null : n.toUpperCase();
    }

    private void validarNumero(String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de aula es obligatorio.");
        }
    }

    private void validarDuplicado(String numero, Long idAula) {
        long dup = aulaRepository.contarDuplicadoNumero(numero, idAula);
        if (dup > 0) {
            throw new IllegalArgumentException("Ya existe un aula con el número '" + numero + "'.");
        }
    }

    // CRUD

    @Transactional
    public Long crearAula(String numero, Long idEstadoFk) {

        if (idEstadoFk == null) throw new IllegalArgumentException("El estado es obligatorio.");

        validarNumero(numero);
        String n = normalizarNumero(numero);

        validarDuplicado(n, null);

        Long id = aulaRepository.insertarAulaRetornaId(n, idEstadoFk);

        if (id == null || id <= 0) {
            throw new RuntimeException("AULA_INSERTAR no retornó un ID válido (idAula).");
        }

        log.info("Aula creada con ID: {}", id);
        return id;
    }

    @Transactional
    public Long crearAula(Aula aula) {
        if (aula == null) throw new IllegalArgumentException("El aula es obligatoria.");
        return crearAula(aula.getNumero(), aula.getIdEstadoFk());
    }

    @Transactional
    public void actualizarAula(Long idAula, String numero) {

        if (idAula == null) throw new IllegalArgumentException("El ID del aula es obligatorio.");
        obtenerAula(idAula);

        validarNumero(numero);
        String n = normalizarNumero(numero);

        validarDuplicado(n, idAula);

        aulaRepository.modificarAula(idAula, n);
        log.info("Aula ID {} actualizada.", idAula);
    }

    @Transactional
    public void actualizarAula(Aula aula) {
        if (aula == null) throw new IllegalArgumentException("El aula es obligatoria.");
        if (aula.getIdAula() == null) {
            throw new IllegalArgumentException("El ID del aula es obligatorio para actualizar.");
        }
        actualizarAula(aula.getIdAula(), aula.getNumero());
    }

    // Estado

    @Transactional
    public void cambiarEstadoAula(Long idAula, Long idEstado) {
        if (idAula == null) throw new IllegalArgumentException("El ID del aula es obligatorio.");
        if (idEstado == null) throw new IllegalArgumentException("El estado es obligatorio.");

        obtenerAula(idAula);

        aulaRepository.cambiarEstadoAula(idAula, idEstado);

        log.info("Aula ID {} estado cambiado a {}", idAula,
                ESTADO_ACTIVO.equals(idEstado) ? "ACTIVA" : "INACTIVA");
    }

    @Transactional
    public void activarAula(Long idAula) {
        cambiarEstadoAula(idAula, ESTADO_ACTIVO);
    }

    @Transactional
    public void desactivarAula(Long idAula) {
        cambiarEstadoAula(idAula, ESTADO_INACTIVO);
    }

    // Consultas

    public Aula obtenerAula(Long idAula) {
        return aulaRepository.findById(idAula)
                .orElseThrow(() -> new RuntimeException("Aula no encontrada con ID: " + idAula));
    }

    public List<Aula> listarTodas() {
        return aulaRepository.findAll();
    }

    public List<Aula> listarActivas() {
        return aulaRepository.listarActivas();
    }

    public long contarPorEstado(Long idEstadoFk) {
        if (idEstadoFk == null) throw new IllegalArgumentException("El estado es obligatorio.");
        return aulaRepository.countByIdEstadoFk(idEstadoFk);
    }
}

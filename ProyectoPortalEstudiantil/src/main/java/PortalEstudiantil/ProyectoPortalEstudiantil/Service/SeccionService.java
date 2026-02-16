
package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Seccion;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.PeriodoRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.SeccionRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SeccionService {
    
    private static final Logger log = LoggerFactory.getLogger(SeccionService.class);

    private static final Long ESTADO_ACTIVO = 1L;
    private static final Long ESTADO_INACTIVO = 2L;

    private final SeccionRepository seccionRepository;
    private final PeriodoRepository periodoRepository;

    public SeccionService(SeccionRepository seccionRepository,
                          PeriodoRepository periodoRepository) {
        this.seccionRepository = seccionRepository;
        this.periodoRepository = periodoRepository;
    }

    private String normalizar(String s) {
        return s == null ? null : s.trim();
    }

    private void validarNumero(String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de sección es obligatorio.");
        }
        String n = numero.trim().toUpperCase();
        if (!n.matches("^\\d{1,2}-[A-Z]$")) {
            throw new IllegalArgumentException("El número de sección debe tener formato 7-A, 8-B, 10-C.");
        }
    }

    private void validarPeriodoExiste(Long idPeriodo) {
        if (idPeriodo == null) throw new IllegalArgumentException("El período es obligatorio.");
        if (!periodoRepository.existsById(idPeriodo)) {
            throw new RuntimeException("No existe un período con ID: " + idPeriodo);
        }
    }

    private void validarDuplicado(Long idPeriodo, String numero, Long idSeccion) {
        long dup = seccionRepository.contarDuplicadoNumeroEnPeriodo(idPeriodo, numero, idSeccion);
        if (dup > 0) {
            throw new IllegalArgumentException("Ya existe la sección '" + numero + "' en ese período.");
        }
    }

    // CRUD

    @Transactional
    public Long crearSeccion(String numero, Long idPeriodoFk, Long idEstadoFk) {

        numero = normalizar(numero);
        if (numero != null) numero = numero.toUpperCase();

        if (idEstadoFk == null) throw new IllegalArgumentException("El estado es obligatorio.");

        validarNumero(numero);
        validarPeriodoExiste(idPeriodoFk);
        validarDuplicado(idPeriodoFk, numero, null);

        Long id = seccionRepository.insertarSeccionRetornaId(numero, idPeriodoFk, idEstadoFk);

        if (id == null || id <= 0) {
            throw new RuntimeException("SECCION_INSERTAR no retornó un ID válido (idSeccion).");
        }

        log.info("Sección creada con ID: {}", id);
        return id;
    }

    @Transactional
    public Long crearSeccion(Seccion seccion) {
        if (seccion == null) throw new IllegalArgumentException("La sección es obligatoria.");
        return crearSeccion(seccion.getNumero(), seccion.getIdPeriodoFk(), seccion.getIdEstadoFk());
    }

    @Transactional
    public void actualizarSeccion(Long idSeccion, String numero, Long idPeriodoFk) {

        if (idSeccion == null) throw new IllegalArgumentException("El ID de la sección es obligatorio.");
        obtenerSeccion(idSeccion);

        numero = normalizar(numero);
        if (numero != null) numero = numero.toUpperCase();

        validarNumero(numero);
        validarPeriodoExiste(idPeriodoFk);
        validarDuplicado(idPeriodoFk, numero, idSeccion);

        seccionRepository.modificarSeccion(idSeccion, numero, idPeriodoFk);

        log.info("Sección ID {} actualizada.", idSeccion);
    }

    @Transactional
    public void actualizarSeccion(Seccion seccion) {
        if (seccion == null) throw new IllegalArgumentException("La sección es obligatoria.");
        if (seccion.getIdSeccion() == null) {
            throw new IllegalArgumentException("El ID de la sección es obligatorio para actualizar.");
        }
        actualizarSeccion(seccion.getIdSeccion(), seccion.getNumero(), seccion.getIdPeriodoFk());
    }

    // Estado

    @Transactional
    public void cambiarEstadoSeccion(Long idSeccion, Long idEstado) {
        if (idSeccion == null) throw new IllegalArgumentException("El ID de la sección es obligatorio.");
        if (idEstado == null) throw new IllegalArgumentException("El estado es obligatorio.");

        obtenerSeccion(idSeccion);

        seccionRepository.cambiarEstadoSeccion(idSeccion, idEstado);

        log.info("Sección ID {} estado cambiado a {}", idSeccion,
                ESTADO_ACTIVO.equals(idEstado) ? "ACTIVA" : "INACTIVA");
    }

    @Transactional
    public void activarSeccion(Long idSeccion) {
        cambiarEstadoSeccion(idSeccion, ESTADO_ACTIVO);
    }

    @Transactional
    public void desactivarSeccion(Long idSeccion) {
        cambiarEstadoSeccion(idSeccion, ESTADO_INACTIVO);
    }

    // Consultas

    public Seccion obtenerSeccion(Long idSeccion) {
        return seccionRepository.findById(idSeccion)
                .orElseThrow(() -> new RuntimeException("Sección no encontrada con ID: " + idSeccion));
    }

    public List<Seccion> listarTodas() {
        return seccionRepository.findAll();
    }

    public List<Seccion> listarActivas() {
        return seccionRepository.listarActivas();
    }

    public List<Seccion> listarActivasPorPeriodo(Long idPeriodoFk) {
        validarPeriodoExiste(idPeriodoFk);
        return seccionRepository.listarActivasPorPeriodo(idPeriodoFk);
    }

    public List<SeccionRepository.SeccionRow> listarResumen() {
        return seccionRepository.listarResumen();
    }

    public List<SeccionRepository.SeccionRow> listarPorPeriodo(Long idPeriodoFk) {
        validarPeriodoExiste(idPeriodoFk);
        return seccionRepository.listarPorPeriodo(idPeriodoFk);
    }

    public long contarPorEstado(Long idEstadoFk) {
        if (idEstadoFk == null) throw new IllegalArgumentException("El estado es obligatorio.");
        return seccionRepository.countByIdEstadoFk(idEstadoFk);
    }
}

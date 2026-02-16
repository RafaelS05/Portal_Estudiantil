package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.SeccionMateria;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Usuario;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.MateriaRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.SeccionMateriaRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.SeccionRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.UsuarioRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeccionMateriaService {

    private static final Logger log = LoggerFactory.getLogger(SeccionMateriaService.class);

    private static final Long ESTADO_ACTIVO = 1L;
    private static final Long ESTADO_INACTIVO = 2L;

    private final SeccionMateriaRepository seccionMateriaRepository;
    private final SeccionRepository seccionRepository;
    private final MateriaRepository materiaRepository;
    private final UsuarioRepository usuarioRepository;

    public SeccionMateriaService(SeccionMateriaRepository seccionMateriaRepository,
                                 SeccionRepository seccionRepository,
                                 MateriaRepository materiaRepository,
                                 UsuarioRepository usuarioRepository) {
        this.seccionMateriaRepository = seccionMateriaRepository;
        this.seccionRepository = seccionRepository;
        this.materiaRepository = materiaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Validaciones

    private void validarFk(Long id, String nombreCampo) {
        if (id == null) throw new IllegalArgumentException("El " + nombreCampo + " es obligatorio.");
        if (id <= 0) throw new IllegalArgumentException("El " + nombreCampo + " debe ser válido.");
    }

    private void validarExisteSeccion(Long idSeccion) {
        validarFk(idSeccion, "ID de sección");
        if (!seccionRepository.existsById(idSeccion)) {
            throw new RuntimeException("No existe una sección con ID: " + idSeccion);
        }
    }

    private void validarExisteMateria(Long idMateria) {
        validarFk(idMateria, "ID de materia");
        if (!materiaRepository.existsById(idMateria)) {
            throw new RuntimeException("No existe una materia con ID: " + idMateria);
        }
    }

    private void validarExisteDocente(Long idDocente) {
        validarFk(idDocente, "ID de docente");
        if (!usuarioRepository.existsById(idDocente)) {
            throw new RuntimeException("No existe un usuario/docente con ID: " + idDocente);
        }
    }

    private void validarDuplicado(Long idSeccion, Long idMateria, Long idDocente, Long idSeccionMateria) {
        long dup = seccionMateriaRepository.contarDuplicado(idSeccion, idMateria, idDocente, idSeccionMateria);
        if (dup > 0) {
            throw new IllegalArgumentException(
                "Ya existe la asignación Sección/Materia/Docente para esos valores."
            );
        }
    }

    // CRUD

    @Transactional
    public Long crearSeccionMateria(Long idSeccionFk, Long idMateriaFk, Long idUsuarioDocenteFk, Long idEstadoFk) {

        if (idEstadoFk == null) throw new IllegalArgumentException("El estado es obligatorio.");

        validarExisteSeccion(idSeccionFk);
        validarExisteMateria(idMateriaFk);
        validarExisteDocente(idUsuarioDocenteFk);

        validarDuplicado(idSeccionFk, idMateriaFk, idUsuarioDocenteFk, null);

        Long id = seccionMateriaRepository.insertarSeccionMateriaRetornaId(
                idSeccionFk, idMateriaFk, idUsuarioDocenteFk, idEstadoFk
        );

        if (id == null || id <= 0) {
            throw new RuntimeException("SECCIONMATERIA_INSERTAR no retornó un ID válido.");
        }

        log.info("SeccionMateria creada con ID: {}", id);
        return id;
    }

    @Transactional
    public Long crearSeccionMateria(SeccionMateria sm) {
        if (sm == null) throw new IllegalArgumentException("La asignación sección-materia es obligatoria.");
        return crearSeccionMateria(sm.getIdSeccionFk(), sm.getIdMateriaFk(), sm.getIdUsuarioDocenteFk(), sm.getIdEstadoFk());
    }

    @Transactional
    public void actualizarSeccionMateria(Long idSeccionMateria,
                                         Long idSeccionFk,
                                         Long idMateriaFk,
                                         Long idUsuarioDocenteFk) {

        if (idSeccionMateria == null) throw new IllegalArgumentException("El ID de sección-materia es obligatorio.");
        obtenerSeccionMateria(idSeccionMateria);

        validarExisteSeccion(idSeccionFk);
        validarExisteMateria(idMateriaFk);
        validarExisteDocente(idUsuarioDocenteFk);

        validarDuplicado(idSeccionFk, idMateriaFk, idUsuarioDocenteFk, idSeccionMateria);

        seccionMateriaRepository.modificarSeccionMateria(idSeccionMateria, idSeccionFk, idMateriaFk, idUsuarioDocenteFk);

        log.info("SeccionMateria ID {} actualizada.", idSeccionMateria);
    }

    @Transactional
    public void actualizarSeccionMateria(SeccionMateria sm) {
        if (sm == null) throw new IllegalArgumentException("La asignación sección-materia es obligatoria.");
        if (sm.getIdSeccionMateria() == null) {
            throw new IllegalArgumentException("El ID de sección-materia es obligatorio para actualizar.");
        }
        actualizarSeccionMateria(sm.getIdSeccionMateria(), sm.getIdSeccionFk(), sm.getIdMateriaFk(), sm.getIdUsuarioDocenteFk());
    }

    // Estado

    @Transactional
    public void cambiarEstadoSeccionMateria(Long idSeccionMateria, Long idEstado) {
        if (idSeccionMateria == null) throw new IllegalArgumentException("El ID de sección-materia es obligatorio.");
        if (idEstado == null) throw new IllegalArgumentException("El estado es obligatorio.");

        obtenerSeccionMateria(idSeccionMateria);

        seccionMateriaRepository.cambiarEstadoSeccionMateria(idSeccionMateria, idEstado);

        log.info("SeccionMateria ID {} estado cambiado a {}", idSeccionMateria,
                ESTADO_ACTIVO.equals(idEstado) ? "ACTIVA" : "INACTIVA");
    }

    @Transactional
    public void activarSeccionMateria(Long idSeccionMateria) {
        cambiarEstadoSeccionMateria(idSeccionMateria, ESTADO_ACTIVO);
    }

    @Transactional
    public void desactivarSeccionMateria(Long idSeccionMateria) {
        cambiarEstadoSeccionMateria(idSeccionMateria, ESTADO_INACTIVO);
    }

    // Consultas

    public SeccionMateria obtenerSeccionMateria(Long idSeccionMateria) {
        return seccionMateriaRepository.findById(idSeccionMateria)
                .orElseThrow(() -> new RuntimeException("SeccionMateria no encontrada con ID: " + idSeccionMateria));
    }

    public List<SeccionMateria> listarTodas() {
        return seccionMateriaRepository.findAll();
    }

    public List<SeccionMateria> listarActivas() {
        return seccionMateriaRepository.listarActivas();
    }

    public List<SeccionMateria> listarPorSeccion(Long idSeccion) {
        validarExisteSeccion(idSeccion);
        return seccionMateriaRepository.listarPorSeccion(idSeccion);
    }

    public List<SeccionMateria> listarPorDocente(Long idDocente) {
        validarExisteDocente(idDocente);
        return seccionMateriaRepository.listarPorDocente(idDocente);
    }

    public long contarPorEstado(Long idEstadoFk) {
        if (idEstadoFk == null) throw new IllegalArgumentException("El estado es obligatorio.");
        return seccionMateriaRepository.countByIdEstadoFk(idEstadoFk);
    }
}
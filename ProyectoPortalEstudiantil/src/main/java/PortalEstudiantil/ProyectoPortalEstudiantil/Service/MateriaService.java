package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Materia;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.MateriaRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MateriaService {

    private static final Logger log = LoggerFactory.getLogger(MateriaService.class);

    private static final Long ESTADO_ACTIVO = 1L;
    private static final Long ESTADO_INACTIVO = 2L;

    private final MateriaRepository materiaRepository;

    public MateriaService(MateriaRepository materiaRepository) {
        this.materiaRepository = materiaRepository;
    }

    // Validaciones
    private String normalizar(String s) {
        return (s == null) ? null : s.trim();
    }

    private void validarDatosBasicos(String nombre, String codigo) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la materia es obligatorio.");
        }
        if (nombre.trim().length() > 80) {
            throw new IllegalArgumentException("El nombre no puede exceder 80 caracteres.");
        }

        // Código es opcional, pero si viene, validamos formato según tu Domain
        if (codigo != null && !codigo.trim().isEmpty()) {
            String cod = codigo.trim().toUpperCase();
            if (!cod.matches("^[A-Z]{3}-\\d{3}$")) {
                throw new IllegalArgumentException("El código debe tener formato AAA-999 (ej: MAT-101).");
            }
        }
    }

    private void validarNombreUnico(String nombre, Long idMateria) {
        long dup = materiaRepository.contarNombreDuplicado(nombre.trim(), idMateria);
        if (dup > 0) {
            throw new IllegalArgumentException("Ya existe una materia con el nombre: " + nombre);
        }
    }

    private void validarCodigoUnico(String codigo, Long idMateria) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return;
        }

        long dup = materiaRepository.contarCodigoDuplicado(codigo.trim(), idMateria);
        if (dup > 0) {
            throw new IllegalArgumentException("Ya existe una materia con el código: " + codigo);
        }
    }

    // CRUD
    @Transactional
    public Long crearMateria(String nombre, String codigoMateria, Long idEstadoFk) {

        nombre = normalizar(nombre);
        codigoMateria = normalizar(codigoMateria);
        if (codigoMateria != null && !codigoMateria.isEmpty()) {
            codigoMateria = codigoMateria.toUpperCase();
        }

        if (idEstadoFk == null) {
            throw new IllegalArgumentException("El estado es obligatorio.");
        }

        validarDatosBasicos(nombre, codigoMateria);
        validarNombreUnico(nombre, null);
        validarCodigoUnico(codigoMateria, null);

        Long id = materiaRepository.insertarMateriaRetornaId(
                nombre,
                (codigoMateria == null || codigoMateria.isEmpty()) ? null : codigoMateria,
                idEstadoFk
        );

        if (id == null || id <= 0) {
            throw new RuntimeException("El stored procedure MATERIA_INSERTAR no retornó un ID válido.");
        }

        log.info("Materia creada con ID: {}", id);
        return id;
    }

    @Transactional
    public Long crearMateria(Materia materia) {
        if (materia == null) {
            throw new IllegalArgumentException("La materia es obligatoria.");
        }
        return crearMateria(materia.getNombre(), materia.getCodigoMateria(), materia.getIdEstadoFk());
    }

    @Transactional
    public void actualizarMateria(Long idMateria, String nombre, String codigoMateria) {

        if (idMateria == null) {
            throw new IllegalArgumentException("El ID de la materia es obligatorio.");
        }
        Materia actual = obtenerMateria(idMateria);

        nombre = normalizar(nombre);
        codigoMateria = normalizar(codigoMateria);
        if (codigoMateria != null && !codigoMateria.isEmpty()) {
            codigoMateria = codigoMateria.toUpperCase();
        }

        validarDatosBasicos(nombre, codigoMateria);
        validarNombreUnico(nombre, idMateria);
        validarCodigoUnico(codigoMateria, idMateria);

        materiaRepository.modificarMateria(
                idMateria,
                nombre,
                (codigoMateria == null || codigoMateria.isEmpty()) ? null : codigoMateria
        );

        log.info("Materia ID {} actualizada.", idMateria);
    }

    @Transactional
    public void actualizarMateria(Materia materia) {
        if (materia == null) {
            throw new IllegalArgumentException("La materia es obligatoria.");
        }
        if (materia.getIdMateria() == null) {
            throw new IllegalArgumentException("El ID de la materia es obligatorio para actualizar.");
        }
        actualizarMateria(materia.getIdMateria(), materia.getNombre(), materia.getCodigoMateria());
    }

    // ====================
    // Estado
    // ====================
    @Transactional
    public void activarMateria(Long idMateria) {
        cambiarEstadoMateria(idMateria, ESTADO_ACTIVO);
    }

    @Transactional
    public void desactivarMateria(Long idMateria) {
        cambiarEstadoMateria(idMateria, ESTADO_INACTIVO);
    }

    @Transactional
    public void cambiarEstadoMateria(Long idMateria, Long idEstado) {
        if (idMateria == null) {
            throw new IllegalArgumentException("El ID de la materia es obligatorio.");
        }
        if (idEstado == null) {
            throw new IllegalArgumentException("El estado es obligatorio.");
        }

        // valida existencia
        obtenerMateria(idMateria);

        materiaRepository.cambiarEstadoMateria(idMateria, idEstado);

        log.info("Materia ID {} estado cambiado a {}", idMateria,
                ESTADO_ACTIVO.equals(idEstado) ? "ACTIVA" : "INACTIVA");
    }

    // ====================
    // Consultas
    // ====================
    public Materia obtenerMateria(Long idMateria) {
        return materiaRepository.findById(idMateria)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada con ID: " + idMateria));
    }

    public List<Materia> listarTodas() {
        return materiaRepository.findAll();
    }

    public List<Materia> listarActivas() {
        return materiaRepository.listarActivas();
    }

    public List<Materia> buscar(String busqueda) {
        if (busqueda == null || busqueda.trim().isEmpty()) {
            throw new IllegalArgumentException("El término de búsqueda es obligatorio.");
        }
        return materiaRepository.buscarPorNombreOCodigo(busqueda.trim());
    }

    public long contarPorEstado(Long idEstadoFk) {
        if (idEstadoFk == null) {
            throw new IllegalArgumentException("El estado es obligatorio.");
        }
        return materiaRepository.countByIdEstadoFk(idEstadoFk);
    }

}

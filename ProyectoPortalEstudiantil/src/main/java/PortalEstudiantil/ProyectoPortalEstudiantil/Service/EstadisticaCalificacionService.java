package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.EstadisticaCalificacion;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.EstadisticaCalificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class EstadisticaCalificacionService {

    @Autowired
    private EstadisticaCalificacionRepository estadisticaRepository;

    @Transactional
    public Long crearEstadistica(Long idMateriaFk, Long idSeccionFk, Long idDocenteFk,
                                Double promedio, Double notaMaxima, Double notaMinima,
                                Long totalEstudiantes, Long aprobados, Long reprobados,
                                Double porcentajeAprobacion, Long idEstadoFk) {
        try {
            // Validaciones
            if (idMateriaFk == null) {
                throw new IllegalArgumentException("El ID de la materia es obligatorio");
            }
            if (idSeccionFk == null) {
                throw new IllegalArgumentException("El ID de la sección es obligatorio");
            }
            if (idDocenteFk == null) {
                throw new IllegalArgumentException("El ID del docente es obligatorio");
            }
            if (promedio == null || promedio < 0 || promedio > 100) {
                throw new IllegalArgumentException("El promedio debe estar entre 0 y 100");
            }
            if (notaMaxima == null || notaMaxima < 0 || notaMaxima > 100) {
                throw new IllegalArgumentException("La nota máxima debe estar entre 0 y 100");
            }
            if (notaMinima == null || notaMinima < 0 || notaMinima > 100) {
                throw new IllegalArgumentException("La nota mínima debe estar entre 0 y 100");
            }
            if (totalEstudiantes == null || totalEstudiantes < 0) {
                throw new IllegalArgumentException("El total de estudiantes debe ser mayor o igual a 0");
            }
            if (aprobados == null || aprobados < 0) {
                throw new IllegalArgumentException("Los aprobados deben ser mayor o igual a 0");
            }
            if (reprobados == null || reprobados < 0) {
                throw new IllegalArgumentException("Los reprobados deben ser mayor o igual a 0");
            }
            if (porcentajeAprobacion == null || porcentajeAprobacion < 0 || porcentajeAprobacion > 100) {
                throw new IllegalArgumentException("El porcentaje de aprobación debe estar entre 0 y 100");
            }
            if (idEstadoFk == null) {
                throw new IllegalArgumentException("El estado es obligatorio");
            }

            estadisticaRepository.insertarEstadistica(idMateriaFk, idSeccionFk, idDocenteFk,
                    promedio, notaMaxima, notaMinima, totalEstudiantes, aprobados, reprobados,
                    porcentajeAprobacion, idEstadoFk);

            // Obtener la estadística recién creada
            List<EstadisticaCalificacion> estadisticasRecientes = 
                estadisticaRepository.findByMateriaAndSeccion(idMateriaFk, idSeccionFk);
            EstadisticaCalificacion estadisticaCreada = estadisticasRecientes.stream()
                .filter(e -> e.getIdDocenteFk().equals(idDocenteFk))
                .findFirst()
                .orElse(null);

            if (estadisticaCreada != null) {
                System.out.println("Estadística creada exitosamente con ID: " + estadisticaCreada.getIdEstadistica());
                return estadisticaCreada.getIdEstadistica();
            }

            throw new RuntimeException("No se pudo obtener el ID de la estadística creada");

        } catch (Exception e) {
            System.err.println("Error al crear estadística: " + e.getMessage());
            throw new RuntimeException("Error en la creación de la estadística: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Long crearEstadistica(EstadisticaCalificacion estadistica) {
        return crearEstadistica(
            estadistica.getIdMateriaFk(),
            estadistica.getIdSeccionFk(),
            estadistica.getIdDocenteFk(),
            estadistica.getPromedio(),
            estadistica.getNotaMaxima(),
            estadistica.getNotaMinima(),
            estadistica.getTotalEstudiantes(),
            estadistica.getAprobados(),
            estadistica.getReprobados(),
            estadistica.getPorcentajeAprobacion(),
            estadistica.getIdEstadoFk()
        );
    }

    @Transactional
    public void actualizarEstadistica(Long idEstadistica, Long idMateriaFk, Long idSeccionFk,
                                     Long idDocenteFk, Double promedio, Double notaMaxima,
                                     Double notaMinima, Long totalEstudiantes, Long aprobados,
                                     Long reprobados, Double porcentajeAprobacion) {
        try {
            // Validar que exista la estadística
            if (!estadisticaRepository.existsById(idEstadistica)) {
                throw new RuntimeException("No existe una estadística con ID: " + idEstadistica);
            }

            if (idMateriaFk == null) {
                throw new IllegalArgumentException("El ID de la materia es obligatorio");
            }
            if (idSeccionFk == null) {
                throw new IllegalArgumentException("El ID de la sección es obligatorio");
            }
            if (idDocenteFk == null) {
                throw new IllegalArgumentException("El ID del docente es obligatorio");
            }
            if (promedio == null || promedio < 0 || promedio > 100) {
                throw new IllegalArgumentException("El promedio debe estar entre 0 y 100");
            }

            estadisticaRepository.modificarEstadistica(idEstadistica, idMateriaFk, idSeccionFk,
                    idDocenteFk, promedio, notaMaxima, notaMinima, totalEstudiantes,
                    aprobados, reprobados, porcentajeAprobacion);

            System.out.println("Estadística ID " + idEstadistica + " actualizada exitosamente");

        } catch (Exception e) {
            System.err.println("Error al actualizar estadística: " + e.getMessage());
            throw new RuntimeException("Error en la actualización de la estadística: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void actualizarEstadistica(EstadisticaCalificacion estadistica) {
        if (estadistica.getIdEstadistica() == null) {
            throw new IllegalArgumentException("El ID de la estadística es obligatorio para actualizar");
        }

        actualizarEstadistica(
            estadistica.getIdEstadistica(),
            estadistica.getIdMateriaFk(),
            estadistica.getIdSeccionFk(),
            estadistica.getIdDocenteFk(),
            estadistica.getPromedio(),
            estadistica.getNotaMaxima(),
            estadistica.getNotaMinima(),
            estadistica.getTotalEstudiantes(),
            estadistica.getAprobados(),
            estadistica.getReprobados(),
            estadistica.getPorcentajeAprobacion()
        );
    }

    @Transactional
    public void cambiarEstadoEstadistica(Long idEstadistica, Long idEstado) {
        try {
            // Validar que exista la estadística
            if (!estadisticaRepository.existsById(idEstadistica)) {
                throw new RuntimeException("No existe una estadística con ID: " + idEstadistica);
            }

            if (idEstado == null) {
                throw new IllegalArgumentException("El estado es obligatorio");
            }

            estadisticaRepository.cambiarEstadoEstadistica(idEstadistica, idEstado);

            String estadoTexto = idEstado == 1L ? "ACTIVADA" : "DESACTIVADA";
            System.out.println("✅ Estadística ID " + idEstadistica + " " + estadoTexto);

        } catch (Exception e) {
            System.err.println("❌ Error al cambiar estado de la estadística: " + e.getMessage());
            throw new RuntimeException("Error al cambiar estado: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void activarEstadistica(Long idEstadistica) {
        cambiarEstadoEstadistica(idEstadistica, 1L);
    }

    @Transactional
    public void desactivarEstadistica(Long idEstadistica) {
        cambiarEstadoEstadistica(idEstadistica, 2L);
    }

    public EstadisticaCalificacion obtenerEstadistica(Long idEstadistica) {
        Optional<EstadisticaCalificacion> estadistica = estadisticaRepository.findById(idEstadistica);
        return estadistica.orElseThrow(() ->
            new RuntimeException("Estadística no encontrada con ID: " + idEstadistica));
    }

    public List<EstadisticaCalificacion> obtenerTodasEstadisticas() {
        return estadisticaRepository.findAll();
    }

    public List<EstadisticaCalificacion> buscarPorMateria(Long idMateriaFk) {
        if (idMateriaFk == null) {
            throw new IllegalArgumentException("El ID de la materia es obligatorio");
        }
        return estadisticaRepository.findByIdMateriaFk(idMateriaFk);
    }

    public List<EstadisticaCalificacion> buscarPorSeccion(Long idSeccionFk) {
        if (idSeccionFk == null) {
            throw new IllegalArgumentException("El ID de la sección es obligatorio");
        }
        return estadisticaRepository.findByIdSeccionFk(idSeccionFk);
    }

    public List<EstadisticaCalificacion> buscarPorDocente(Long idDocenteFk) {
        if (idDocenteFk == null) {
            throw new IllegalArgumentException("El ID del docente es obligatorio");
        }
        return estadisticaRepository.findByIdDocenteFk(idDocenteFk);
    }

    public List<EstadisticaCalificacion> buscarPorEstado(Long idEstado) {
        if (idEstado == null) {
            throw new IllegalArgumentException("El ID del estado es obligatorio");
        }
        return estadisticaRepository.findByIdEstadoFk(idEstado);
    }

    public List<EstadisticaCalificacion> obtenerEstadisticasActivas() {
        return buscarPorEstado(1L);
    }

    public List<EstadisticaCalificacion> obtenerEstadisticasInactivas() {
        return buscarPorEstado(2L);
    }

    public List<EstadisticaCalificacion> buscarPorMateriaYSeccion(Long idMateria, Long idSeccion) {
        if (idMateria == null || idSeccion == null) {
            throw new IllegalArgumentException("Los IDs de materia y sección son obligatorios");
        }
        return estadisticaRepository.findByMateriaAndSeccion(idMateria, idSeccion);
    }

    public List<EstadisticaCalificacion> buscarPorMateriaYDocente(Long idMateria, Long idDocente) {
        if (idMateria == null || idDocente == null) {
            throw new IllegalArgumentException("Los IDs de materia y docente son obligatorios");
        }
        return estadisticaRepository.findByMateriaAndDocente(idMateria, idDocente);
    }

    public List<EstadisticaCalificacion> buscarPorSeccionYDocente(Long idSeccion, Long idDocente) {
        if (idSeccion == null || idDocente == null) {
            throw new IllegalArgumentException("Los IDs de sección y docente son obligatorios");
        }
        return estadisticaRepository.findBySeccionAndDocente(idSeccion, idDocente);
    }

    public EstadisticaCalificacion buscarPorMateriaSeccionDocente(Long idMateria, Long idSeccion, Long idDocente) {
        if (idMateria == null || idSeccion == null || idDocente == null) {
            throw new IllegalArgumentException("Los IDs de materia, sección y docente son obligatorios");
        }
        return estadisticaRepository.findByMateriaSeccionDocente(idMateria, idSeccion, idDocente);
    }

    public List<EstadisticaCalificacion> buscarPorPromedioMinimo(Double promedioMinimo) {
        if (promedioMinimo == null) {
            throw new IllegalArgumentException("El promedio mínimo es obligatorio");
        }
        return estadisticaRepository.findByPromedioGreaterThanEqual(promedioMinimo);
    }

    public List<EstadisticaCalificacion> buscarPorPorcentajeAprobacionMinimo(Double porcentajeMinimo) {
        if (porcentajeMinimo == null) {
            throw new IllegalArgumentException("El porcentaje mínimo es obligatorio");
        }
        return estadisticaRepository.findByPorcentajeAprobacionGreaterThanEqual(porcentajeMinimo);
    }

    public boolean existeEstadistica(Long idEstadistica) {
        if (idEstadistica == null) {
            return false;
        }
        return estadisticaRepository.existsById(idEstadistica);
    }

    public long contarTotalEstadisticas() {
        return estadisticaRepository.count();
    }

    public long contarEstadisticasPorMateria(Long idMateriaFk) {
        return estadisticaRepository.findByIdMateriaFk(idMateriaFk).size();
    }

    public long contarEstadisticasPorSeccion(Long idSeccionFk) {
        return estadisticaRepository.findByIdSeccionFk(idSeccionFk).size();
    }

    public long contarEstadisticasPorDocente(Long idDocenteFk) {
        return estadisticaRepository.findByIdDocenteFk(idDocenteFk).size();
    }

    public long contarEstadisticasPorEstado(Long idEstado) {
        return estadisticaRepository.findByIdEstadoFk(idEstado).size();
    }

    public String obtenerInformacionEstadistica(Long idEstadistica) {
        EstadisticaCalificacion est = obtenerEstadistica(idEstadistica);
        return String.format(
            "Estadística ID: %d - Materia: %d, Sección: %d, Docente: %d | " +
            "Promedio: %.2f, Max: %.2f, Min: %.2f | " +
            "Estudiantes: %d, Aprobados: %d, Reprobados: %d, Aprobación: %.2f%%",
            est.getIdEstadistica(),
            est.getIdMateriaFk(),
            est.getIdSeccionFk(),
            est.getIdDocenteFk(),
            est.getPromedio(),
            est.getNotaMaxima(),
            est.getNotaMinima(),
            est.getTotalEstudiantes(),
            est.getAprobados(),
            est.getReprobados(),
            est.getPorcentajeAprobacion()
        );
    }

    @Transactional
    public void eliminarEstadistica(Long idEstadistica) {
        try {
            if (!estadisticaRepository.existsById(idEstadistica)) {
                throw new RuntimeException("No existe una estadística con ID: " + idEstadistica);
            }

            estadisticaRepository.deleteById(idEstadistica);
            System.out.println("✅ Estadística ID " + idEstadistica + " eliminada exitosamente");

        } catch (Exception e) {
            System.err.println("❌ Error al eliminar estadística: " + e.getMessage());
            throw new RuntimeException("No se pudo eliminar la estadística: " + e.getMessage(), e);
        }
    }

    public boolean validarDatosEstadistica(EstadisticaCalificacion estadistica) {
        if (estadistica == null) return false;
        if (estadistica.getIdMateriaFk() == null) return false;
        if (estadistica.getIdSeccionFk() == null) return false;
        if (estadistica.getIdDocenteFk() == null) return false;
        if (estadistica.getPromedio() == null || estadistica.getPromedio() < 0 || estadistica.getPromedio() > 100) return false;
        if (estadistica.getNotaMaxima() == null || estadistica.getNotaMaxima() < 0 || estadistica.getNotaMaxima() > 100) return false;
        if (estadistica.getNotaMinima() == null || estadistica.getNotaMinima() < 0 || estadistica.getNotaMinima() > 100) return false;
        if (estadistica.getTotalEstudiantes() == null || estadistica.getTotalEstudiantes() < 0) return false;
        if (estadistica.getAprobados() == null || estadistica.getAprobados() < 0) return false;
        if (estadistica.getReprobados() == null || estadistica.getReprobados() < 0) return false;
        if (estadistica.getPorcentajeAprobacion() == null || estadistica.getPorcentajeAprobacion() < 0 || estadistica.getPorcentajeAprobacion() > 100) return false;
        if (estadistica.getIdEstadoFk() == null) return false;
        return true;
    }

    public Double calcularPromedioGeneral(Long idMateriaFk) {
        List<EstadisticaCalificacion> estadisticas = buscarPorMateria(idMateriaFk);
        if (estadisticas.isEmpty()) return 0.0;
        return estadisticas.stream()
            .mapToDouble(EstadisticaCalificacion::getPromedio)
            .average()
            .orElse(0.0);
    }

    public Double calcularPorcentajeAprobacionGeneral(Long idMateriaFk) {
        List<EstadisticaCalificacion> estadisticas = buscarPorMateria(idMateriaFk);
        if (estadisticas.isEmpty()) return 0.0;
        return estadisticas.stream()
            .mapToDouble(EstadisticaCalificacion::getPorcentajeAprobacion)
            .average()
            .orElse(0.0);
    }
}
package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Periodo;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.PeriodoRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PeriodoService {


    @Autowired
    private PeriodoRepository periodoRepository;

    // Listar
    public List<PeriodoRepository.PeriodoRow> listarResumen() {
        return periodoRepository.listarResumen();
    }

    public Periodo obtenerPeriodoActual() {
        return periodoRepository.obtenerPeriodoActualEnCurso();
    }

    public long contarActivos() {
        return periodoRepository.countByIdEstadoFk(1L);
    }

    public Periodo buscarPorId(Long id) {
        return periodoRepository.findByIdPeriodo(id);
    }

    // Insertar
    public Long insertar(String nombre, LocalDate fechaInicio, LocalDate fechaFin) {

        if (periodoRepository.contarNombreDuplicado(nombre, null) > 0) {
            throw new IllegalArgumentException(
                    "Ya existe un período con el nombre \"" + nombre + "\".");
        }

        if (periodoRepository.contarTraslapesActivos(fechaInicio, fechaFin, null) > 0) {
            throw new IllegalArgumentException(
                    "Las fechas se trasladan con otro período activo existente.");
        }

        periodoRepository.insertarPeriodo(nombre, fechaInicio, fechaFin, 1L);

        return periodoRepository.obtenerUltimoIdInsertado();
    }

    // Modificar
    public void modificar(Long idPeriodo, String nombre,
                          LocalDate fechaInicio, LocalDate fechaFin) {

        if (periodoRepository.contarNombreDuplicado(nombre, idPeriodo) > 0) {
            throw new IllegalArgumentException(
                    "Ya existe otro período con el nombre \"" + nombre + "\".");
        }

        if (periodoRepository.contarTraslapesActivos(fechaInicio, fechaFin, idPeriodo) > 0) {
            throw new IllegalArgumentException(
                    "Las fechas se trasladan con otro período activo existente.");
        }

        periodoRepository.modificarPeriodo(idPeriodo, nombre, fechaInicio, fechaFin);
    }

    // Cambiar estado
    public void cambiarEstado(Long idPeriodo, Long idEstado) {
        periodoRepository.cambiarEstadoPeriodo(idPeriodo, idEstado);
    }
}

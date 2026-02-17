/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Periodo;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.PeriodoRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PeriodoService {

    @Autowired
    private PeriodoRepository periodoRepository;

    public List<PeriodoRepository.PeriodoRow> listarResumen() {
        return periodoRepository.listarResumen();
    }

    public List<Periodo> listarActivos() {
        return periodoRepository.listarActivos();
    }

    public List<Periodo> listarTodos() {
        return periodoRepository.findAll();
    }

    public Periodo buscarPorId(Long id) {
        return periodoRepository.findByIdPeriodo(id);
    }

    public Periodo obtenerPeriodoActual() {
        return periodoRepository.obtenerPeriodoActualEnCurso();
    }

    @Transactional
    public Long insertar(String nombre, LocalDate fechaInicio, LocalDate fechaFin) {
        validarCampos(nombre, fechaInicio, fechaFin, null);
        return periodoRepository.insertarPeriodoRetornaId(nombre.trim(), fechaInicio, fechaFin, 1L);
    }

    @Transactional
    public void modificar(Long idPeriodo, String nombre, LocalDate fechaInicio, LocalDate fechaFin) {
        Periodo existente = periodoRepository.findByIdPeriodo(idPeriodo);
        if (existente == null) {
            throw new IllegalArgumentException("Período no encontrado: " + idPeriodo);
        }
        validarCampos(nombre, fechaInicio, fechaFin, idPeriodo);
        periodoRepository.modificarPeriodo(idPeriodo, nombre.trim(), fechaInicio, fechaFin);
    }

    @Transactional
    public void cambiarEstado(Long idPeriodo, Long idEstado) {
        periodoRepository.cambiarEstadoPeriodo(idPeriodo, idEstado);
    }

    public long contarActivos() {
        return periodoRepository.countByIdEstadoFk(1L);
    }

    private void validarCampos(String nombre, LocalDate fechaInicio, LocalDate fechaFin,
            Long idPeriodoExcluir) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del período es obligatorio.");
        }
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas son obligatorias.");
        }
        if (!fechaFin.isAfter(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la de inicio.");
        }
        if (periodoRepository.contarNombreDuplicado(nombre.trim(), idPeriodoExcluir) > 0) {
            throw new IllegalArgumentException("Ya existe un período con el nombre: " + nombre);
        }
        if (periodoRepository.contarTraslapesActivos(fechaInicio, fechaFin, idPeriodoExcluir) > 0) {
            throw new IllegalArgumentException(
                    "El rango de fechas se traslapa con otro período activo existente.");
        }
    }
}

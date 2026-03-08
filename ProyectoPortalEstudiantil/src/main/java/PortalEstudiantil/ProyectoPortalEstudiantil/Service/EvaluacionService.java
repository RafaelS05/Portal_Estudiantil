/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Evaluacion;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.EvaluacionRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 *
 * @author marjo
 */
@Service
public class EvaluacionService {

    private static final Long ESTADO_ACTIVO = 1L;
    private static final Long ESTADO_INACTIVO = 2L;

    private final EvaluacionRepository evaluacionRepository;

    public EvaluacionService(EvaluacionRepository evaluacionRepository) {
        this.evaluacionRepository = evaluacionRepository;
    }

    public List<Evaluacion> listarTodas() {
        return evaluacionRepository.findAll();
    }

    @Transactional
    public Evaluacion crearEvaluacion(Evaluacion evaluacion) {
        if (evaluacion.getPorcentaje().compareTo(BigDecimal.ZERO) < 0
                || evaluacion.getPorcentaje().compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100");
        }

        if (evaluacion.getIdEstadoFk() == null) {
            evaluacion.setIdEstadoFk(ESTADO_ACTIVO);
        }

        return evaluacionRepository.save(evaluacion);
    }

    @Transactional
    public Evaluacion actualizarEvaluacion(Long id, Evaluacion nuevaEvaluacion) {
        Evaluacion evaluacion = evaluacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró la evaluación con ID: " + id));

        evaluacion.setTipo(nuevaEvaluacion.getTipo());
        evaluacion.setPorcentaje(nuevaEvaluacion.getPorcentaje());
        evaluacion.setSeccionMateria(nuevaEvaluacion.getSeccionMateria());
        evaluacion.setPeriodo(nuevaEvaluacion.getPeriodo());

        return evaluacionRepository.save(evaluacion);
    }

    public Evaluacion obtenerPorId(Long id) {
        return evaluacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró la evaluación con ID: " + id));
    }

    @Transactional
    public void cambiarEstado(Long id, Long nuevoEstado) {
        Evaluacion evaluacion = obtenerPorId(id);
        evaluacion.setIdEstadoFk(nuevoEstado);
        evaluacionRepository.save(evaluacion);
    }

    public Page<Evaluacion> listarEvaluacionesPaginado(String busqueda, int pagina, int tamanoPagina) {
        Pageable pageable = PageRequest.of(pagina - 1, tamanoPagina, Sort.by("idEvaluacion").descending());

        if (busqueda != null && !busqueda.trim().isEmpty()) {
            return evaluacionRepository.findByTipoContainingIgnoreCase(busqueda, pageable);
        } else {
            return evaluacionRepository.findAll(pageable);
        }
    }
}

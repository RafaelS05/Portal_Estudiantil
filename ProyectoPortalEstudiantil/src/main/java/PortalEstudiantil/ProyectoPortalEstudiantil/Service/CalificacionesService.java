/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.*;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

/**
 *
 * @author marjo
 */
@Service
public class CalificacionesService {

    private static final Long ESTADO_ACTIVO = 1L;
    private static final Long ESTADO_INACTIVO = 2L;

    private final CalificacionesRepository calificacionesRepository;
    private final MatriculaRepository matriculaRepository;
    private final EvaluacionRepository evaluacionRepository;

    public CalificacionesService(CalificacionesRepository calificacionesRepository, MatriculaRepository matriculaRepository, EvaluacionRepository evaluacionRepository) {
        this.calificacionesRepository = calificacionesRepository;
        this.matriculaRepository = matriculaRepository;
        this.evaluacionRepository = evaluacionRepository;
    }

    public List<Calificaciones> listarTodas() {
        return calificacionesRepository.findAll();
    }

    @Transactional
    public void guardarCalificaciones(Calificaciones calificacion) {
        if (calificacion.getIdEstadoFk() == null) {
            calificacion.setIdEstadoFk(ESTADO_ACTIVO);
        }
        calificacionesRepository.save(calificacion);
    }

    public Calificaciones obtenerPorId(Long id) {
        return calificacionesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró la calificación con ID: " + id));
    }

    @Transactional
    public void eliminar(Long id) {
        Calificaciones calificacion = obtenerPorId(id);
        calificacion.setIdEstadoFk(ESTADO_INACTIVO); 
        calificacionesRepository.save(calificacion);
    }

    public List<Map<String, Object>> listarCalificacionesParaVista() {
        List<Calificaciones> lista = calificacionesRepository.findAll();
        List<Map<String, Object>> out = new ArrayList<>();

        for (Calificaciones c : lista) {
            if (ESTADO_ACTIVO.equals(c.getIdEstadoFk())) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", c.getIdCalificaciones());
                row.put("nota", c.getCalificacion());
                row.put("estudiante", c.getMatricula().getEstudiante().getNombre() + " "
                        + c.getMatricula().getEstudiante().getPrimerApellido());
                row.put("evaluacion", c.getEvaluacion().getTipo());
                out.add(row);
            }
        }
        return out;
    }
}

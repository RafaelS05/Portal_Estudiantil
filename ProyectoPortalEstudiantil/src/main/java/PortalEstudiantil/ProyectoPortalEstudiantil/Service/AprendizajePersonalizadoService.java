/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Calificaciones;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.RecursoAprendizaje;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Usuario;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Seccion;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.CalificacionesRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.RecursoAprendizajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author marjo
 */

@Service
public class AprendizajePersonalizadoService {
    
    @Autowired
    private CalificacionesRepository calificacionesRepository;
    
    @Autowired
    private RecursoAprendizajeRepository recursoRepository;
    
    private static final Long ESTADO_ACTIVO = 1L;
    private static final Integer ESTADO_RECURSO_ACTIVO = 1;
    
    public Page<Map<String, Object>> obtenerEstudiantesConEstrategia(String busqueda, Pageable pageable) {
        
        Page<Calificaciones> calificacionesPage;
        
        if (busqueda == null || busqueda.trim().isEmpty()) {
            calificacionesPage = calificacionesRepository.findAllActivos(ESTADO_ACTIVO, pageable);
        } else {
            calificacionesPage = calificacionesRepository.searchByEstudianteNombre(busqueda, ESTADO_ACTIVO, pageable);
        }
        
        return calificacionesPage.map(this::transformarConEstrategia);
    }
    
    private Map<String, Object> transformarConEstrategia(Calificaciones calificacion) {
        Map<String, Object> resultado = new HashMap<>();
        
        Usuario estudiante = calificacion.getMatricula().getEstudiante();
        resultado.put("idEstudiante", estudiante.getIdUsuario());
        
        String nombreCompleto = estudiante.getNombre() + " " + 
                                estudiante.getPrimerApellido();
        if (estudiante.getSegundoApellido() != null && !estudiante.getSegundoApellido().trim().isEmpty()) {
            nombreCompleto += " " + estudiante.getSegundoApellido();
        }
        resultado.put("nombreCompleto", nombreCompleto);
        
        Seccion seccion = calificacion.getMatricula().getSeccion();
        resultado.put("seccion", seccion.getNumero());
        resultado.put("grado", seccion.getGrado());
        resultado.put("letraSeccion", seccion.getLetra());
        
        String infoMateria = calificacion.getEvaluacion()
                                        .getSeccionMateria()
                                        .getNombreCompleto();
        resultado.put("materia", infoMateria);
        
        resultado.put("calificacion", calificacion.getCalificacion());

        String estrategia = calcularEstrategia(calificacion.getCalificacion());
        resultado.put("estrategia", estrategia);
        resultado.put("colorEstrategia", getColorEstrategia(estrategia));
        
        List<RecursoAprendizaje> recursos = recursoRepository.findActivos();
        resultado.put("recursos", recursos);
        resultado.put("tieneRecursos", !recursos.isEmpty());
        
        return resultado;
    }
    
    private String calcularEstrategia(BigDecimal calificacion) {
        double nota = calificacion.doubleValue();
        
        if (nota < 60) {
            return "Refuerzo guiado";
        } else if (nota < 85) {
            return "Práctica estructurada";
        } else {
            return "Mapas conceptuales";
        }
    }
    
    private String getColorEstrategia(String estrategia) {
        switch (estrategia) {
            case "Refuerzo guiado": return "danger";
            case "Práctica estructurada": return "warning";
            case "Mapas conceptuales": return "success";
            default: return "secondary";
        }
    }
}
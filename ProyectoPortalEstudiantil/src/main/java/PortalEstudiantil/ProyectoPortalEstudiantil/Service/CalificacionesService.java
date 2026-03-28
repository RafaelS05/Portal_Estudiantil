/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.*;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
    private final MateriaRepository materiaRepository;
    private final SeccionRepository seccionRepository;

    public CalificacionesService(CalificacionesRepository calificacionesRepository, MatriculaRepository matriculaRepository, EvaluacionRepository evaluacionRepository, MateriaRepository materiaRepository, SeccionRepository seccionRepository) {
        this.calificacionesRepository = calificacionesRepository;
        this.matriculaRepository = matriculaRepository;
        this.evaluacionRepository = evaluacionRepository;
        this.materiaRepository = materiaRepository;
        this.seccionRepository = seccionRepository;
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

        Map<Long, String> cacheMaterias = new HashMap<>();

        for (Calificaciones c : lista) {
            if (ESTADO_ACTIVO.equals(c.getIdEstadoFk())) {
                Map<String, Object> row = new HashMap<>();

                row.put("id", c.getIdCalificaciones());
                row.put("nota", c.getCalificacion());

                String nombreEstudiante = c.getMatricula().getEstudiante().getNombre() + " "
                        + c.getMatricula().getEstudiante().getPrimerApellido();
                row.put("estudiante", nombreEstudiante);

                if (c.getMatricula() != null && c.getMatricula().getSeccion() != null) {
                    row.put("seccion", c.getMatricula().getSeccion().getNombreCompleto());
                } else {
                    row.put("seccion", "Sin sección");
                }

                if (c.getMatricula() != null && c.getMatricula().getEstudiante() != null
                        && c.getMatricula().getEstudiante().getCorreos() != null
                        && !c.getMatricula().getEstudiante().getCorreos().isEmpty()) {

                    row.put("correo", c.getMatricula().getEstudiante().getCorreos().get(0).getCorreo());
                } else {
                    row.put("correo", "Sin correo");
                }

                String nombreMateria = "Sin materia";
                if (c.getEvaluacion() != null && c.getEvaluacion().getSeccionMateria() != null) {
                    Long idMateria = c.getEvaluacion().getSeccionMateria().getIdMateriaFk();
                    if (idMateria != null) {
                        if (cacheMaterias.containsKey(idMateria)) {
                            nombreMateria = cacheMaterias.get(idMateria);
                        } else {
                            Optional<Materia> materiaOpt = materiaRepository.findById(idMateria);
                            if (materiaOpt.isPresent()) {
                                nombreMateria = materiaOpt.get().getNombreCompleto();
                                cacheMaterias.put(idMateria, nombreMateria);
                            }
                        }
                    }
                }
                row.put("materia", nombreMateria);
                row.put("evaluacion", c.getEvaluacion().getTipo());

                out.add(row);
            }
        }
        return out;
    }

    public Map<String, Object> listarCalificacionesParaVistaPaginado(
            String busqueda,
            Long idSeccion,
            int pagina,
            int tamanoPagina) {

        Pageable pageable = PageRequest.of(pagina - 1, tamanoPagina, Sort.by("idCalificaciones").descending());
        Page<Calificaciones> page;

        if (idSeccion != null && idSeccion > 0) {
            page = calificacionesRepository.findBySeccionAndActivos(idSeccion, ESTADO_ACTIVO, pageable);
        } else if (busqueda != null && !busqueda.trim().isEmpty()) {
            page = calificacionesRepository.searchByEstudianteNombre(busqueda, ESTADO_ACTIVO, pageable);
        } else {
            page = calificacionesRepository.findAllActivos(ESTADO_ACTIVO, pageable);
        }

        List<Map<String, Object>> calificacionesMap = new ArrayList<>();
        Map<Long, String> cacheMaterias = new HashMap<>();

        for (Calificaciones c : page.getContent()) {
            Map<String, Object> row = new HashMap<>();

            row.put("id", c.getIdCalificaciones());
            row.put("nota", c.getCalificacion());

            String nombreEstudiante = c.getMatricula().getEstudiante().getNombre() + " "
                    + c.getMatricula().getEstudiante().getPrimerApellido();
            row.put("estudiante", nombreEstudiante);

            if (c.getMatricula() != null && c.getMatricula().getSeccion() != null) {
                row.put("seccion", c.getMatricula().getSeccion().getNombreCompleto());
                row.put("idSeccion", c.getMatricula().getSeccion().getIdSeccion());
            } else {
                row.put("seccion", "Sin sección");
                row.put("idSeccion", null);
            }

            if (c.getMatricula() != null && c.getMatricula().getEstudiante() != null
                    && c.getMatricula().getEstudiante().getCorreos() != null
                    && !c.getMatricula().getEstudiante().getCorreos().isEmpty()) {

                row.put("correo", c.getMatricula().getEstudiante().getCorreos().get(0).getCorreo());
            } else {
                row.put("correo", "Sin correo");
            }

            String nombreMateria = "Sin materia";
            if (c.getEvaluacion() != null && c.getEvaluacion().getSeccionMateria() != null) {
                Long idMateria = c.getEvaluacion().getSeccionMateria().getIdMateriaFk();
                if (idMateria != null) {
                    if (cacheMaterias.containsKey(idMateria)) {
                        nombreMateria = cacheMaterias.get(idMateria);
                    } else {
                        Optional<Materia> materiaOpt = materiaRepository.findById(idMateria);
                        if (materiaOpt.isPresent()) {
                            nombreMateria = materiaOpt.get().getNombreCompleto();
                            cacheMaterias.put(idMateria, nombreMateria);
                        }
                    }
                }
            }
            row.put("materia", nombreMateria);
            row.put("evaluacion", c.getEvaluacion().getTipo());

            calificacionesMap.add(row);
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("calificaciones", calificacionesMap);
        resultado.put("paginaActual", pagina);
        resultado.put("totalPaginas", page.getTotalPages());
        resultado.put("totalElementos", page.getTotalElements());
        resultado.put("tamanoPagina", tamanoPagina);

        return resultado;
    }

    public List<Map<String, Object>> obtenerTodasLasSecciones() {
        List<Seccion> secciones = seccionRepository.findAll();
        List<Map<String, Object>> listaSecciones = new ArrayList<>();

        for (Seccion s : secciones) {
            if (s.isActiva()) {
                Map<String, Object> seccionMap = new HashMap<>();
                seccionMap.put("id", s.getIdSeccion());
                seccionMap.put("nombre", s.getNombreCompleto());
                listaSecciones.add(seccionMap);
            }
        }
        return listaSecciones;
    }

    public Calificaciones buscarPorId(Long id) {
        return calificacionesRepository.findById(id).orElse(null);
    }
}

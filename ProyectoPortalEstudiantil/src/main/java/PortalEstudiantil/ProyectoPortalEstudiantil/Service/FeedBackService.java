package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.FeedBackRequest;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.FeedBackRequest.FeedBackItem;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.FeedBackResumen;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.FeedBackRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedBackService {

    private static final int UMBRAL_ALERTA = 2;
    private static final int ESTADO_ACTIVO = 1;

    @Autowired
    private FeedBackRepository feedback360Repository;

    public List<FeedBackResumen> listarTodos() {
        return feedback360Repository.listarTodos();
    }

    public List<Map<String, Object>> promediosPorSeccion() {
        return feedback360Repository.promediosPorSeccion();
    }

    public List<Map<String, Object>> promediosPorDocente() {
        return feedback360Repository.promediosPorDocente();
    }

    public List<FeedBackResumen> listarAlertas() {
        return feedback360Repository.listarAlertas(UMBRAL_ALERTA);
    }

    public List<Map<String, Object>> compararPeriodos(Integer idPeriodo1,
            Integer idPeriodo2) {
        if (idPeriodo1 == null || idPeriodo2 == null) {
            throw new IllegalArgumentException(
                    "Debés seleccionar dos períodos distintos para comparar.");
        }
        if (idPeriodo1.equals(idPeriodo2)) {
            throw new IllegalArgumentException(
                    "Los períodos seleccionados deben ser distintos.");
        }
        return feedback360Repository.comparativaPorPeriodos(idPeriodo1, idPeriodo2);
    }

    public List<FeedBackResumen> listarPorDocente(Long idDocente) {
        return feedback360Repository.listarPorDocente(idDocente);
    }

    public List<FeedBackResumen> listarEstudiantesConFeedback(
            Integer idSeccionmateria) {
        return feedback360Repository.listarEstudiantesConFeedback(idSeccionmateria);
    }

    @Transactional
    public void registrarBatch(FeedBackRequest request) {

        if (request.getIdSeccionmateria() == null) {
            throw new IllegalArgumentException("Debe indicar la sección-materia.");
        }

        List<FeedBackItem> items = request.getEvaluaciones();
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("No hay evaluaciones para guardar.");
        }

        LocalDate hoy = LocalDate.now();

        for (FeedBackItem item : items) {

            // Validar calificación
            if (item.getCalificacion() == null
                    || item.getCalificacion() < 1
                    || item.getCalificacion() > 5) {
                throw new IllegalArgumentException(
                        "La calificación debe estar entre 1 y 5. "
                        + "Verificá el registro del estudiante "
                        + item.getIdMatricula());
            }

            String comentario = (item.getComentario() != null
                    && !item.getComentario().isBlank())
                    ? item.getComentario().trim()
                    : null;

            long count = feedback360Repository.contarExistente(request.getIdSeccionmateria(),
                    item.getIdMatricula());
            if (item.getIdFeedbackExistente() != null) {
                feedback360Repository.modificarFeedback(
                        item.getIdFeedbackExistente(),
                        item.getCalificacion(),
                        comentario,
                        hoy,
                        request.getIdSeccionmateria(),
                        item.getIdMatricula());
            } else if (count == 0) {
                feedback360Repository.insertarFeedback(item.getCalificacion(),
                        comentario,
                        hoy,
                        request.getIdSeccionmateria(),
                        item.getIdMatricula(),
                        ESTADO_ACTIVO);
            }
        }
    }

    public List<FeedBackResumen> listarPorEstudiante(Long idEstudiante,
            Integer idMateria) {
        if (idMateria != null) {
            return feedback360Repository
                    .listarPorEstudianteYMateria(idEstudiante, idMateria);
        }
        return feedback360Repository.listarPorEstudiante(idEstudiante);
    }

    public List<FeedBackResumen> listarPorEncargado(Long idEncargado,
            Integer idMateria) {
        if (idMateria != null) {
            return feedback360Repository
                    .listarPorEncargadoYMateria(idEncargado, idMateria);
        }
        return feedback360Repository.listarPorEncargado(idEncargado);
    }

    @Transactional
    public void cambiarEstado(Integer idFeedback, Integer idEstado) {
        feedback360Repository.cambiarEstadoFeedback(idFeedback, idEstado);
    }
}

package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.MatriculaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MatriculaService {

    @Autowired
    private MatriculaRepository matriculaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<MatriculaRepository.MatriculaRow> listTodos() {
        return matriculaRepository.listarTodos();
    }

    public List<MatriculaRepository.MatriculaRow> listarPorSeccion(Long idSeccion) {
        return matriculaRepository.listarPorSeccion(idSeccion);
    }

    public long contarMatriculadosPorSeccion(Long idSeccion) {
        return matriculaRepository.contarPorSeccionYEstado(idSeccion, 1L);
    }

    public long contarActivas() {
        return matriculaRepository.contarPorEstado(1L);
    }

    @Transactional
    public void insertar(LocalDate fechaMatricula, Long idEstudiante, Long idSeccion) {
        if (matriculaRepository.contarEstudianteEnSeccion(idEstudiante, idSeccion) > 0) {
            throw new IllegalArgumentException(
                    "El estudiante ya está matriculado en esta sección.");
        }
        entityManager.createNativeQuery(
                "CALL MATRICULA_INSERTAR(:fecha, :estudiante, :seccion, :estado)")
                .setParameter("fecha", fechaMatricula)
                .setParameter("estudiante", idEstudiante)
                .setParameter("seccion", idSeccion)
                .setParameter("estado", 1)
                .executeUpdate();
    }

    @Transactional
    public void cambiarEstado(Long idMatricula, Long idEstado) {
        entityManager.createNativeQuery(
                "CALL MATRICULA_CAMBIAR_ESTADO(:idMatricula, :idEstado)")
                .setParameter("idMatricula", idMatricula)
                .setParameter("idEstado", idEstado)
                .executeUpdate();
    }
}

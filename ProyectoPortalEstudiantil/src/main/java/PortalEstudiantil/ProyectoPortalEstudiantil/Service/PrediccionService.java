package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.PrediccionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class PrediccionService {

    private static final BigDecimal UMBRAL_ALTO  = new BigDecimal("65");
    private static final BigDecimal UMBRAL_MEDIO = new BigDecimal("75");

    private final PrediccionRepository repo;

    public PrediccionService(PrediccionRepository repo) {
        this.repo = repo;
    }


    //  ENCARGADO / ESTUDIANTE


    public List<Map<String, Object>> hijosDeEncargado(Long idEncargado) {
        return repo.listarHijosDeEncargado(idEncargado);
    }

    public List<Map<String, Object>> calificacionesPorMateria(Long idMatricula, Long idPeriodo) {
        return repo.calificacionesPorMateria(idMatricula, idPeriodo);
    }

    public BigDecimal promedioGeneral(Long idMatricula, Long idPeriodo) {
        return repo.promedioGeneral(idMatricula, idPeriodo);
    }

    public Map<String, Object> resumenAsistencia(Long idMatricula, Long idPeriodo) {
        return repo.resumenAsistencia(idMatricula, idPeriodo);
    }

    
    //  DOCENTE / ADMIN
  

    public List<Map<String, Object>> prediccionDocente(Long idDocente, Long idPeriodo) {
        return repo.prediccionDocente(idDocente, idPeriodo);
    }

    public List<Map<String, Object>> prediccionAdmin(Long idPeriodo) {
        return repo.prediccionAdmin(idPeriodo);
    }

    public List<Map<String, Object>> estadisticasPorPeriodo(Long idPeriodo) {
        return repo.estadisticasPorPeriodo(idPeriodo);
    }

    
    //  LÓGICA DE RIESGO
    

    public String calcularNivel(BigDecimal promedio) {
        if (promedio == null || promedio.compareTo(BigDecimal.ZERO) == 0) return "SIN_DATOS";
        if (promedio.compareTo(UMBRAL_ALTO)  < 0) return "ALTO";
        if (promedio.compareTo(UMBRAL_MEDIO) < 0) return "MEDIO";
        return "BAJO";
    }

    public String mensajeAlerta(String nivel, BigDecimal promedio) {
        return switch (nivel) {
            case "ALTO"  -> "Tu promedio actual es " + promedio +
                            ". Estás por debajo del mínimo de aprobación (65 pts). " +
                            "Te recomendamos hablar con tu docente y reforzar los temas con menor calificación.";
            case "MEDIO" -> "Tu promedio actual es " + promedio +
                            ". Estás en zona de alerta (entre 65 y 75 pts). " +
                            "Con un poco más de esfuerzo puedes mejorar tu rendimiento.";
            case "BAJO"  -> "Tu promedio actual es " + promedio +
                            ". Vas muy bien. Sigue así para mantener tu nivel académico.";
            default      -> "Aún no hay calificaciones registradas para este período.";
        };
    }

    /** Porcentaje de asistencia (presentes / total registros) */
    public int porcentajeAsistencia(Map<String, Object> asistencia) {
        long total = toLong(asistencia.get("total"));
        if (total == 0) return 0;
        long presentes = toLong(asistencia.get("presentes"))
                       + toLong(asistencia.get("justificados")); // justificado cuenta como asistencia
        return (int) Math.round(presentes * 100.0 / total);
    }

    private long toLong(Object val) {
        if (val == null) return 0;
        return ((Number) val).longValue();
    }
}
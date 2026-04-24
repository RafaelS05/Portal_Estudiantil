package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import java.time.LocalDate;

public interface FeedBackResumen {

    Integer   getIdFeedback();
    Integer   getCalificacion();
    String    getComentario();
    LocalDate getFechaEvaluacion();

    String    getNombreEstudiante();
    String    getNombreDocente();
    String    getNombreMateria();
    String    getNombreSeccion();

    Integer   getIdSeccionmateria();
    Integer   getIdMatricula();
    Integer   getIdEstado();


    default boolean isAlerta() {
        return getCalificacion() != null && getCalificacion() <= 2;
    }

    default String getNivelCalificacion() {
        if (getCalificacion() == null) return "Sin calificar";
        return switch (getCalificacion()) {
            case 1 -> "Deficiente";
            case 2 -> "Regular";
            case 3 -> "Bueno";
            case 4 -> "Muy Bueno";
            case 5 -> "Excelente";
            default -> "Sin calificar";
        };
    }

    default String getEstrellasTxt() {
        if (getCalificacion() == null) return "☆☆☆☆☆";
        return "★".repeat(getCalificacion()) + "☆".repeat(5 - getCalificacion());
    }
}

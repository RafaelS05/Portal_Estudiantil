package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class FeedBackRequest {

    private Integer idSeccionmateria;
    private List<FeedBackItem> evaluaciones;

    @Getter
    @Setter
    public static class FeedBackItem {

        private Integer idMatricula;
        private Integer calificacion;
        private String comentario;
        private Integer idFeedbackExistente;
    }
}

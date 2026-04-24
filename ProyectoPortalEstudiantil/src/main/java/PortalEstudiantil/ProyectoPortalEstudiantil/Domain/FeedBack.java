package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "FEEDBACK360_TB")
public class FeedBack {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_FEEDBACK")
    private Long idFeedback;

    @Column(name = "CALIFICACION")
    private Integer calificacion;

    @Column(name = "COMENTARIO")
    private String comentario;

    @Column(name = "FECHA_EVALUACION")
    private LocalDate fechaEvaluacion;

    @Column(name = "ID_SECCIONMATERIA_FK")
    private Integer idSeccionmateriaFk;

    @Column(name = "ID_MATRICULA_FK")
    private Integer idMatriculaFk;

    @Column(name = "ID_ESTADO_FK")
    private Integer idEstadoFk;

    @Column(name = "FECHA_CREACION", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHA_MODIFICACION")
    private LocalDateTime fechaModificacion;

    @Column(name = "CREADO_POR", updatable = false)
    private String creadoPor;

    @Column(name = "MODIFICADO_POR")
    private String modificadoPor;

    @Column(name = "ACCION")
    private String accion;
    
    public boolean isActivo() {
        return idEstadoFk != null && idEstadoFk == 1;
    }
    public boolean isAlerta() {
        return calificacion != null && calificacion <= 2;
    }

    public String getNivelCalificacion() {
        if (calificacion == null) return "Sin calificar";
        return switch (calificacion) {
            case 1 -> "Deficiente";
            case 2 -> "Regular";
            case 3 -> "Bueno";
            case 4 -> "Muy Bueno";
            case 5 -> "Excelente";
            default -> "Sin calificar";
        };
    }

    public String getEstrellasTxt() {
        if (calificacion == null) return "☆☆☆☆☆";
        return "★".repeat(calificacion) + "☆".repeat(5 - calificacion);
    }
}

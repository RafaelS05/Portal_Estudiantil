package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "EVALUACIONSOPORTE_TB")
public class TicketEvaluacionSoporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EVALUACIONSOPORTE")
    private Long idEvaluacionSoporte;

    @Column(name = "PUNTUACION")
    private Integer puntuacion;

    @Column(name = "COMENTARIO")
    private String comentario;

    @Column(name = "FECHA_EVALUACION", updatable = false)
    private LocalDateTime fechaEvaluacion;

    @Column(name = "ID_USUARIO_FK")
    private Long idUsuarioFk;

    @Column(name = "ID_TICKET_FK")
    private Long idTicketFk;

    @Column(name = "ID_ESTADO_FK")
    private Long idEstadoFk;

    //////////////////////////////////////////////////
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

}

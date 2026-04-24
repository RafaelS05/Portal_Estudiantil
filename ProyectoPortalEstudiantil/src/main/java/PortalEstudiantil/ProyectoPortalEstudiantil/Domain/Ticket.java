package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "TICKET_TB")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TICKET")
    private Long idTicket;

    @Column(name = "TITULO")
    private String titulo;

    @Column(name = "COMENTARIO")
    private String comentario;

    @Column(name = "FECHA_CREACION_TICKET", updatable = false)
    private LocalDateTime fechaCreacionTicket;

    @Column(name = "FECHA_CIERRE", updatable = false)
    private LocalDateTime fechaCierre;

    @Column(name = "PRIORIDAD")
    private String prioridad;

    @Column(name = "ID_USUARIO_REPORTA_FK")
    private Long idUsuarioReportaFk;

    @Column(name = "ID_USUARIO_TECNICO_FK")
    private Long idUsuarioTecnicoFk;

    @Column(name = "ID_CATEGORIA_FK")
    private Long idCategoriaFk;

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

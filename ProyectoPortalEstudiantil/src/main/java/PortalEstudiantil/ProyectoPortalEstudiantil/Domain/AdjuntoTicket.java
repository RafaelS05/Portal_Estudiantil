package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ADJUNTOTICKET_TB")
public class AdjuntoTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ADJUNTOTICKET")
    private Long idAdjuntoTicket;

    @Column(name = "RUTA_ARCHIVO")
    private String rutaArchivo;

    @Column(name = "ID_USUARIO_SUBIDO_POR_FK")
    private Long idUsuarioSubidoFk;

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

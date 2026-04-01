package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "CATEGORIATICKET_TB")
public class CategoriaTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CATEGORIA")
    private Long idCategoriaTicket;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "ID_ESTADO_FK")
    private Long idEstadoFk;

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

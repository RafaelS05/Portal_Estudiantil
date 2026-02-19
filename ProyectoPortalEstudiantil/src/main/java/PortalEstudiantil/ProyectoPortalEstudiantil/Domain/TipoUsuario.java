package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.*;

@Entity
@Table(name = "TIPOUSUARIO_TB")
public class TipoUsuario {

    @Id
    @Column(name = "ID_TIPOUSUARIO")
    private Long idTipoUsuario;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "ID_ESTADO_FK")
    private Long idEstadoFk;

    public Long getIdTipoUsuario() { return idTipoUsuario; }
    public void setIdTipoUsuario(Long idTipoUsuario) { this.idTipoUsuario = idTipoUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Long getIdEstadoFk() { return idEstadoFk; }
    public void setIdEstadoFk(Long idEstadoFk) { this.idEstadoFk = idEstadoFk; }
}

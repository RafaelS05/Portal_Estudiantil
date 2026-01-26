package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.*;

@Entity
@Table(name = "DIRECCION_TB")
public class Direccion {

    @Id
    @Column(name = "ID_DIRECCION")
    private Long idDireccion;

    @Column(name = "OTRAS_SENAS")
    private String otrasSenas;

    @ManyToOne
    @JoinColumn(name = "ID_USUARIO_FK")
    private Usuario usuario;

    // getters y setters

    public Long getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(Long idDireccion) {
        this.idDireccion = idDireccion;
    }

    public String getOtrasSenas() {
        return otrasSenas;
    }

    public void setOtrasSenas(String otrasSenas) {
        this.otrasSenas = otrasSenas;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}

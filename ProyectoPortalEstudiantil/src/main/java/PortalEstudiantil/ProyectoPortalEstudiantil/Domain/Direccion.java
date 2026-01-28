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

    @ManyToOne
    @JoinColumn(name = "ID_PROVINCIA_FK")
    private Provincia provincia;

    @ManyToOne
    @JoinColumn(name = "ID_CANTON_FK")
    private Canton canton;

    @ManyToOne
    @JoinColumn(name = "ID_DISTRITO_FK")
    private Distrito distrito;

    @Column(name = "ID_ESTADO_FK")
    private Long idEstado;

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

    public Provincia getProvincia() {
        return provincia;
    }

    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }

    public Canton getCanton() {
        return canton;
    }

    public void setCanton(Canton canton) {
        this.canton = canton;
    }

    public Distrito getDistrito() {
        return distrito;
    }

    public void setDistrito(Distrito distrito) {
        this.distrito = distrito;
    }

    public Long getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Long idEstado) {
        this.idEstado = idEstado;
    }




   
}

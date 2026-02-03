package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "USUARIOS_TB")
public class Usuario {

    @Id
    @Column(name = "ID_USUARIO")
    private Long idUsuario;

    private String nombre;

    @Column(name = "PRIMER_APELLIDO")
    private String primerApellido;

    @Column(name = "SEGUNDO_APELLIDO")
    private String segundoApellido;

    @Column(name = "ID_TIPOUSUARIO_FK")
    private Long idTipoUsuarioFk;

    @Column(name = "ID_ESTADO_FK")
    private Long idEstadoFk;

    @OneToMany(mappedBy = "usuario")
    private List<Correo> correos;

    @OneToMany(mappedBy = "usuario")
    private List<Telefono> telefonos;

    @OneToMany(mappedBy = "usuario")
    private List<Direccion> direcciones;

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public Long getIdTipoUsuarioFk() {
        return idTipoUsuarioFk;
    }

    public void setIdTipoUsuarioFk(Long idTipoUsuarioFk) {
        this.idTipoUsuarioFk = idTipoUsuarioFk;
    }

    public Long getIdEstadoFk() {
        return idEstadoFk;
    }

    public void setIdEstadoFk(Long idEstadoFk) {
        this.idEstadoFk = idEstadoFk;
    }

    public List<Direccion> getDirecciones() {
        return direcciones;
    }

    public void setDirecciones(List<Direccion> direcciones) {
        this.direcciones = direcciones;
    }

    @Transient
    public Direccion getDireccion() {
        if (direcciones != null && !direcciones.isEmpty()) {
            return direcciones.get(0);
        } else {
            return null;
        }
    }

    @Transient
    public void setDireccion(Direccion direccion) {
        if (direcciones == null) {
            direcciones = new ArrayList<>();
        }

        if (direcciones.isEmpty()) {
            direcciones.add(direccion);
        } else {
            direcciones.set(0, direccion);
        }
    }

    public List<Correo> getCorreos() {
        return correos;
    }

    public void setCorreos(List<Correo> correos) {
        this.correos = correos;
    }

    public List<Telefono> getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(List<Telefono> telefonos) {
        this.telefonos = telefonos;
    }
}
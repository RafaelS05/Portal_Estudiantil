/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.*;

@Entity
@Table(name = "USUARIOS_TB")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USUARIO")
    private Long idUsuario;
    
    @Column(name = "NOMBRE", nullable = false, length = 80)
    private String nombre;
    
    @Column(name = "PRIMER_APELLIDO", nullable = false, length = 80)
    private String primerApellido;
    
    @Column(name = "SEGUNDO_APELLIDO", length = 80)
    private String segundoApellido;
    
    @ManyToOne
    @JoinColumn(name = "ID_TIPOUSUARIO_FK", nullable = false)
    private TipoUsuario tipoUsuario;
    
    @ManyToOne
    @JoinColumn(name = "ID_ESTADO_FK", nullable = false)
    private Estado estado;
    
    // Constructores
    public Usuario() {
    }
    
    public Usuario(String nombre, String primerApellido, String segundoApellido, 
                   TipoUsuario tipoUsuario, Estado estado) {
        this.nombre = nombre;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.tipoUsuario = tipoUsuario;
        this.estado = estado;
    }
    
    // Getters y Setters
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
    
    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }
    
    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
    
    public Estado getEstado() {
        return estado;
    }
    
    public void setEstado(Estado estado) {
        this.estado = estado;
    }
    
    // Método de conveniencia
    public String getNombreCompleto() {
        return nombre + " " + primerApellido + 
               (segundoApellido != null ? " " + segundoApellido : "");
    }
}
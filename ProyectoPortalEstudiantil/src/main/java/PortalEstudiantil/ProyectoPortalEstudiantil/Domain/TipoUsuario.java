/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.*;

@Entity
@Table(name = "TIPOUSUARIO_TB")
public class TipoUsuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TIPOUSUARIO")
    private Long idTipoUsuario;
    
    @Column(name = "NOMBRE", nullable = false, unique = true, length = 50)
    private String nombre;
    
    @ManyToOne
    @JoinColumn(name = "ID_ESTADO_FK", nullable = false)
    private Estado estado;
    
    // Constructores
    public TipoUsuario() {
    }
    
    public TipoUsuario(String nombre, Estado estado) {
        this.nombre = nombre;
        this.estado = estado;
    }
    
    // Getters y Setters
    public Long getIdTipoUsuario() {
        return idTipoUsuario;
    }
    
    public void setIdTipoUsuario(Long idTipoUsuario) {
        this.idTipoUsuario = idTipoUsuario;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public Estado getEstado() {
        return estado;
    }
    
    public void setEstado(Estado estado) {
        this.estado = estado;
    }
}
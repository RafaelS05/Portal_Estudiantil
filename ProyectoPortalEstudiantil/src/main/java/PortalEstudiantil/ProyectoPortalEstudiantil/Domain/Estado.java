/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.*;

@Entity
@Table(name = "ESTADOS_TB")
public class Estado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ESTADO")
    private Long idEstado;
    
    // IMPORTANTE: En tu BD la columna se llama DESCRIPCION, no NOMBRE
    @Column(name = "DESCRIPCION", nullable = false, length = 200)
    private String descripcion;  // Cambiamos de "nombre" a "descripcion"
    
    // Constructores
    public Estado() {
    }
    
    public Estado(String descripcion) {
        this.descripcion = descripcion;
    }
    
    // Getters y Setters
    public Long getIdEstado() {
        return idEstado;
    }
    
    public void setIdEstado(Long idEstado) {
        this.idEstado = idEstado;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    // Método de conveniencia - Para usar "getNombre()" en la vista si prefieres
    public String getNombre() {
        return descripcion;
    }
}
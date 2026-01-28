/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "PROVINCIA_TB")
public class Provincia {

    @Id
    @Column(name = "ID_PROVINCIA")
    private Long idProvincia;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "ID_ESTADO_FK")
    private Long idEstado;

    // getters y setters

    public Long getIdProvincia() {
        return idProvincia;
    }

    public void setIdProvincia(Long idProvincia) {
        this.idProvincia = idProvincia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Long idEstado) {
        this.idEstado = idEstado;
    }
    
}

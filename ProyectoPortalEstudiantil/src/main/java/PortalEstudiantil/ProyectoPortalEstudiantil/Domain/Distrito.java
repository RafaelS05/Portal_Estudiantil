/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
@Entity
@Table(name = "DISTRITO_TB")
public class Distrito {

    @Id
    @Column(name = "ID_DISTRITO")
    private Long idDistrito;

    @Column(name = "NOMBRE")
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "ID_CANTON_FK")
    private Canton canton;

    @Column(name = "ID_ESTADO_FK")
    private Long idEstado;

    // getters y setters
}

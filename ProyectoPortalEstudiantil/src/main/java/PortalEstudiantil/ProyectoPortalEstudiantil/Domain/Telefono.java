/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.*;

@Entity
@Table(name = "TELEFONO_TB")
public class Telefono {

    @Id
    @Column(name = "ID_TELEFONO")
    private Long idTelefono;

    private String numero;

    @Column(name = "ID_USUARIO_FK")
    private Long idUsuario;

    public Long getIdTelefono() { return idTelefono; }
    public void setIdTelefono(Long idTelefono) { this.idTelefono = idTelefono; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
}

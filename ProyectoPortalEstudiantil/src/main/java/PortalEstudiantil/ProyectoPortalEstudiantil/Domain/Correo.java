/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;



import jakarta.persistence.*;

@Entity
@Table(name = "CORREO_TB")
public class Correo {

    @Id
    @Column(name = "ID_CORREO")
    private Long idCorreo;

    private String correo;

    @Column(name = "ES_LOGIN")
    private String esLogin;

    @Column(name = "ID_USUARIO_FK")
    private Long idUsuario;

    public Long getIdCorreo() { return idCorreo; }
    public void setIdCorreo(Long idCorreo) { this.idCorreo = idCorreo; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getEsLogin() { return esLogin; }
    public void setEsLogin(String esLogin) { this.esLogin = esLogin; }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
}

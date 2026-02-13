package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.*;

@Entity
@Table(name = "ENCARGADOESTUDIANTE_TB")
public class EncargadoEstudiante {

    @Id
   
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ENCARGADOESTUDIANTE")
    private Long idEncargadoEstudiante;

    @Column(name = "PARENTESCO")
    private String parentesco;

    @Column(name = "ID_USUARIO_ESTUDIANTE_FK")
    private Long idUsuarioEstudianteFk;

    @Column(name = "ID_USUARIO_ENCARGADO_FK")
    private Long idUsuarioEncargadoFk;

    @Column(name = "ID_ESTADO_FK")
    private Long idEstadoFk;

    public Long getIdEncargadoEstudiante() { return idEncargadoEstudiante; }
    public void setIdEncargadoEstudiante(Long idEncargadoEstudiante) { this.idEncargadoEstudiante = idEncargadoEstudiante; }

    public String getParentesco() { return parentesco; }
    public void setParentesco(String parentesco) { this.parentesco = parentesco; }

    public Long getIdUsuarioEstudianteFk() { return idUsuarioEstudianteFk; }
    public void setIdUsuarioEstudianteFk(Long idUsuarioEstudianteFk) { this.idUsuarioEstudianteFk = idUsuarioEstudianteFk; }

    public Long getIdUsuarioEncargadoFk() { return idUsuarioEncargadoFk; }
    public void setIdUsuarioEncargadoFk(Long idUsuarioEncargadoFk) { this.idUsuarioEncargadoFk = idUsuarioEncargadoFk; }

    public Long getIdEstadoFk() { return idEstadoFk; }
    public void setIdEstadoFk(Long idEstadoFk) { this.idEstadoFk = idEstadoFk; }
}
package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ASISTENCIAS_TB")
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ASISTENCIA")
    private Long idAsistencia;

    @Column(name = "FECHA_ASISTENCIA")
    @Temporal(TemporalType.DATE)
    private Date fechaAsistencia;

    @Column(name = "ID_MATRICULA_FK")
    private Long idMatriculaFk;

    @Column(name = "ID_SECCIONMATERIA_FK")
    private Long idSeccionMateriaFk;

    @Column(name = "ID_ESTADO_FK")
    private Long idEstadoFk;

    // Getters y Setters
    public Long getIdAsistencia() {
        return idAsistencia;
    }

    public void setIdAsistencia(Long idAsistencia) {
        this.idAsistencia = idAsistencia;
    }

    public Date getFechaAsistencia() {
        return fechaAsistencia;
    }

    public void setFechaAsistencia(Date fechaAsistencia) {
        this.fechaAsistencia = fechaAsistencia;
    }

    public Long getIdMatriculaFk() {
        return idMatriculaFk;
    }

    public void setIdMatriculaFk(Long idMatriculaFk) {
        this.idMatriculaFk = idMatriculaFk;
    }

    public Long getIdSeccionMateriaFk() {
        return idSeccionMateriaFk;
    }

    public void setIdSeccionMateriaFk(Long idSeccionMateriaFk) {
        this.idSeccionMateriaFk = idSeccionMateriaFk;
    }

    public Long getIdEstadoFk() {
        return idEstadoFk;
    }

    public void setIdEstadoFk(Long idEstadoFk) {
        this.idEstadoFk = idEstadoFk;
    }
}

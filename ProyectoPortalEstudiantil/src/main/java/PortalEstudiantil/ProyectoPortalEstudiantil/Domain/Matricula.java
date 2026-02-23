package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "MATRICULA_TB")

public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MATRICULA")
    private Long idMatricula;

    @Column(name = "FECHA_MATRICULA")
    private LocalDate fechaMatricula;

    @ManyToOne
    @JoinColumn(name = "ID_USUARIO_ESTUDIANTE_FK")
    private Usuario estudiante;

    @ManyToOne
    @JoinColumn(name = "ID_SECCION_FK")
    private Seccion seccion;

    //Estados FK

    public Long getIdMatricula() {
        return idMatricula;
    }

    public void setIdMatricula(Long idMatricula) {
        this.idMatricula = idMatricula;
    }

    public LocalDate getFechaMatricula() {
        return fechaMatricula;
    }

    public void setFechaMatricula(LocalDate fechaMatricula) {
        this.fechaMatricula = fechaMatricula;
    }

    public Usuario getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Usuario estudiante) {
        this.estudiante = estudiante;
    }

    public Seccion getSeccion() {
        return seccion;
    }

    public void setSeccion(Seccion seccion) {
        this.seccion = seccion;
    }
    
}           


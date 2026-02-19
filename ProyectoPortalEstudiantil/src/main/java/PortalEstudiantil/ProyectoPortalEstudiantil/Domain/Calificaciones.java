/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 *
 * @author marjo
 */

@Entity
@Table(name = "CALIFICACIONES_TB")

public class Calificaciones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CALIFICACIONES")
    private Long idCalificaciones;
    
    @Column(name = "CALIFICACION", precision = 5, scale = 2)
    private java.math.BigDecimal calificacion;
  
    // Aquí falta el FK de Estados
    @ManyToOne
    @JoinColumn(name = "ID_MATRICULA_FK")
    private Matricula matricula;
 
    @ManyToOne
    @JoinColumn(name = "ID_EVALUACION_FK")
    private Evaluacion evaluacion;

    public Long getIdCalificaciones() {
        return idCalificaciones;
    }

    public void setIdCalificaciones(Long idCalificaciones) {
        this.idCalificaciones = idCalificaciones;
    }

    public BigDecimal getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(BigDecimal calificacion) {
        this.calificacion = calificacion;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public void setMatricula(Matricula matricula) {
        this.matricula = matricula;
    }

    public Evaluacion getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(Evaluacion evaluacion) {
        this.evaluacion = evaluacion;
    }
   
}

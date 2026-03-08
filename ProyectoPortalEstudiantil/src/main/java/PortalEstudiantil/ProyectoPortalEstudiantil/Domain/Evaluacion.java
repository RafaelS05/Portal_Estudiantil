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
@Table(name = "EVALUACION_TB")

public class Evaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EVALUACION")
    private Long idEvaluacion;

    @Column(name = "TIPO", length = 40, nullable = false)
    private String tipo;

    @Column(name = "PORCENTAJE", precision = 5, scale = 2)
    private java.math.BigDecimal porcentaje;

    @ManyToOne
    @JoinColumn(name = "ID_SECCIONMATERIA_FK")
    private SeccionMateria seccionMateria;

    @ManyToOne
    @JoinColumn(name = "ID_PERIODO_FK")
    private Periodo periodo;

    @Column(name = "ID_ESTADO_FK")
    private Long idEstadoFk;

    public Long getIdEvaluacion() {
        return idEvaluacion;
    }

    public void setIdEvaluacion(Long idEvaluacion) {
        this.idEvaluacion = idEvaluacion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public SeccionMateria getSeccionMateria() {
        return seccionMateria;
    }

    public void setSeccionMateria(SeccionMateria seccionMateria) {
        this.seccionMateria = seccionMateria;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }

    public Long getIdEstadoFk() {
        return idEstadoFk;
    }

    public void setIdEstadoFk(Long idEstadoFk) {
        this.idEstadoFk = idEstadoFk;
    }

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.*;

/**
 *
 * @author marjo
 */
@Entity
@Table(name = "RECURSOS_APRENDIZAJE_TB")
public class RecursoAprendizaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_RECURSO")
    private Integer idRecurso;

    @Column(name = "TITULO", length = 200, nullable = false)
    private String titulo;

    @Column(name = "DESCRIPCION", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "URL_RECURSO", length = 500, nullable = false)
    private String urlRecurso;

    @Column(name = "ID_ESTADO_FK", nullable = false)
    private Integer idEstadoFk;

    public Integer getIdRecurso() {
        return idRecurso;
    }

    public void setIdRecurso(Integer idRecurso) {
        this.idRecurso = idRecurso;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUrlRecurso() {
        return urlRecurso;
    }

    public void setUrlRecurso(String urlRecurso) {
        this.urlRecurso = urlRecurso;
    }

    public Integer getIdEstadoFk() {
        return idEstadoFk;
    }

    public void setIdEstadoFk(Integer idEstadoFk) {
        this.idEstadoFk = idEstadoFk;
    }
    
}


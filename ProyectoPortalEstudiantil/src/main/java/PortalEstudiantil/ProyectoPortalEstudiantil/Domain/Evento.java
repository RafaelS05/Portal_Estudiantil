package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "EVENTO_TB")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EVENTO")
    private Long idEvento;

    @Column(name = "TITULO")
    private String titulo;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(name = "FECHA_INICIO")
    private LocalDate fechaInicio;

    @Column(name = "FECHA_FIN")
    private LocalDate fechaFin;

    @Column(name = "TIPO_EVENTO")
    private String tipoEvento; // 'NOTICIA' o 'ANUNCIO'

    @Column(name = "ID_TIPOVISIBILIDAD_FK")
    private Long idTipoVisibilidadFk;

    @Column(name = "ID_SECCION_FK")
    private Long idSeccionFk;

    @Column(name = "ID_SECCIONMATERIA_FK")
    private Long idSeccionMateriaFk;

    @Column(name = "ID_USUARIO_CREADO_POR_FK")
    private Long idUsuarioCreadoPorFk;

    @Column(name = "ID_ESTADO_FK")
    private Long idEstadoFk;

    @Column(name = "FECHA_CREACION", insertable = false, updatable = false)
    private java.time.LocalDateTime fechaCreacion;

    // ── Getters / Setters ──────────────────────────────────────────────
    public Long getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(Long idEvento) {
        this.idEvento = idEvento;
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

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public Long getIdTipoVisibilidadFk() {
        return idTipoVisibilidadFk;
    }

    public void setIdTipoVisibilidadFk(Long idTipoVisibilidadFk) {
        this.idTipoVisibilidadFk = idTipoVisibilidadFk;
    }

    public Long getIdSeccionFk() {
        return idSeccionFk;
    }

    public void setIdSeccionFk(Long idSeccionFk) {
        this.idSeccionFk = idSeccionFk;
    }

    public Long getIdSeccionMateriaFk() {
        return idSeccionMateriaFk;
    }

    public void setIdSeccionMateriaFk(Long idSeccionMateriaFk) {
        this.idSeccionMateriaFk = idSeccionMateriaFk;
    }

    public Long getIdUsuarioCreadoPorFk() {
        return idUsuarioCreadoPorFk;
    }

    public void setIdUsuarioCreadoPorFk(Long idUsuarioCreadoPorFk) {
        this.idUsuarioCreadoPorFk = idUsuarioCreadoPorFk;
    }

    public Long getIdEstadoFk() {
        return idEstadoFk;
    }

    public void setIdEstadoFk(Long idEstadoFk) {
        this.idEstadoFk = idEstadoFk;
    }

    public java.time.LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(java.time.LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}

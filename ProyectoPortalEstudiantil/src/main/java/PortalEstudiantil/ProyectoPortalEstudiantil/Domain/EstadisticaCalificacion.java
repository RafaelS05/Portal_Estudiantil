package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.*;

@Entity
@Table(name = "ESTADISTICAS_CALIFICACIONES_TB")
public class EstadisticaCalificacion {
    
    @Id
    @Column(name = "ID_ESTADISTICA")
    private Long idEstadistica;
    
    @Column(name = "ID_MATERIA_FK")
    private Long idMateriaFk;
    
    @Column(name = "ID_SECCION_FK")
    private Long idSeccionFk;
    
    @Column(name = "ID_DOCENTE_FK")
    private Long idDocenteFk;
    
    @Column(name = "PROMEDIO")
    private Double promedio;
    
    @Column(name = "NOTA_MAXIMA")
    private Double notaMaxima;
    
    @Column(name = "NOTA_MINIMA")
    private Double notaMinima;
    
    @Column(name = "TOTAL_ESTUDIANTES")
    private Long totalEstudiantes;
    
    @Column(name = "APROBADOS")
    private Long aprobados;
    
    @Column(name = "REPROBADOS")
    private Long reprobados;
    
    @Column(name = "PORCENTAJE_APROBACION")
    private Double porcentajeAprobacion;
    
    @Column(name = "ID_ESTADO_FK")
    private Long idEstadoFk;
    
    // Getters y Setters
    public Long getIdEstadistica() {
        return idEstadistica;
    }
    
    public void setIdEstadistica(Long idEstadistica) {
        this.idEstadistica = idEstadistica;
    }
    
    public Long getIdMateriaFk() {
        return idMateriaFk;
    }
    
    public void setIdMateriaFk(Long idMateriaFk) {
        this.idMateriaFk = idMateriaFk;
    }
    
    public Long getIdSeccionFk() {
        return idSeccionFk;
    }
    
    public void setIdSeccionFk(Long idSeccionFk) {
        this.idSeccionFk = idSeccionFk;
    }
    
    public Long getIdDocenteFk() {
        return idDocenteFk;
    }
    
    public void setIdDocenteFk(Long idDocenteFk) {
        this.idDocenteFk = idDocenteFk;
    }
    
    public Double getPromedio() {
        return promedio;
    }
    
    public void setPromedio(Double promedio) {
        this.promedio = promedio;
    }
    
    public Double getNotaMaxima() {
        return notaMaxima;
    }
    
    public void setNotaMaxima(Double notaMaxima) {
        this.notaMaxima = notaMaxima;
    }
    
    public Double getNotaMinima() {
        return notaMinima;
    }
    
    public void setNotaMinima(Double notaMinima) {
        this.notaMinima = notaMinima;
    }
    
    public Long getTotalEstudiantes() {
        return totalEstudiantes;
    }
    
    public void setTotalEstudiantes(Long totalEstudiantes) {
        this.totalEstudiantes = totalEstudiantes;
    }
    
    public Long getAprobados() {
        return aprobados;
    }
    
    public void setAprobados(Long aprobados) {
        this.aprobados = aprobados;
    }
    
    public Long getReprobados() {
        return reprobados;
    }
    
    public void setReprobados(Long reprobados) {
        this.reprobados = reprobados;
    }
    
    public Double getPorcentajeAprobacion() {
        return porcentajeAprobacion;
    }
    
    public void setPorcentajeAprobacion(Double porcentajeAprobacion) {
        this.porcentajeAprobacion = porcentajeAprobacion;
    }
    
    public Long getIdEstadoFk() {
        return idEstadoFk;
    }
    
    public void setIdEstadoFk(Long idEstadoFk) {
        this.idEstadoFk = idEstadoFk;
    }
}
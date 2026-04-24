package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "REPORTESACADEMICOS_TB")
public class ReporteAcademico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_REPORTE")
    private Long idReporte;

    @Column(name = "FECHA_CREACION_REPORTE")
    private LocalDate fechaCreacionReporte;

    @Column(name = "PROMEDIO_PONDERADO")
    private BigDecimal promedioPonderado;

    @Column(name = "ID_TIPOREPORTE_FK")
    private Long idTipoReporteFk;

    @Column(name = "ID_GENERADO_POR_FK")
    private Long idGeneradoPorFk;

    @Column(name = "ID_MATRICULA_FK")
    private Long idMatriculaFk;

    @Column(name = "ID_PERIODO_FK")
    private Long idPeriodoFk;

    @Column(name = "ID_ESTADO_FK")
    private Long idEstadoFk;

    public Long getIdReporte()                          { return idReporte; }
    public void setIdReporte(Long v)                    { this.idReporte = v; }

    public LocalDate getFechaCreacionReporte()          { return fechaCreacionReporte; }
    public void setFechaCreacionReporte(LocalDate v)    { this.fechaCreacionReporte = v; }

    public BigDecimal getPromedioPonderado()             { return promedioPonderado; }
    public void setPromedioPonderado(BigDecimal v)       { this.promedioPonderado = v; }

    public Long getIdTipoReporteFk()                    { return idTipoReporteFk; }
    public void setIdTipoReporteFk(Long v)              { this.idTipoReporteFk = v; }

    public Long getIdGeneradoPorFk()                    { return idGeneradoPorFk; }
    public void setIdGeneradoPorFk(Long v)              { this.idGeneradoPorFk = v; }

    public Long getIdMatriculaFk()                      { return idMatriculaFk; }
    public void setIdMatriculaFk(Long v)                { this.idMatriculaFk = v; }

    public Long getIdPeriodoFk()                        { return idPeriodoFk; }
    public void setIdPeriodoFk(Long v)                  { this.idPeriodoFk = v; }

    public Long getIdEstadoFk()                         { return idEstadoFk; }
    public void setIdEstadoFk(Long v)                   { this.idEstadoFk = v; }
}
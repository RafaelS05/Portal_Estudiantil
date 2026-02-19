package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PERIODOS_TB")
public class Periodo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_PERIODO")
  private Long idPeriodo;

  @Column(name = "NOMBRE")
  private String nombre;

  @Column(name = "FECHA_INICIO")
  private LocalDate fechaInicio;

  @Column(name = "FECHA_FIN")
  private LocalDate fechaFin;

  @Column(name = "ID_ESTADO_FK")
  private Long idEstadoFk;
  
  public boolean isActivo() {
        return idEstadoFk != null && idEstadoFk == 1;
    }

    public boolean contieneFecha(LocalDate fecha) {
        if (fecha == null || fechaInicio == null || fechaFin == null) {
            return false;
        }
        return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
    }
    
    public boolean isEnCurso() {
        return contieneFecha(LocalDate.now());
    }
    
    public long getDuracionEnDias() {
        if (fechaInicio == null || fechaFin == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin);
    }
}


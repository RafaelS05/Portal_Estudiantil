package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "SECCION_TB")
public class Seccion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_SECCION")
  private Long idSeccion;

  @Column(name = "NUMERO")
  private String numero;

  @Column(name = "ID_PERIODO_FK")
  private Long idPeriodoFk;

  @Column(name = "ID_ESTADO_FK")
  private Long idEstadoFk;
  
  
    @Transient
    private String nombrePeriodo;
    @Transient
    private Integer cantidadEstudiantes;
  
    public boolean isActiva() {
        return idEstadoFk != null && idEstadoFk == 1; // 1 = ACTIVO
    }

    public String getNombreCompleto() {
        if (nombrePeriodo != null) {
            return String.format("Sección %s - %s", numero, nombrePeriodo);
        }
        return String.format("Sección %s", numero);
    }

    public boolean isNumeroValido() {
        if (numero == null || numero.trim().isEmpty()) {
            return false;
        }
        // Formato: 7-A, 8-B, 10-C
        return numero.matches("^\\d{1,2}-[A-Z]$");
    }

    public Integer getGrado() {
        if (numero == null || !isNumeroValido()) {
            return null;
        }
        String[] partes = numero.split("-");
        try {
            return Integer.parseInt(partes[0]);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public String getLetra() {
        if (numero == null || !isNumeroValido()) {
            return null;
        }
        String[] partes = numero.split("-");
        return partes.length > 1 ? partes[1] : null;
    }
}


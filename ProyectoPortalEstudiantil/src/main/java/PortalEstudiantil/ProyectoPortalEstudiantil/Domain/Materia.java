package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "MATERIA_TB")
public class Materia {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_MATERIA")
  private Long idMateria;

  @Column(name = "NOMBRE")
  private String nombre;

  @Column(name = "CODIGO")
  private String codigoMateria;

  @Column(name = "ID_ESTADO_FK")
  private Long idEstadoFk;
  
  public boolean isActiva() {
        return idEstadoFk != null && idEstadoFk == 1;
    }

    public String getNombreCompleto() {
        if (codigoMateria != null && !codigoMateria.trim().isEmpty()) {
            return String.format("%s (%s)", nombre, codigoMateria);
        }
        return nombre;
    }

    public boolean isCodigoValido() {
        if (codigoMateria == null || codigoMateria.trim().isEmpty()) {
            return false;
        }
        // Formato: MAT-101, ESP-202
        return codigoMateria.matches("^[A-Z]{3}-\\d{3}$");
    }
}


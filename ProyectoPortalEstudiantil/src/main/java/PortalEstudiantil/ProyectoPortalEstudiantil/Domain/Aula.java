package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "AULA_TB")
public class Aula {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_AULA")
  private Long idAula;

  @Column(name = "NUMERO")
  private String numero;

  @Column(name = "ID_ESTADO_FK")
  private Long idEstadoFk;
  
 public boolean isActiva() {
        return idEstadoFk != null && idEstadoFk == 1;
    }
}


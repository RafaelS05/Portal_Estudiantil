package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "HORARIO_TB")
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_HORARIO")
    private Long idHorario;

    @Column(name = "DIA_SEMANA")
    private int diaSemana;

    @Column(name = "HORA_INICIO")
    private String horaInicio;

    @Column(name = "HORA_FIN")
    private String horaFin;

    @Column(name = "ID_AULA_FK")
    private Long idAulaFk;

    @Column(name = "ID_SECCIONMATERIA_FK")
    private Long idSeccionMateriaFk;

    @Column(name = "ID_ESTADO_FK")
    private Long idEstadoFk;
    
    public boolean isActivo(){
        return idEstadoFk != null && idEstadoFk == 1L;
    }
}

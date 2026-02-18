package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "SECCIONMATERIA_TB")
public class SeccionMateria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SECCIONMATERIA")
    private Long idSeccionMateria;

    @Column(name = "ID_SECCION_FK")
    private Long idSeccionFk;

    @Column(name = "ID_MATERIA_FK")
    private Long idMateriaFk;

    @Column(name = "ID_USUARIO_DOCENTE_FK")
    private Long idUsuarioDocenteFk;

    @Column(name = "ID_ESTADO_FK")
    private Long idEstadoFk;

    public boolean isActiva() {
        return idEstadoFk != null && idEstadoFk == 1;
    }
}

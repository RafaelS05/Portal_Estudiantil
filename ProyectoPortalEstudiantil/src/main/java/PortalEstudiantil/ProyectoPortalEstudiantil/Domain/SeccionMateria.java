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

    // Reemplazás el Long por la relación completa
    @ManyToOne
    @JoinColumn(name = "ID_SECCION_FK")
    private Seccion seccion;

    @ManyToOne
    @JoinColumn(name = "ID_MATERIA_FK")
    private Materia materia;

    @Column(name = "ID_USUARIO_DOCENTE_FK")
    private Long idUsuarioDocenteFk;

    @Column(name = "ID_ESTADO_FK")
    private Long idEstadoFk;

    public boolean isActiva() {
        return idEstadoFk != null && idEstadoFk == 1;
    }

    @Transient
    public String getNombreCompleto() {
        String seccionNombre = (seccion != null) ? seccion.getNumero() : "Sin sección";
        String materiaNombre = (materia != null) ? materia.getNombre() : "Sin materia";
        return seccionNombre + " - " + materiaNombre;
    }

    // Getters de compatibilidad por si algo usa los IDs directamente
    public Long getIdSeccionFk() {
        return seccion != null ? seccion.getIdSeccion() : null;
    }

    public Long getIdMateriaFk() {
        return materia != null ? materia.getIdMateria() : null;
    }
}
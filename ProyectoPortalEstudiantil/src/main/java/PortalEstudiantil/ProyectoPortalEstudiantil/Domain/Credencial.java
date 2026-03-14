package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "CREDENCIALES_TB")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Credencial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CREDENCIAL")
    private Long idCredencial;

    @Column(name = "PASSWORD_HASH", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "ULTIMO_LOGIN")
    private LocalDateTime ultimoLogin;

    @Column(name = "INTENTOS_FALLIDOS", nullable = false)
    private Integer intentosFallidos = 0;

    @Column(name = "BLOQUEADO_HASTA")
    private LocalDateTime bloqueadoHasta;

    @Column(name = "ID_USUARIO_FK", nullable = false)
    private Long idUsuarioFk;

    @Column(name = "ID_CORREO_FK", nullable = false)
    private Long idCorreoFk;

    @Column(name = "ID_ESTADO_FK", nullable = false)
    private Long idEstadoFk;

    @Column(name = "FECHA_CREACION", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHA_MODIFICACION")
    private LocalDateTime fechaModificacion;

    @Column(name = "CREADO_POR", length = 100)
    private String creadoPor;

    @Column(name = "MODIFICADO_POR", length = 100)
    private String modificadoPor;

    @Column(name = "ACCION", length = 100)
    private String accion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO_FK", insertable = false, updatable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CORREO_FK", insertable = false, updatable = false)
    private Correo correo;
}
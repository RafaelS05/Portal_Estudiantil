package PortalEstudiantil.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "RESET_PASSWORD_TB")
public class ResetPassword {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reset_seq")
    @SequenceGenerator(name = "reset_seq", sequenceName = "RESET_PASSWORD_SEQ", allocationSize = 1)
    @Column(name = "ID_RESET")
    private long id_reset;
    
    @Column(name = "TOKEN", nullable = false, unique = true, length = 255)
    private String token;
    
    @Column(name = "FECHA_CREACION_TOKEN", nullable = false)
    private LocalDateTime fecha_Creacion_Token;
    
    @Column(name = "FECHA_EXPIRACION", nullable = false)
    private LocalDateTime fecha_Expiracion;
    
    @Column(name = "FECHA_USO")
    private LocalDateTime fecha_Uso;
    
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "ID_CREDENCIAL_FK", nullable = false)
//    private Credencial credencial;
//    
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "ID_ESTADO_FK", nullable = false)
//    private Estado estado;
    
    
}

package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Correo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CorreoRepository extends JpaRepository<Correo, Long> {
    
    // ==================== LECTURA ====================
    
    Correo findByUsuario_IdUsuarioAndEsLogin(Long idUsuario, String esLogin);
    
    
    @Query(value = "SELECT * FROM CORREO_TB WHERE CORREO = :correo AND ID_ESTADO_FK = 1 LIMIT 1", 
           nativeQuery = true)
    Correo findByCorreo(@Param("correo") String correo);
    
    // ==================== INSERTAR ====================
    
    @Modifying(clearAutomatically = true)   
    @Transactional
    @Query(value = """
        CALL CORREO_INSERTAR(
            :correo,
            :esLogin,
            :idUsuario,
            :idEstado
        )
    """, nativeQuery = true)
    void insertarCorreo(
            @Param("correo") String correo,
            @Param("esLogin") String esLogin,
            @Param("idUsuario") Long idUsuario,
            @Param("idEstado") Long idEstado
    );
    
    // ==================== MODIFICAR ====================
    
    @Modifying(clearAutomatically = true)   
    @Transactional
    @Query(value = """
        CALL CORREO_MODIFICAR(
            :idCorreo,
            :correo,
            :esLogin,
            :idUsuario
        )
    """, nativeQuery = true)
    void modificarCorreo(
            @Param("idCorreo") Long idCorreo,
            @Param("correo") String correo,
            @Param("esLogin") String esLogin,
            @Param("idUsuario") Long idUsuario
    );
    
}
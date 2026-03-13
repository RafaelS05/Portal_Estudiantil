package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Credencial;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CredencialRepository extends JpaRepository<Credencial, Long> {

    
      //Buscar credencial por ID de usuario
     
    Credencial findByIdUsuarioFk(Long idUsuario);

    
     //Buscar credencial por ID de correo
     
    Credencial findByIdCorreoFk(Long idCorreo);

    
     //Insertar una nueva credencial
     
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = """
        INSERT INTO CREDENCIALES_TB (
            PASSWORD_HASH,
            INTENTOS_FALLIDOS,
            ID_USUARIO_FK,
            ID_CORREO_FK,
            ID_ESTADO_FK,
            CREADO_POR
        ) VALUES (
            :passwordHash,
            0,
            :idUsuario,
            :idCorreo,
            :idEstado,
            :creadoPor
        )
    """, nativeQuery = true)
    void insertarCredencial(
            @Param("passwordHash") String passwordHash,
            @Param("idUsuario") Long idUsuario,
            @Param("idCorreo") Long idCorreo,
            @Param("idEstado") Long idEstado,
            @Param("creadoPor") String creadoPor
    );
    
    //Verificar si ya existe credencial para un usuario
    
    @Query("SELECT COUNT(c) > 0 FROM Credencial c WHERE c.idUsuarioFk = :idUsuario")
    boolean existsByIdUsuarioFk(@Param("idUsuario") Long idUsuario);

    
//     Verificar si ya existe credencial para un correo
     
    @Query("SELECT COUNT(c) > 0 FROM Credencial c WHERE c.idCorreoFk = :idCorreo")
    boolean existsByIdCorreoFk(@Param("idCorreo") Long idCorreo);
}
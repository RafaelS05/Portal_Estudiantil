/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findByIdUsuario(Long idUsuario);
    
    List<Usuario> findByNombreContaining(String nombre);
   
    List<Usuario> findByIdTipoUsuarioFk(Long idTipoUsuarioFk);
    
    List<Usuario> findByIdEstadoFk(Long idEstadoFk);
    
    @Query("SELECT u FROM Usuario u WHERE CONCAT(u.nombre, ' ', u.primerApellido, ' ', u.segundoApellido) LIKE %:nombreCompleto%")
    List<Usuario> findByNombreCompletoContaining(@Param("nombreCompleto") String nombreCompleto);

    @Modifying
    @Transactional
    @Query(value = """
        CALL USUARIOS_INSERTAR(
            :nombre,
            :primerApellido,
            :segundoApellido,
            :idTipoUsuarioFk,
            :idEstadoFk
        )
    """, nativeQuery = true)
    void insertarUsuario(
        @Param("nombre") String nombre,
        @Param("primerApellido") String primerApellido,
        @Param("segundoApellido") String segundoApellido,
        @Param("idTipoUsuarioFk") Long idTipoUsuarioFk,
        @Param("idEstadoFk") Long idEstadoFk
    );

    @Modifying
    @Transactional
    @Query(value = """
        CALL USUARIOS_MODIFICAR(
            :idUsuario,
            :nombre,
            :primerApellido,
            :segundoApellido,
            :idTipoUsuarioFk
        )
    """, nativeQuery = true)
    void modificarUsuario(
        @Param("idUsuario") Long idUsuario,
        @Param("nombre") String nombre,
        @Param("primerApellido") String primerApellido,
        @Param("segundoApellido") String segundoApellido,
        @Param("idTipoUsuarioFk") Long idTipoUsuarioFk
    );

    @Modifying
    @Transactional
    @Query(value = """
        CALL USUARIOS_CAMBIAR_ESTADO(
            :idUsuario,
            :idEstado
        )
    """, nativeQuery = true)
    void cambiarEstadoUsuario(
        @Param("idUsuario") Long idUsuario,
        @Param("idEstado") Long idEstado
    );
}
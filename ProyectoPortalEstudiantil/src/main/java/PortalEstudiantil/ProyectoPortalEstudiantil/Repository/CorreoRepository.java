/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Correo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CorreoRepository extends JpaRepository<Correo, Long> {

    // =====================
    // LECTURA
    // =====================
    Correo findByUsuario_IdUsuarioAndEsLogin(Long idUsuario, String esLogin);

    // =====================
    // MODIFICAR
    // =====================
    @Modifying
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
